package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 22, 2014
 */
public class BarbarianAgilityScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7376835657448477617L;

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
		return CHEER;
	}

	@Override
	public String[] getHints() {
		return new String[] { "Cheer in the Barbarian Agility Arena.", "Equip a steel plate body, maple shortbow, and bronze boots." };
	}

	@Override
	public Boolean completePrequisites(Player player) {
		return player.getEquipment().getChestId() == 1119 && player.getEquipment().getWeaponId() == 853 && player.getEquipment().getBootsId() == 4119;
	}

	@Override
	public WorldTile getActionTile() {
		return new WorldTile(2547, 3554, 0);
	}

}
