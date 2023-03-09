package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 10, 2014
 */
public class CookingGauntletsMediumAchievement extends Achievement {

	public CookingGauntletsMediumAchievement() {
		super(Types.MEDIUM, "Successfully cook @TOTAL@ meats");
	}

	@Override
	public String getRewardInfo() {
		return "Cooking Gauntlets & 2x Achievement Points";
	}

	@Override
	public void giveReward(Player player) {
		addItem(player, new Item(775));
		addAchievementPoints(player, 2);
	}

	@Override
	public int getTotalAmount() {
		return 195;
	}

	@Override
	public String getKey() {
		return "cooking_gauntlets";
	}

	private static final long serialVersionUID = -1985878729531917906L;
}
