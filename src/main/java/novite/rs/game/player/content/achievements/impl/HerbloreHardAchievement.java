package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 10, 2014
 */
public class HerbloreHardAchievement extends Achievement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 148275366500868211L;

	public HerbloreHardAchievement() {
		super(Types.HARD, "Create @TOTAL@ prayer potion(3)s");
	}

	@Override
	public String getRewardInfo() {
		return "Witchdoctor outfit & 3x Achievement Points";
	}

	@Override
	public void giveReward(Player player) {
		addAchievementPoints(player, 3);
		addItem(player, new Item(20044), new Item(20045), new Item(20046));
	}

	@Override
	public int getTotalAmount() {
		return 175;
	}

	@Override
	public String getKey() {
		return "prayer_pot_3";
	}

}
