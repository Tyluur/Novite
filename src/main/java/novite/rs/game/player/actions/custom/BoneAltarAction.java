package novite.rs.game.player.actions.custom;

import novite.rs.game.Animation;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.actions.Action;
import novite.rs.game.player.actions.prayer.Bone;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 5, 2014
 */
public class BoneAltarAction extends Action {

	public BoneAltarAction(int id, int amount) {
		this.id = id;
		this.amount = amount;
		bone = Bone.forId(id);
	}

	public boolean checkAll(Player player) {
		if (!player.getInventory().contains(id)) {
			player.sendMessage("You don't have any more bones.");
			return false;
		}
		if (bone == null) {
			player.sendMessage("This bone doesn't work yet. Report on forums");
			return false;
		}
		if (amount <= 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean start(Player player) {
		return checkAll(player);
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		player.setNextAnimation(new Animation(896));
		player.getInventory().deleteItem(id, 1);
		player.getSkills().addXp(Skills.PRAYER, (bone.getExperience()) * 2.25);
		amount--;
		return 3;
	}

	@Override
	public void stop(Player player) {

	}

	private final int id;
	private final Bone bone;

	private int amount;

}
