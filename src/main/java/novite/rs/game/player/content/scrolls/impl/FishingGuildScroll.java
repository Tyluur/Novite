package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 22, 2014
 */
public class FishingGuildScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2141696125605881886L;

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
		return RASPBERRY;
	}

	@Override
	public String[] getHints() {
		return new String[] { "Blow a raspberry in the Fishing Guild bank.", "Beware of double agents!", "Equip an elemental shield, blue dragonhide chaps, and rune warhammer." };
	}

	@Override
	public Boolean completePrequisites(Player player) {
		return player.getEquipment().getShieldId() == 2890 && player.getEquipment().getLegsId() == 2493 && player.getEquipment().getWeaponId() == 1347;
	}

	@Override
	public WorldTile getActionTile() {
		return new WorldTile(2586, 3422, 0);
	}

}
