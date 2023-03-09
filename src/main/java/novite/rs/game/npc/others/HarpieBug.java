package novite.rs.game.npc.others;

import novite.rs.game.Hit;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;

public class HarpieBug extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = -1030776397095407860L;

	public HarpieBug(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (hit.getSource() instanceof Player) {
			Player player = (Player) hit.getSource();
			if (player.getEquipment().getShieldId() != 7053) {
				hit.setDamage(0);
			}
		}
		super.handleIngoingHit(hit);
	}

}
