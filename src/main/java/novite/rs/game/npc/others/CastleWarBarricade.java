package novite.rs.game.npc.others;

import novite.rs.game.Entity;
import novite.rs.game.WorldTile;
import novite.rs.game.minigames.CastleWars;
import novite.rs.game.npc.NPC;
import novite.rs.utility.Utils;

public class CastleWarBarricade extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = 31049854479236711L;
	private int team;

	public CastleWarBarricade(int team, WorldTile tile) {
		super(1532, tile, -1, true, true);
		setCantFollowUnderCombat(true);
		this.team = team;
	}

	@Override
	public void processNPC() {
		if (isDead()) {
			return;
		}
		cancelFaceEntityNoCheck();
		if (getId() == 1533 && Utils.getRandom(20) == 0) {
			sendDeath(this);
		}
	}

	public void litFire() {
		transformInto(1533);
		sendDeath(this);
	}

	public void explode() {
		sendDeath(this);
	}

	@Override
	public void sendDeath(Entity killer) {
		resetWalkSteps();
		getCombat().removeTarget();
		if (this.getId() != 1533) {
			setNextAnimation(null);
			reset();
			setLocation(getRespawnTile());
			finish();
		} else {
			super.sendDeath(killer);
		}
		CastleWars.removeBarricade(team, this);
	}

}
