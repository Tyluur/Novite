package novite.rs.game.player.actions.custom;

import novite.rs.game.Animation;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.actions.Action;
import novite.rs.game.player.dialogues.ItemMessage;
import novite.rs.game.player.dialogues.SimpleMessage;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class DFSCreateAction extends Action {

	@Override
	public boolean start(Player player) {
		return checkAll(player);
	}

	@Override
	public boolean process(Player player) {
		if (!checkAll(player)) {
			return false;
		}
		player.lock();
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		ticksPassed = ticksPassed + 1;
		switch (ticksPassed) {
			case 1:
				player.setNextAnimation(new Animation(898));
				player.getDialogueManager().startDialogue(ItemMessage.class, 11286, "You set to work, trying to attach the ancient draconic", "visage to your anti-dragonbreath shield. It's not easy to", "work with the ancient artifact and it takes all of your", "skill as a master smith.");
				break;
			case 4:
			case 6:
			case 8:
				player.setNextAnimation(new Animation(898));
				break;
			case 10:
				player.unlock();
				player.getInventory().addItem(11283, 1);
				player.getInventory().deleteItem(11286, 1);
				player.getInventory().deleteItem(1540, 1);
				player.getSkills().addExpNoModifier(Skills.SMITHING, 2000);
				player.stopAll();

				player.getDialogueManager().startDialogue(ItemMessage.class, 11283, "Even for an expert armourer it is not an easy task,", "but eventually it is ready. You have crafted the", "draconic visage and anti-dragonbreath shield into a", "dragonfire shield");
				break;
		}
		return 0;
	}

	@Override
	public void stop(Player player) {

	}

	private boolean checkAll(Player player) {
		if (player.getSkills().getLevelForXp(Skills.SMITHING) < 90) {
			player.getDialogueManager().startDialogue(SimpleMessage.class, "You need a smithing level of 90 to smith a dragon fire shield.");
			return false;
		}
		if (!player.getInventory().containsItems(new int[] { 11286, 2347, 1540 }, new int[] { 1, 1, 1 })) {
			player.getDialogueManager().startDialogue(SimpleMessage.class, "You need to have a visage, hammer, and anti-", "dragon fire shield in your inventory to do this.");
			return false;
		}
		return true;
	}

	private int ticksPassed;

}
