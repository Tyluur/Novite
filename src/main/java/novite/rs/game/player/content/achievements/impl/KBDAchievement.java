package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class KBDAchievement extends Achievement {

	public KBDAchievement() {
		super(Types.HARD, "Kill @TOTAL@ king black dragons");
	}

	@Override
	public String getRewardInfo() {
		return "5x Achievement Points";
	}

	@Override
	public void giveReward(Player player) {
		addAchievementPoints(player, 5);
	}

	@Override
	public int getTotalAmount() {
		return 20;
	}

	@Override
	public String getKey() {
		return "kbds_killed";
	}

	private static final long serialVersionUID = 6986329359578958430L;
}
