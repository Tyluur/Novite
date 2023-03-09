package novite.rs.game.player.cutscenes.actions;

import novite.rs.game.Graphics;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;

public class NPCGraphicAction extends CutsceneAction {

	private Graphics gfx;

	public NPCGraphicAction(int cachedObjectIndex, Graphics gfx, int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.gfx = gfx;
	}

	@Override
	public void process(Player player, Object[] cache) {
		NPC npc = (NPC) cache[getCachedObjectIndex()];
		npc.setNextGraphics(gfx);
	}

}
