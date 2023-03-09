package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class VarrockEasyDitchAchievement extends Achievement {

	public VarrockEasyDitchAchievement() {
		super(Types.EASY, "Cross the wilderness ditch in varrock @TOTAL@x");
	}

	@Override
	public String getRewardInfo() {
		return "Varrock Armour";
	}

	@Override
	public void giveReward(Player player) {
		addItem(player, new Item(19757));
	}

	@Override
	public int getTotalAmount() {
		return 5;
	}

	@Override
	public String getKey() {
		return "varrock_ditch_cross";
	}

	private static final long serialVersionUID = -6565688482758144780L;
}
