package novite.rs.game.npc.familiar;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.Hit;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.actions.summoning.Pouches;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class Karamthulhuoverlord extends Familiar {

	private static final long serialVersionUID = 6236333946001886534L;

	public Karamthulhuoverlord(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Doomsphere Device";
	}

	@Override
	public String getSpecialDescription() {
		return "Attacks the target with a powerful water spell that can cause up to 160 life points";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 3;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ENTITY;
	}

	@Override
	public boolean submitSpecial(Object object) {
		final Entity target = (Entity) object;
		Player player = getOwner();
		setNextGraphics(new Graphics(7974));
		setNextGraphics(new Graphics(1478));
		player.setNextAnimation(new Animation(7660));
		player.setNextGraphics(new Graphics(1316));
		World.sendProjectile(this, target, 1479, 34, 16, 30, 35, 16, 0);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				target.applyHit(new Hit(getOwner(), Utils.getRandom(163), HitLook.MAGIC_DAMAGE));
				target.setNextGraphics(new Graphics(1480));
			}
		}, 2);
		return true;
	}
}
