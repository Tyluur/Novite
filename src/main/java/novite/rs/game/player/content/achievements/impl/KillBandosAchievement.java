package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class KillBandosAchievement extends Achievement {

	public KillBandosAchievement() {
		super(Types.MEDIUM, "Kill @TOTAL@ General Graardor's");
	}

	@Override
	public String getRewardInfo() {
		return "1,000,000 GP & 2x Achievement Points";
	}

	@Override
	public void giveReward(Player player) {
		addItem(player, new Item(995, 1000000));
		addAchievementPoints(player, 2);
	}

	@Override
	public int getTotalAmount() {
		return 10;
	}

	@Override
	public String getKey() {
		return "bandos_kills";
	}

	private static final long serialVersionUID = -2113671630799692314L;

}
