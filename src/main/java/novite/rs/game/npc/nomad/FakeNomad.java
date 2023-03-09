package novite.rs.game.npc.nomad;

import novite.rs.game.Hit;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;

public class FakeNomad extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = 1439177820906728935L;
	private Nomad nomad;

	public FakeNomad(WorldTile tile, Nomad nomad) {
		super(8529, tile, -1, true, true);
		this.nomad = nomad;
		setForceMultiArea(true);
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		nomad.destroyCopy(this);
	}

}
