package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 10, 2014
 */
public class WildernessAgilityHardAchievement extends Achievement {

	public WildernessAgilityHardAchievement() {
		super(Types.HARD, "Complete @TOTAL@ Wilderness Agility Courses");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5608608654009265564L;

	@Override
	public String getRewardInfo() {
		return "Agile Armour Set & 3x Achievement Points";
	}

	@Override
	public void giveReward(Player player) {
		addItem(player, new Item(14936), new Item(14938));
		addAchievementPoints(player, 3);
	}

	@Override
	public int getTotalAmount() {
		return 120;
	}

	@Override
	public String getKey() {
		return "wilderness_agility";
	}

}
