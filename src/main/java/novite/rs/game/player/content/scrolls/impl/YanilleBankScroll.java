package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 22, 2014
 */
public class YanilleBankScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1128536478267935706L;

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
		return new String[] { "Jump for joy in Yanille bank.", "Equip a iron crossbow, adamant medium helmet, and snakeskin chaps." };
	}

	@Override
	public Boolean completePrequisites(Player player) {
		return player.getEquipment().getWeaponId() == 9177 && player.getEquipment().getHatId() == 1145 && player.getEquipment().getLegsId() == 6324;
	}

	@Override
	public WorldTile getActionTile() {
		return new WorldTile(2611, 3091, 0);
	}

}
