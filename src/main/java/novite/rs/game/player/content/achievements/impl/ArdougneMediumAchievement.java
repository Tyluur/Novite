package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class ArdougneMediumAchievement extends Achievement {

	public ArdougneMediumAchievement() {
		super(Types.MEDIUM, "Steal from the any of the ardougne stalls @TOTAL@ times");
	}

	@Override
	public String getRewardInfo() {
		return "Ardougne Cloak (4) & 500k Cash";
	}

	@Override
	public void giveReward(Player player) {
		addItem(player, new Item(19748), new Item(995, 500000));
	}

	@Override
	public int getTotalAmount() {
		return 50;
	}

	@Override
	public String getKey() {
		return "ardgoune_stall_thieve";
	}

	private static final long serialVersionUID = 2655332977166866653L;

}
