package novite.rs.game.npc.slayer;

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
	private static final long serialVersionUID = 5306673077106151605L;
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
					npc.setAttackedBy(player);
				} else {
					player.getPackets().sendGameMessage("That npc is already in combat.");
					return;
				}
			}
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
