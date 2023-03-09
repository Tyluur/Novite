package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class BarbarianMediumAchievement extends Achievement {

	public BarbarianMediumAchievement() {
		super(Types.MEDIUM, "Complete @TOTAL@ Barbarian Agility Courses");
	}

	@Override
	public String getRewardInfo() {
		return "3x Achievement Points";
	}

	@Override
	public void giveReward(Player player) {
		addAchievementPoints(player, 3);
	}

	@Override
	public int getTotalAmount() {
		return 25;
	}

	@Override
	public String getKey() {
		return "barbarian_courses_complete";
	}

	private static final long serialVersionUID = 2500461318596441078L;

}
