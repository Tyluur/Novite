package novite.rs.game.player;

import novite.rs.game.player.actions.Action;
import novite.rs.utility.Utils;

public final class ActionManager {

	public ActionManager(Player player) {
		this.player = player;
	}

	public void process() {
		if (action != null) {
			if (player.isDead()) {
				forceStop();
			} else if (!action.process(player)) {
				forceStop();
			}
		}
		if (actionDelay > 0) {
			actionDelay--;
			return;
		}
		if (action == null) {
			return;
		}
		int delay = action.processWithDelay(player);
		if (delay == -1) {
			forceStop();
			return;
		}
		actionDelay += delay;
	}

	public boolean setAction(Action skill) {
		startTime = Utils.currentTimeMillis();
		forceStop();
		if (!skill.start(player)) {
			return false;
		}
		this.action = skill;
		return true;
	}

	public Action getAction() {
		return action;
	}

	public void forceStop() {
		if (action == null) {
			return;
		}
		action.stop(player);
		player.setActionTime(player.getActionTime() + Utils.currentTimeMillis() - startTime);
		action = null;
	}

	public int getActionDelay() {
		return actionDelay;
	}

	public void addActionDelay(int actionDelay) {
		this.actionDelay += actionDelay;
	}

	public void setActionDelay(int actionDelay) {
		this.actionDelay = actionDelay;
	}

	public boolean hasSkillWorking() {
		return action != null;
	}

	private long startTime;
	private Player player;
	private Action action;
	private int actionDelay;
}
