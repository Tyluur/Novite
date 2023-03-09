package novite.rs.game.player.cutscenes.actions;

import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.cutscenes.Cutscene;

public class NPCFaceTileAction extends CutsceneAction {

	private int x, y;

	public NPCFaceTileAction(int cachedObjectIndex, int x, int y, int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.x = x;
		this.y = y;
	}

	@Override
	public void process(Player player, Object[] cache) {
		Cutscene scene = (Cutscene) cache[0];
		NPC npc = (NPC) cache[getCachedObjectIndex()];
		npc.setNextFaceWorldTile(new WorldTile(scene.getBaseX() + x, scene.getBaseY() + y, npc.getPlane()));
	}

}
