package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class GnomeEasyAchievement extends Achievement {

	public GnomeEasyAchievement() {
		super(Types.EASY, "Complete @TOTAL@ Gnome Agility Courses");
	}

	@Override
	public String getRewardInfo() {
		return "1x Achievement Points";
	}

	@Override
	public void giveReward(Player player) {
		addAchievementPoints(player, 1);
	}

	@Override
	public int getTotalAmount() {
		return 15;
	}

	@Override
	public String getKey() {
		return "agility_courses_complete";
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 9141136434925405898L;
}
