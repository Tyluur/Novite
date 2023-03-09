package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 10, 2014
 */
public class RuneOreHardAchievement extends Achievement {

	public RuneOreHardAchievement() {
		super(Types.HARD, "Mine @TOTAL@ Rune Ores");
	}

	@Override
	public String getRewardInfo() {
		return "Dragon Pickaxe & 3x Achievement Points";
	}

	@Override
	public void giveReward(Player player) {
		addItem(player, new Item(15259));
		addAchievementPoints(player, 3);
	}

	@Override
	public int getTotalAmount() {
		return 200;
	}

	@Override
	public String getKey() {
		return "rune_ores_mined";
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3041033555077230225L;
}
