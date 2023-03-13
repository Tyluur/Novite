package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 22, 2014
 */
public class PartyroomScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2577295574747991076L;

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
		return DANCE;
	}

	@Override
	public String[] getHints() {
		return new String[] { "Dance in the Party Room.", "Equip a steel full helmet, steel platebody and an iron plateskirt." };
	}

	@Override
	public Boolean completePrequisites(Player player) {
		return player.getEquipment().getHatId() == 1157 && player.getEquipment().getChestId() == 1119 && player.getEquipment().getLegsId() == 1081;
	}

	@Override
	public WorldTile getActionTile() {
		return new WorldTile(3045, 3376, 0);
	}

}
