package novite.rs.game.minigames.akrisae;

import java.util.ArrayList;
import java.util.List;

import novite.rs.game.Entity;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 25, 2014
 */
public class AkrisaeBrother extends NPC {

	public AkrisaeBrother(Player killer, int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		setForceAgressive(true);
		setSpawned(true);
		setForceMultiArea(true);
		this.killer = killer;
	}

	@Override
	public void drop() {
		if (killer.getControllerManager().getController() instanceof AkrisaeController) {
			AkrisaeController controller = (AkrisaeController) killer.getControllerManager().getController();
			controller.setKillCount(controller.getKillCount() + 1);
			controller.removeBrother(this);
		}
	}
	
	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> targets = new ArrayList<Entity>();
		List<Integer> indexes = getRegion().getPlayerIndexes();
		if (indexes == null) 
			return targets;
		for (Integer index : indexes) {
			Entity entity = World.getPlayers().get(index);
			if (entity == null)
				continue;
			targets.add(entity);
		}
		return targets;
	}
	
	private final Player killer;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5618869263114062689L;

}
