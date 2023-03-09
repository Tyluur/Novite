package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 22, 2014
 */
public class DuelArenaOfficeScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1768145815330434824L;

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
		return BOW;
	}

	@Override
	public String[] getHints() {
		return new String[] { "Bow or curtsy in the ticket office of the Duel Arena.", "Equip an iron chain body, leather chaps and a coif." };
	}

	@Override
	public Boolean completePrequisites(Player player) {
		return player.getEquipment().getLegsId() == 1095 && player.getEquipment().getHatId() == 1169 && player.getEquipment().getChestId() == 1101;
	}

	@Override
	public WorldTile getActionTile() {
		return new WorldTile(3314, 3241, 0);
	}

}
