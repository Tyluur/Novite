package novite.rs.game.npc.fightcave;

import java.util.ArrayList;
import java.util.List;

import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.controlers.impl.FightCaves;

public class FightCavesNPC extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = -2698762042118008571L;

	public FightCavesNPC(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		setForceMultiArea(true);
		setForceAgressive(true);
	}
	
	@Override
	public void processNPC() {
		super.processNPC();
		Player closest = null;
		List<Integer> playerIndexes = World.getRegion(getRegionId()).getPlayerIndexes();
		if (playerIndexes != null) {
			for (int npcIndex : playerIndexes) {
				Player player = World.getPlayers().get(npcIndex);
				if (player == null || player.isDead() || player.hasFinished() || !player.isRunning() || !(player.getControllerManager().getController() instanceof FightCaves)) {
					continue;
				}
				closest = player;
				break;
			}
		}
		if (closest != null && !closest.withinDistance(this, 5) && getCombat().getTarget() == null) {
			calcFollow(closest, 2, true, false);
		}
		
	}

	@Override
	public void drop() {
		
	}
	
	@Override
	public void sendDeath(Entity source) {
		setNextGraphics(new Graphics(2924 + getSize()));
		super.sendDeath(source);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>(1);
		List<Integer> playerIndexes = World.getRegion(getRegionId()).getPlayerIndexes();
		if (playerIndexes != null) {
			for (int npcIndex : playerIndexes) {
				Player player = World.getPlayers().get(npcIndex);
				if (player == null || player.isDead() || player.hasFinished() || !player.isRunning()) {
					continue;
				}
				possibleTarget.add(player);
			}
		}
		return possibleTarget;
	}

}
