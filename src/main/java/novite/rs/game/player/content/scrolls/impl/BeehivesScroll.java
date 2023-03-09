package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 22, 2014
 */
public class BeehivesScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2590370731213004737L;

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
		return JUMP_FOR_JOY;
	}

	@Override
	public String[] getHints() {
		return new String[] { "Jump for joy at the beehives.", "Equip iron boots, an unholy symbol, and a steel hatchet." };
	}

	@Override
	public Boolean completePrequisites(Player player) {
		return player.getEquipment().getBootsId() == 4121 && player.getEquipment().getAmuletId() == 1724 && player.getEquipment().getWeaponId() == 1353;
	}

	@Override
	public WorldTile getActionTile() {
		return new WorldTile(2757, 3445, 0);
	}

}
