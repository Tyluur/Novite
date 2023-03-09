package novite.rs.game.npc.corp;

import novite.rs.game.Entity;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;

public class CorporealBeast extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = 4008329879983345213L;
	private DarkEnergyCore core;

	public CorporealBeast(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setCapDamage(1000);
		setLureDelay(3000);
		setForceTargetDistance(64);
		setForceFollowClose(true);
	}

	public void spawnDarkEnergyCore() {
		if (core != null) {
			return;
		}
		core = new DarkEnergyCore(this);
	}

	public void removeDarkEnergyCore() {
		if (core == null) {
			return;
		}
		core.finish();
		core = null;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead()) {
			return;
		}
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && getPossibleTargets().isEmpty()) {
			setHitpoints(maxhp);
		}
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		if (core != null) {
			core.sendDeath(source);
		}
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

}
