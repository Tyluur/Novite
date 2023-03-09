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
import novite.rs.game.player.actions.Fishing;
import novite.rs.game.player.actions.summoning.Pouches;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class Granitelobster extends Familiar {

	private int forageTicks;
	private static final long serialVersionUID = -8354346765099305088L;

	public Granitelobster(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		boolean isFishing = getOwner().getActionManager().getAction() != null && getOwner().getActionManager().getAction() instanceof Fishing;
		if (isFishing) {
			forageTicks++;
			if (forageTicks == 300) {
				giveReward();
			}
		}
	}

	private void giveReward() {
		boolean isShark = Utils.random(3) == 0;
		int foragedItem = isShark ? 383 : 371;
		if (!isShark) {
			getOwner().getSkills().addXp(Skills.FISHING, 30);
		}
		getBob().getBeastItems().add(new Item(foragedItem, 1));
		forageTicks = 0;
	}

	@Override
	public String getSpecialName() {
		return "Crushing Claw";
	}

	@Override
	public String getSpecialDescription() {
		return "May inflict up to 140 life points of magic damage and temporarily decrease an opponent's Defence by five levels.";
	}

	@Override
	public int getBOBSize() {
		return 30;
	}

	@Override
	public int getSpecialAmount() {
		return 6;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ENTITY;
	}

	@Override
	public boolean submitSpecial(Object object) {
		final Entity target = (Entity) object;
		final Familiar npc = this;
		getOwner().setNextGraphics(new Graphics(1316));
		getOwner().setNextAnimation(new Animation(7660));
		setNextAnimation(new Animation(8118));
		setNextGraphics(new Graphics(1351));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {

				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						if (Utils.getRandom(5) == 0) {
							if (target instanceof Player) {
								((Player) target).getSkills().set(Skills.DEFENCE, ((Player) target).getSkills().getLevel(Skills.DEFENCE));
							}
						}
						target.applyHit(new Hit(getOwner(), Utils.random(140), HitLook.MELEE_DAMAGE));
						target.setNextGraphics(new Graphics(1353));
					}
				}, 2);
				World.sendProjectile(npc, target, 1352, 34, 16, 30, 35, 16, 0);
			}
		});
		return true;
	}
}
