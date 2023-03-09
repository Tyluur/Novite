package novite.rs.game.player.cutscenes.actions;

import novite.rs.game.ForceTalk;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;

public class NPCForceTalkAction extends CutsceneAction {

	private String text;

	public NPCForceTalkAction(int cachedObjectIndex, String text, int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.text = text;
	}

	@Override
	public void process(Player player, Object[] cache) {
		NPC npc = (NPC) cache[getCachedObjectIndex()];
		npc.setNextForceTalk(new ForceTalk(text));
	}

}
