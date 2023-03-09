package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 22, 2014
 */
public class ZamorakTempleScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6321347822921965946L;

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
		return SHRUG;
	}

	@Override
	public String[] getHints() {
		return new String[] { "Shrug in the Zamorak Temple found in the Eastern Wilderness. Beware of double agents!", "Equip bronze platelegs, an iron plate body, and blue dragonhide vambraces." };
	}

	@Override
	public Boolean completePrequisites(Player player) {
		return player.getEquipment().getLegsId() == 1075 && player.getEquipment().getChestId() == 1115 && player.getEquipment().getGlovesId() == 2487;
	}

	@Override
	public WorldTile getActionTile() {
		return new WorldTile(3240, 3609, 0);
	}

}
