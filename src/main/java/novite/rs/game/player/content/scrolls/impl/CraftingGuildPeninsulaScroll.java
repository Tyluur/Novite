package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 22, 2014
 */
public class CraftingGuildPeninsulaScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5835905404780151840L;

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Integer getInformationInterface() {
		return 352;
	}

	@Override
	public Integer getAnimation() {
		return null;
	}

	@Override
	public String[] getHints() {
		return null;
	}

	@Override
	public Boolean completePrequisites(Player player) {
		return null;
	}

	@Override
	public WorldTile getActionTile() {
		return new WorldTile(2906, 3294, 0);
	}

}
