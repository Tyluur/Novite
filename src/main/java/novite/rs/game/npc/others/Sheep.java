package novite.rs.game.npc.others;

import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;

public class Sheep extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = -7865762223287777845L;
	int ticks, origonalId;

	public Sheep(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		origonalId = id;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (getId() != origonalId) {
			if (ticks++ == 60) {
				transformInto(origonalId);
				ticks = 0;
			}
		}
	}
}
