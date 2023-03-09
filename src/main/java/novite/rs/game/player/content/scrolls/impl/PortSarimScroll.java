package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 22, 2014
 */
public class PortSarimScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4784407382669369906L;

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
		return new String[] { "Cheer for the monks at Port Sarim.", "Equip a coif, steel plateskirt, and a sapphire necklace." };
	}

	@Override
	public Boolean completePrequisites(Player player) {
		return player.getEquipment().getHatId() == 1169 && player.getEquipment().getLegsId() == 1083 && player.getEquipment().getAmuletId() == 1656;
	}

	@Override
	public WorldTile getActionTile() {
		return new WorldTile(3048, 3234, 0);
	}

}
