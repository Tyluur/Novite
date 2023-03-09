package novite.rs.game.npc.others;

import novite.rs.game.Entity;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;

public class Jadinko extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = 4434769545744287829L;

	public Jadinko(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		if (source instanceof Player) {
			Player player = (Player) source;
			player.setFavorPoints((getId() == 13820 ? 3 : getId() == 13821 ? 7 : 10) + player.getFavorPoints());
		}
	}
}
