package novite.rs.game.npc.others;

import novite.rs.game.Entity;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;

public class RockCrabs extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = -4621253301902072996L;
	private int realId;

	public RockCrabs(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		realId = id;
		setForceAgressive(true); //to ignore combat lvl
	}

	@Override
	public void setTarget(Entity entity) {
		if (realId == getId()) {
			this.transformInto(realId - 1);
			setHitpoints(getMaxHitpoints()); //rock/bulders have no hp
		}
		super.setTarget(entity);
	}

	@Override
	public void reset() {
		setNPC(realId);
		super.reset();
	}

}
