package novite.rs.game.npc.others;

import novite.rs.game.Animation;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class Strykewyrm extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = 3376189652470482334L;
	private int stompId;

	public Strykewyrm(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
		stompId = id;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead()) {
			return;
		}
		if (getId() != stompId && !isCantInteract() && !isUnderCombat()) {
			setNextAnimation(new Animation(12796));
			setCantInteract(true);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					transformInto(stompId);
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							setCantInteract(false);
						}
					});
				}
			});
		}
	}

	@Override
	public void reset() {
		setNPC(stompId);
		super.reset();
	}

	public int getStompId() {
		return stompId;
	}

	public static void handleStomping(final Player player, final NPC npc) {
		if (npc.isCantInteract()) {
			return;
		}
		if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
			if (player.getAttackedBy() != npc && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
				player.getPackets().sendGameMessage("You are already in combat.");
				return;
			}
			if (npc.getAttackedBy() != player && npc.getAttackedByDelay() > Utils.currentTimeMillis()) {
				if (npc.getAttackedBy() instanceof NPC) {
					npc.setAttackedBy(player); // changes enemy to player,
					// player has priority over
					// npc on single areas
				} else {
					player.getPackets().sendGameMessage("That npc is already in combat.");
					return;
				}
			}
		}
		switch (npc.getId()) {
			case 9462:
				if (player.getSkills().getLevel(18) < 93) {
					player.getPackets().sendGameMessage("You need at least a slayer level of 93 to fight this.");
					return;
				}
				break;
			case 9464:
				if (player.getSkills().getLevel(18) < 77) {
					player.getPackets().sendGameMessage("You need at least a slayer level of 77 to fight this.");
					return;
				}
				break;
			case 9466:
				if (player.getSkills().getLevel(18) < 73) {
					player.getPackets().sendGameMessage("You need at least a slayer level of 73 to fight this.");
					return;
				}
				break;
			default:
				return;
		}
		player.setNextAnimation(new Animation(4278));
		player.lock(2);
		npc.setCantInteract(true);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				npc.setNextAnimation(new Animation(12795));
				npc.transformInto(((Strykewyrm) npc).stompId + 1);
				stop();
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						npc.setTarget(player);
						npc.setAttackedBy(player);
						npc.setCantInteract(false);
					}
				});
			}

		}, 1);
	}

}
