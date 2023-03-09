package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 22, 2014
 */
public class FishingEntranceScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3259039665555025633L;

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
		return JIG;
	}

	@Override
	public String[] getHints() {
		return new String[] { "Dance a jig by the entrance to the fishing guild.", "Equip an emerald ring, a sapphire amulet, and a bronze chain body." };
	}

	@Override
	public Boolean completePrequisites(Player player) {
		return player.getEquipment().getRingId() == 1639 && player.getEquipment().getAmuletId() == 1694 && player.getEquipment().getChestId() == 1103;
	}

	@Override
	public WorldTile getActionTile() {
		return new WorldTile(2613, 3386, 0);
	}

}
