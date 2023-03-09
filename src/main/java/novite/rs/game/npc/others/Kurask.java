package novite.rs.game.npc.others;

import novite.rs.game.Hit;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.actions.PlayerCombat;

public class Kurask extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = 3299760720763083112L;

	public Kurask(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (hit.getSource() instanceof Player) {
			Player player = (Player) hit.getSource();
			if (!(player.getEquipment().getWeaponId() == 13290 || player.getEquipment().getWeaponId() == 4158) && !(PlayerCombat.isRanging(player) == 2 && (player.getEquipment().getAmmoId() == 13280 || player.getEquipment().getAmmoId() == 4160))) {
				hit.setDamage(0);
			}
		}
		super.handleIngoingHit(hit);
	}
}
