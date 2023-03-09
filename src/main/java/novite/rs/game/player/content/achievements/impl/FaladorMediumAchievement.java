package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class FaladorMediumAchievement extends Achievement {

	public FaladorMediumAchievement() {
		super(Types.MEDIUM, "Mine @TOTAL@ Coal Ore");
	}

	@Override
	public String getRewardInfo() {
		return "Falador Shield & 500k Cash";
	}

	@Override
	public void giveReward(Player player) {
		addItem(player, new Item(19749, 1), new Item(995, 500000));
	}

	@Override
	public int getTotalAmount() {
		return 300;
	}

	@Override
	public String getKey() {
		return "coal_falador_mine";
	}

	private static final long serialVersionUID = -3552572586935690711L;

}
