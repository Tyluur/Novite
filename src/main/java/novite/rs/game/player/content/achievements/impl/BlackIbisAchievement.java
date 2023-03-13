package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 29, 2014
 */
public class BlackIbisAchievement extends Achievement {

	public BlackIbisAchievement() {
		super(Types.HARD, "Steal from Paladins @TOTAL@ times");
	}

	@Override
	public String getRewardInfo() {
		return "Black Ibis Armour";
	}

	@Override
	public void giveReward(Player player) {
		addItem(player, new Item(21480), new Item(21481), new Item(21482), new Item(21483));
	}

	@Override
	public int getTotalAmount() {
		return 150;
	}

	@Override
	public String getKey() {
		return "paladin_thieving";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2187069769468136443L;
}
