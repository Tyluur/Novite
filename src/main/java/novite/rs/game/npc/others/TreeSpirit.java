package novite.rs.game.npc.others;

import novite.rs.game.Entity;
import novite.rs.game.ForceTalk;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;

public class TreeSpirit extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = -7774114178429486637L;
	private Player target;

	public TreeSpirit(Player target, WorldTile tile) {
		super(655, tile, -1, true, true);
		this.target = target;
		target.getTemporaryAttributtes().put("HAS_SPIRIT_TREE", true);
		setTarget(target);
		setNextForceTalk(new ForceTalk("You must defeat me before touching the tree!"));
	}

	@Override
	public void processNPC() {
		if (!target.withinDistance(this, 16)) {
			target.getTemporaryAttributtes().remove("HAS_SPIRIT_TREE");
			finish();
		}
		super.processNPC();
	}

	@Override
	public void sendDeath(Entity source) {
		target.getTemporaryAttributtes().remove("HAS_SPIRIT_TREE");
		super.sendDeath(source);

	}

}
