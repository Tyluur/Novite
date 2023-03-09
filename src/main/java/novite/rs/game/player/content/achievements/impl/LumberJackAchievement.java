package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 27, 2014
 */
public class LumberJackAchievement extends Achievement {

	public LumberJackAchievement() {
		super(Types.MEDIUM, "Chop down @TOTAL@ trees in Novite");
	}

	@Override
	public String getRewardInfo() {
		return "Lumber Jack Set & 2x Achievement Points";
	}

	@Override
	public void giveReward(Player player) {
		addAchievementPoints(player, 2);
		addItem(player, new Item(10933), new Item(10939), new Item(10940), new Item(10941));
	}

	@Override
	public int getTotalAmount() {
		return 1000;
	}

	@Override
	public String getKey() {
		return "lumber_jack_trees";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7841128125345862439L;
}
