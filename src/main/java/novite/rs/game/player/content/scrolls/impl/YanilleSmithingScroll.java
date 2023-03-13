package novite.rs.game.player.content.scrolls.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.scrolls.ClueScroll;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 22, 2014
 */
public class YanilleSmithingScroll extends ClueScroll {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1885013107452122762L;

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Integer getInformationInterface() {
		return 353;
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
		return new WorldTile(2616, 3077, 0);
	}

}
