package novite.rs.game.player.controlers.impl.quest;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.others.quest.DesertTreasureNPC;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.game.TeleportLocations;

/**
 * 
 * @author Tyluur <itstyluur@gmail.com>
 * @since 2012-12-12
 */
public class DesertTreasure extends Controller {

	private int boss;
	private DesertTreasureNPC target;

	@Override
	public void start() {
		boss = (Integer) getArguments()[0];
		switch (boss) {
		case 0: // kamil
			target = new DesertTreasureNPC(player.getUsername(), 1913, new WorldTile(2836, 3810, 2), -1, true);
			break;
		case 1: // dessous
			target = new DesertTreasureNPC(player.getUsername(), 1914, new WorldTile(3570, 3407, 0), -1, true);
			break;
		case 2: // fareed
			target = new DesertTreasureNPC(player.getUsername(), 1977, new WorldTile(3316, 9377, 0), -1, true);
			break;
		case 3: // damis
			target = new DesertTreasureNPC(player.getUsername(), 1974, new WorldTile(2740, 5091, 0), -1, true);
			break;
		}
		if (player.getFacade().getDesertTreasureKills().contains((target.getId() == 1974 ? 1975 : target.getId()))) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have already defeated this monster.");
			forceClose();
			return;
		}
		player.closeInterfaces();
		switch (boss) {
		case 0:
			player.setNextWorldTile(new WorldTile(2825, 3810, 2));
			break;
		case 1:
			player.setNextWorldTile(new WorldTile(3571, 3410, 0));
			break;
		case 2:
			player.setNextWorldTile(new WorldTile(3308, 9375, 0));
			break;
		case 3:
			player.setNextWorldTile(new WorldTile(2741, 5099, 0));
			break;
		}
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
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.reset();
					forceClose();
					removeControler();
					player.setNextAnimation(new Animation(-1));
					player.setNextWorldTile(TeleportLocations.QUESTING_DOME);
				} else if (loop == 1) {
					player.getPackets().sendMusicEffect(90);
					removeControler();
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return true;
	}

	@Override
	public void forceClose() {
		if (target != null) {
			target.finish(); // target also calls removing hint icon at remove
		}
	}

}
