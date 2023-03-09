package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class MagicsMediumAchievement extends Achievement {

	public MagicsMediumAchievement() {
		super(Types.MEDIUM, "Cut @TOTAL@ magic trees");
	}

	@Override
	public String getRewardInfo() {
		return "300K Cash & 3x Achievement Points";
	}

	@Override
	public void giveReward(Player player) {
		addItem(player, new Item(995, 300000));
		addAchievementPoints(player, 3);
	}

	@Override
	public int getTotalAmount() {
		return 75;
	}

	@Override
	public String getKey() {
		return "magic_trees_cut";
	}

	private static final long serialVersionUID = -7223265835965576807L;

}
