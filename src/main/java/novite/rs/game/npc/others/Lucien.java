package novite.rs.game.npc.others;

import novite.rs.game.Hit;
import novite.rs.game.WorldTile;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.npc.NPC;

public class Lucien extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = -9203549207415173902L;

	public Lucien(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setRun(true);
		setForceMultiAttacked(true);
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE && hit.getLook() != HitLook.MAGIC_DAMAGE) {
			return;
		}
		super.handleIngoingHit(hit);
		if (hit.getSource() != null) {
			int recoil = (int) (hit.getDamage() * 0.2);
			if (recoil > 0) {
				hit.getSource().applyHit(new Hit(this, recoil, HitLook.REFLECTED_DAMAGE));
			}
		}
	}
}
