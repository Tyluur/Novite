package novite.rs.game.npc.others;

import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.utility.Utils;

public class AbyssalDemon extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = -3454133323494579077L;

	public AbyssalDemon(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		Entity target = getCombat().getTarget();
		if (target != null && Utils.isInRange(target.getX(), target.getY(), target.getSize(), getX(), getY(), getSize(), 4) && Utils.random(50) == 0) {
			sendTeleport(Utils.random(2) == 0 ? target : this);
		}
	}

	private void sendTeleport(Entity entity) {
		int entitySize = entity.getSize();
		for (int c = 0; c < 10; c++) {
			int dir = Utils.random(Utils.DIRECTION_DELTA_X.length);
			if (World.checkWalkStep(entity.getPlane(), entity.getX(), entity.getY(), dir, entitySize)) {
				entity.setNextGraphics(new Graphics(409));
				entity.setNextWorldTile(new WorldTile(getX() + Utils.DIRECTION_DELTA_X[dir], getY() + Utils.DIRECTION_DELTA_Y[dir], getPlane()));
				break;
			}
		}
	}
}
