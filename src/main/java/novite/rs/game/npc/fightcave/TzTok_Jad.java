package novite.rs.game.npc.fightcave;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.controlers.impl.FightCaves;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;

public class TzTok_Jad extends FightCavesNPC {

	private static final long serialVersionUID = 3859224985238139857L;
	private boolean spawnedMinions;
	private FightCaves controler;

	public TzTok_Jad(int id, WorldTile tile, FightCaves controler) {
		super(id, tile);
		this.controler = controler;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (!spawnedMinions && getHitpoints() < getMaxHitpoints() / 2) {
			spawnedMinions = true;
			controler.spawnHealers();
		}
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
					setNextGraphics(new Graphics(2924 + getSize()));
				} else if (loop >= defs.getDeathDelay()) {
					reset();
					finish();
					controler.win();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

}
