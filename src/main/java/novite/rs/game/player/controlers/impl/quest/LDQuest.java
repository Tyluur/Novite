package novite.rs.game.player.controlers.impl.quest;

import novite.rs.game.Entity;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.others.quest.DesertTreasureNPC;
import novite.rs.game.npc.others.quest.LunarDiplomacyNPC;
import novite.rs.game.player.controlers.Controller;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 29, 2014
 */
public class LDQuest extends Controller {

	@Override
	public void start() {
		player.closeInterfaces();
		target = new LunarDiplomacyNPC(player.getUsername(), 4510, TARGET_TILE, -1, true);
		player.setNextWorldTile(new WorldTile(1825, 5165, 2));
		target.setTarget(player);
		player.getHintIconsManager().addHintIcon(target, 1, -1, false);
	}

	@Override
	public boolean login() {
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public boolean checkWalkStep(int lastX, int lastY, int nextX, int nextY) {
		if (target != null && !player.withinDistance(target, 20)) {
			forceClose();
			removeControler();
		}
		return true;
	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof DesertTreasureNPC && target != this.target) {
			player.getPackets().sendGameMessage("This isn't your target.");
			return false;
		}
		return true;
	}

	@Override
	public void forceClose() {
		if (target != null) {
			target.finish(); // target also calls removing hint icon at remove
		}
	}

	private static final WorldTile TARGET_TILE = new WorldTile(1825, 5155, 2);
	private LunarDiplomacyNPC target;
}
