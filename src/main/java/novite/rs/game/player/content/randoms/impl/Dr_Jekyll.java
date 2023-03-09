package novite.rs.game.player.content.randoms.impl;

import java.util.concurrent.TimeUnit;

import novite.rs.game.Animation;
import novite.rs.game.ForceTalk;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.others.Follower;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.randoms.RandomEvent;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

/**
 * 
 * @author Tyluur <itstyluur@gmail.com>
 * @since Nov 26, 2013
 */
public class Dr_Jekyll extends RandomEvent {

	@Override
	public String getName() {
		return "Dr_Jekyll";
	}

	@Override
	public void initiate(Player player) {
		gaveReward = false;
		setNpc(new Follower(npcId, player, player.getDisplayName()));
		getNpc().setNextForceTalk(new ForceTalk(player.getDisplayName() + "! How're you doing? I need to talk to you for a moment."));
		getNpc().getTemporaryAttributtes().put("gave_reward", Boolean.FALSE);
		getNpc().getTemporaryAttributtes().put("spawned_time", Utils.currentTimeMillis());
	}

	@Override
	public void process(Player player) {
		if (getNpc() != null) {
			try {
				interacted = getNpc().getTemporaryAttributtes().get("gave_reward") != null && getNpc().getTemporaryAttributtes().get("gave_reward") == Boolean.TRUE;
				long startTime = (long) getNpc().getTemporaryAttributtes().get("spawned_time");
				if (!interacted) {
					long seconds = TimeUnit.MILLISECONDS.toSeconds(Utils.currentTimeMillis() - startTime);
					if (seconds >= 60) {
						player.getDialogueManager().startDialogue("SimpleMessage", "Pay attention next time you have a random event!");
						dispose(player);
						return;
					}
					final String name = getNpc().getTarget();
					if (seconds == 10) {
						getNpc().setNextForceTalk(new ForceTalk(name + "! Could I have your attention for a moment?"));
					} else if (seconds == 20) {
						getNpc().setNextForceTalk(new ForceTalk("I really need you for a brief moment, " + name + ". It is best we converse."));
					} else if (seconds == 30) {
						getNpc().setNextForceTalk(new ForceTalk("It seems like you're ignoring me, " + name + ". TALK TO ME!"));
					} else if (seconds == 50) {
						getNpc().setNextForceTalk(new ForceTalk(name + ", this is your last chance. Converse with me or face the consequences."));
					}
				} else {
					getNpc().finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void dispose(Player player) {
		super.dispose(player);
		interacted = getNpc().getTemporaryAttributtes().get("gave_reward") != null && getNpc().getTemporaryAttributtes().get("gave_reward") == Boolean.TRUE;
		if (getNpc() != null) {
			if (!interacted) {
				player.setNextWorldTile(getRandomPosition());
				if (!interacted) {
					if (!World.containsPlayer(player.getUsername())) {
						getNpc().setNextForceTalk(new ForceTalk("Where did they go???"));
					} else {
						getNpc().setNextForceTalk(new ForceTalk("That should teach them..."));
					}
					getNpc().setNextAnimation(new Animation(857));
				}
			}
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					getNpc().finish();
					stop();
				}
			}, 2);
		}
	}

	@Override
	public boolean handleNPCInteraction(final Player player, NPC npc) {
		switch (npc.getId()) {
		case npcId:
			if (npc instanceof Follower) {
				final Follower follower = (Follower) npc;
				if (follower.getTarget().equals(player.getDisplayName())) {
					if (!interacted) {
						if (!gaveReward) {
							follower.setNextForceTalk(new ForceTalk("Thank you for talking to me! Enjoy your reward!"));
							follower.setNextAnimation(new Animation(863));
							player.getFacade().setLoyaltyPoints(player.getFacade().getLoyaltyPoints() + REWARD_POINTS);
							player.getDialogueManager().startDialogue("SimpleMessage", "You gain " + REWARD_POINTS + " loyalty points for talking to " + follower.getName() + " on time.", "Exchange these points in the loyalty point shop.");
							gaveReward = true;
						}
						WorldTasksManager.schedule(new WorldTask() {

							@Override
							public void run() {
								follower.getTemporaryAttributtes().put("gave_reward", Boolean.TRUE);
								dispose(player);
							}
						}, 2);
					} else {
						player.sendMessage("You have already spoken to him!");
					}
				} else {
					player.sendMessage("This random event is not for you.");
				}
			}
			return true;
		}
		return false;
	}

	public Follower getNpc() {
		return npc;
	}

	public void setNpc(Follower npc) {
		this.npc = npc;
	}

	/**
	 * The npc object
	 */
	private Follower npc;

	/**
	 * The id of the npc
	 */
	private final int npcId = 8643;

	/**
	 * If the player has interacted with the npc
	 */
	private boolean interacted;

	/**
	 * If the npc gave the player the reward
	 */
	private boolean gaveReward;

	/**
	 * The amount of loyalty reward points you get
	 */
	private static final int REWARD_POINTS = 150;

	@Override
	public boolean handleInterfaceInteraction(Player player, int interfaceId, int buttonId) {
		return false;
	}

}
