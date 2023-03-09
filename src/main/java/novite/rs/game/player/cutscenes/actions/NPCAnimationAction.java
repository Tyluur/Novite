package novite.rs.game.player.cutscenes.actions;

import novite.rs.game.Animation;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;

public class NPCAnimationAction extends CutsceneAction {

	private Animation anim;

	public NPCAnimationAction(int cachedObjectIndex, Animation anim, int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.anim = anim;
	}

	@Override
	public void process(Player player, Object[] cache) {
		NPC npc = (NPC) cache[getCachedObjectIndex()];
		npc.setNextAnimation(anim);
	}

}
