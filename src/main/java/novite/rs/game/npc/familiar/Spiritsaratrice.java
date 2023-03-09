package novite.rs.game.npc.familiar;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.Hit;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.actions.summoning.Pouches;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class Spiritsaratrice extends Familiar {

	/**
	 *
	 */
	private static final long serialVersionUID = 2314062140846425292L;
	private int chocoTriceEgg;

	public Spiritsaratrice(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		chocoTriceEgg++;
		if (chocoTriceEgg == 500) {
			addChocolateEgg();
		}
	}

	private void addChocolateEgg() {
		getBob().getBeastItems().add(new Item(12109, 1));
		chocoTriceEgg = 0;
	}

	@Override
	public String getSpecialName() {
		return "Petrifying Gaze";
	}

	@Override
	public String getSpecialDescription() {
		return "Inflicts damage and drains a combat stat, which varies according to the type of cockatrice.";
	}

	@Override
	public int getBOBSize() {
		return 30;
	}

	@Override
	public int getSpecialAmount() {
		return 5;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ENTITY;
	}

	@Override
	public boolean submitSpecial(Object object) {
		final Entity target = (Entity) object;
		getOwner().setNextGraphics(new Graphics(1316));
		getOwner().setNextAnimation(new Animation(7660));
		setNextAnimation(new Animation(7766));
		setNextGraphics(new Graphics(1467));
		World.sendProjectile(this, target, 1468, 34, 16, 30, 35, 16, 0);
		if (target instanceof Player) {
			Player playerTarget = (Player) target;
			int level = playerTarget.getSkills().getLevelForXp(Skills.PRAYER);
			int drained = 3;
			if (level - drained > 0) {
				drained = level;
			}
			playerTarget.getSkills().drainLevel(Skills.PRAYER, drained);
		}
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				target.applyHit(new Hit(getOwner(), Utils.random(100), HitLook.MELEE_DAMAGE));
			}
		}, 2);
		return true;
	}
}
