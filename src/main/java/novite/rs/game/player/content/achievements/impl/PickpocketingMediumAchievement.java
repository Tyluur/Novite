package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 10, 2014
 */
public class PickpocketingMediumAchievement extends Achievement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3222197034483550398L;

	public PickpocketingMediumAchievement() {
		super(Types.MEDIUM, "Successfully steal from edgeville guards @TOTAL@ times");
	}

	@Override
	public String getRewardInfo() {
		return "Rogues Armour & 2x Achievement Points";
	}

	@Override
	public void giveReward(Player player) {
		addAchievementPoints(player, 2);
		addItem(player, new Item(5553), new Item(5554), new Item(5555), new Item(5556), new Item(5557));
	}

	@Override
	public int getTotalAmount() {
		return 100;
	}

	@Override
	public String getKey() {
		return "edgeville_guards_steal";
	}

}
