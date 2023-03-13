package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 21, 2014
 */
public class ChampionsGuildScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2134850771133747556L;

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Integer getInformationInterface() {
		return 346;
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
		return new WorldTile(3166, 3360, 0);
	}

}
