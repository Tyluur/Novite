package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 22, 2014
 */
public class RoguesStoreScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2220197602060900738L;

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
		return YAWN;
	}

	@Override
	public String[] getHints() {
		return new String[] { "Yawn in the rogues' general store. Beware of double agents!", "Equip an iron kiteshield, blue dragon vambraces and an iron pickaxe." };
	}

	@Override
	public Boolean completePrequisites(Player player) {
		return player.getEquipment().getShieldId() == 1191 && player.getEquipment().getGlovesId() == 2487 && player.getEquipment().getWeaponId() == 1267;
	}

	@Override
	public WorldTile getActionTile() {
		return new WorldTile(3025, 3701, 0);
	}

}
