package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 22, 2014
 */
public class RoguesCastleScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8475796210383944577L;

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
		return new String[] { "Shrug in the Rogue's Castle found deep in the North Eastern Wilderness. Beware of double agents!", "Equip iron platelegs, dragon scimitar, and climbing boots." };
	}

	@Override
	public Boolean completePrequisites(Player player) {
		return player.getEquipment().getLegsId() == 1067 && player.getEquipment().getWeaponId() == 4587 && player.getEquipment().getBootsId() == 3105;
	}

	@Override
	public WorldTile getActionTile() {
		return new WorldTile(3294, 3933, 0);
	}

}
