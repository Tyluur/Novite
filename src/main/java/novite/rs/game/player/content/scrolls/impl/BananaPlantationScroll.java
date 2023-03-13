package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 22, 2014
 */
public class BananaPlantationScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4626003099015466360L;

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Integer getInformationInterface() {
		return null;
	}

	@Override
	public Integer getAnimation() {
		return SALUTE;
	}

	@Override
	public String[] getHints() {
		return new String[] { "Salute in the banana plantation. Beware of double agents!", "Equip a diamond ring, amulet of power, and nothing on your chest and legs." };
	}

	@Override
	public Boolean completePrequisites(Player player) {
		return player.getEquipment().getChestId() == -1 && player.getEquipment().getLegsId() == -1 && player.getEquipment().getRingId() == 1643 && player.getEquipment().getAmuletId() == 1731;
	}

	@Override
	public WorldTile getActionTile() {
		return new WorldTile(2917, 3166, 0);
	}

}
