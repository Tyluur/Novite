package novite.rs.game.player.actions.crafting;

import novite.rs.game.Animation;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.actions.Action;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Jan 15, 2014
 */
public class CannonBallCreation extends Action {

	private boolean checkAll(Player player) {
		/*if (!player.getQuestManager().completedQuest(QuestManager.getQuest(Helpless_Lawgof.class).getName())) {
			player.sendMessage("You must complete " + QuestManager.getQuest(Helpless_Lawgof.class).getName() + " to smith cannonballs.");
			return false;
		}*/
		if (!player.getInventory().contains(2353)) {
			player.sendMessage("You have run out of steel bars to use!");
			return false;
		}
		if (!player.getInventory().contains(4)) {
			player.sendMessage("You do not have an ammo mould in your inventory.");
			return false;
		}
		if (amount < 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		player.setNextAnimation(new Animation(3243));
		player.getSkills().addXp(Skills.SMITHING, 25.6);
		player.sendMessage("The molten metal cools slowly to form 4 cannonballs.");
		player.getInventory().deleteItem(2353, 1);
		player.getInventory().addItem(2, 4);
		amount--;
		return 4;
	}

	@Override
	public void stop(Player player) {

	}

	public CannonBallCreation(int amount) {
		this.amount = amount;
	}

	private int amount;

}
