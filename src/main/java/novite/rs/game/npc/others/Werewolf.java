package novite.rs.game.npc.others;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class Werewolf extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = -2622152146713279394L;
	private int realId;

	public Werewolf(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		realId = id;
	}

	public boolean hasWolfbane(Entity target) {
		if (target instanceof NPC) {
			return false;
		}
		return ((Player) target).getEquipment().getWeaponId() == 2952;
	}

	@Override
	public void processNPC() {
		if (isDead() || isCantInteract()) {
			return;
		}
		if (isUnderCombat() && getId() == realId && Utils.random(5) == 0) {
			final Entity target = getCombat().getTarget();
			if (!hasWolfbane(target)) {
				setNextAnimation(new Animation(6554));
				setCantInteract(true);
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						transformInto(realId - 20);
						setNextAnimation(new Animation(-1));
						setCantInteract(false);
						setTarget(target);
					}
				}, 1);
				return;
			}
		}
		super.processNPC();
	}

	@Override
	public void reset() {
		setNPC(realId);
		super.reset();
	}

}
