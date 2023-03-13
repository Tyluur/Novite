package novite.rs.game.player.actions.prayer;

import novite.rs.game.Animation;
import novite.rs.game.Graphics;
import novite.rs.game.WorldObject;
import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.actions.Action;
import novite.rs.game.player.dialogues.impl.AltarBoneD;
import novite.rs.game.player.dialogues.impl.SimpleItemMessage;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 4, 2014
 */
public class AltarAction extends Action {

	public AltarAction(WorldObject object, int boneId, int ticks) {
		this.object = object;
		this.boneId = boneId;
		this.ticks = ticks;
	}

	public static boolean handleBoneOnAltar(Player player, WorldObject object, Item item) {
		final Bone bone = Bone.forId(item.getId());
		if (object.getId() == 13199 && bone != null) {
			player.getDialogueManager().startDialogue(AltarBoneD.class, item.getId(), object);
			return true;
		}
		return false;
	}

	@Override
	public boolean start(Player player) {
		if (!process(player)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (ticks <= 0) {
			return false;
		}
		if (!player.getInventory().containsItem(boneId, 1)) {
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		Bone bone = Bone.forId(boneId);
		if (bone != null) {
			ticks--;
			player.getInventory().deleteItem(boneId, 1);
			player.getSkills().addXp(Skills.PRAYER, bone.getExperience() * 3);
			player.getDialogueManager().startDialogue(SimpleItemMessage.class, boneId, "You offer the bones to the gods...", "", "They give you prayer experience in return.");
			player.getPackets().sendGraphics(OBJECT_GRAPHICS, object);
			player.setNextAnimation(ANIMATION);
		} else {
			stop(player);
		}
		return 1;
	}

	@Override
	public void stop(Player player) {
		
	}
	
	private final int boneId;
	private final WorldObject object;
	
	private int ticks;
	
	private static final Graphics OBJECT_GRAPHICS = new Graphics(624);
	private static final Animation ANIMATION = new Animation(896);
}
