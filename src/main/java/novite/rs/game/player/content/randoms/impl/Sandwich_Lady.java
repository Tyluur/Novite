package novite.rs.game.player.content.randoms.impl;

import novite.rs.game.Animation;
import novite.rs.game.ForceTalk;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.others.SandwichLady;
import novite.rs.game.npc.others.SandwichLady.Sandwiches;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.randoms.RandomEvent;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.game.player.dialogues.SimpleNPCMessage;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 21, 2014
 */
public class Sandwich_Lady extends RandomEvent {

	@Override
	public String getName() {
		return "Sandwich Lady";
	}

	@Override
	public void initiate(Player player) {
		lady = new SandwichLady(SANDWICH_LADY_ID, player, player.getDisplayName());
		gaveReward = false;
	}

	@Override
	public void process(Player player) {
		/**
		 * The flag for timer running out will call dispose
		 */
		if (lady != null && lady.getAttributes().get("sandwich_timer_finished") != null) {
			dispose(player);
		}
	}

	@Override
	public boolean handleNPCInteraction(Player player, NPC npc) {
		switch (npc.getId()) {
		case SANDWICH_LADY_ID:
			if (npc instanceof SandwichLady) {
				final SandwichLady npcLady = (SandwichLady) npc;
				if (!npcLady.getTarget().equalsIgnoreCase(player.getDisplayName())) {
					player.getDialogueManager().startDialogue(SimpleNPCMessage.class, SANDWICH_LADY_ID, "I only want to talk to " + npcLady.getTarget() + "...");
					return false;
				}
				final int npcId = npc.getId();
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You look hungry to me. I tell you what -", "have a " + npcLady.getSandwich().getName().toLowerCase() + " on me.");
					}

					@Override
					public void run(int interfaceId, int option) {
						end();
						displayInterface(player);
					}

					@Override
					public void finish() {

					}
				});
				return true;
			}
			break;
		}
		return false;
	}

	/**
	 * Displays the sandwich picking interface
	 */
	public void displayInterface(Player player) {
		player.getInterfaceManager().sendInterface(SANDWICH_INTERFACE);
		player.getPackets().sendIComponentText(SANDWICH_INTERFACE, 48, "Have a " + lady.getSandwich().getName() + " for free!");
	}

	@Override
	public boolean handleInterfaceInteraction(final Player player, int interfaceId, int buttonId) {
		if (interfaceId == 297) {
			if (buttonId == 49 || buttonId == 47)
				return true;
			Sandwiches sandwich = null;
			for (Sandwiches sandwiches : Sandwiches.values()) {
				if (sandwiches.getButtonId() == buttonId) {
					sandwich = sandwiches;
					break;
				}
			}
			if (sandwich.ordinal() != lady.getSandwich().ordinal()) {
				player.closeInterfaces();
				player.getAttributes().put("failed_sandwich_lady", "type");
				dispose(player);
				return true;
			}
			if (!gaveReward) {
				player.getFacade().setLoyaltyPoints(player.getFacade().getLoyaltyPoints() + REWARD_POINTS);
				player.getDialogueManager().startDialogue("SimpleMessage", "You gain " + REWARD_POINTS + " loyalty points for talking to the sandwich lady", "Exchange these points in the loyalty point shop.");
				gaveReward = true;
			}
			
			player.closeInterfaces();
			lady.setNextForceTalk(new ForceTalk("Enjoy!"));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					lady.getAttributes().put("completed_existance", true);
				}
			}, 2);
			return true;
		}
		return false;
	}

	@Override
	public void dispose(final Player player) {
		super.dispose(player);
		if (player.getAttributes().get("failed_sandwich_lady") != null) {
			String type = (String) player.getAttributes().remove("failed_sandwich_lady");
			lady.setNextForceTalk(new ForceTalk(type == "type" ? "Hey! I didn't say you could have that!" : "Take that, " + player.getDisplayName()));
			lady.setNextAnimation(new Animation(12033));
			lady.setNextFaceEntity(player);
			player.sendMessage("The sandwich lady whacks you over the head with a baguette.");
			player.lock();
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					lady.getAttributes().put("completed_existance", true);
					player.setNextWorldTile(getRandomPosition());
					player.unlock();
				}
			}, 3);
		}
	}

	/**
	 * The sandwich lady instance
	 */
	private SandwichLady lady;

	/**
	 * If the npc gave the player the reward
	 */
	private boolean gaveReward = false;

	/**
	 * The id of the sandwich interface
	 */
	private static final int SANDWICH_INTERFACE = 297;

	/**
	 * The id of the sandwich lady
	 */
	private static final int SANDWICH_LADY_ID = 8629;

	/**
	 * The amount of loyalty reward points you get
	 */
	private static final int REWARD_POINTS = 150;

}
