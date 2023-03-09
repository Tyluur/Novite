package novite.rs.game.npc.dragons;

import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;

public class KingBlackDragon extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = -5972729887400254289L;

	public KingBlackDragon(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(0);
	}

	public static boolean atKBD(WorldTile tile) {
		if ((tile.getX() >= 2250 && tile.getX() <= 2292) && (tile.getY() >= 4675 && tile.getY() <= 4710)) {
			return true;
		}
		return false;
	}

}
