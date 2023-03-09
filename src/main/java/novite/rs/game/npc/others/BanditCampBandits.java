package novite.rs.game.npc.others;

import java.util.ArrayList;

import novite.rs.game.Entity;
import novite.rs.game.ForceTalk;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.godwars.saradomin.GodwarsSaradominFaction;
import novite.rs.game.npc.godwars.zammorak.GodwarsZammorakFaction;
import novite.rs.game.player.Player;

public class BanditCampBandits extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = 248649568922522713L;

	public BanditCampBandits(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setForceAgressive(true); //to ignore combat lvl
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> targets = super.getPossibleTargets();
		ArrayList<Entity> targetsCleaned = new ArrayList<Entity>();
		for (Entity t : targets) {
			if (!(t instanceof Player) || (!GodwarsZammorakFaction.hasGodItem((Player) t) && !GodwarsSaradominFaction.hasGodItem((Player) t))) {
				continue;
			}
			targetsCleaned.add(t);
		}
		return targetsCleaned;
	}

	@Override
	public void setTarget(Entity entity) {
		if (entity instanceof Player && (GodwarsZammorakFaction.hasGodItem((Player) entity) || GodwarsSaradominFaction.hasGodItem((Player) entity))) {
			setNextForceTalk(new ForceTalk(GodwarsZammorakFaction.hasGodItem((Player) entity) ? "Time to die, Saradominist filth!" : "Prepare to suffer, Zamorakian scum!"));
		}
		super.setTarget(entity);
	}

}
