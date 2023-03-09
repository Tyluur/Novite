package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 22, 2014
 */
public class AlkharidMineScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3027081908371997600L;

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
		return HEADBANG;
	}

	@Override
	public String[] getHints() {
		return new String[] { "Headbang in the mine north of Al Kharid.", "Equip a black d'hide body, leather gloves, and leather boots." };
	}

	@Override
	public Boolean completePrequisites(Player player) {
		return player.getEquipment().getChestId() == 2503 && player.getEquipment().getGlovesId() == 1059 && player.getEquipment().getBootsId() == 1061;
	}

	@Override
	public WorldTile getActionTile() {
		return new WorldTile(3298, 3287, 0);
	}

}
