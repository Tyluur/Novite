package novite.rs.game.minigames.clanwars;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.player.controlers.impl.Wilderness;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;

/**
 * Handles the FFA Clan Wars zone.
 * 
 * @author Emperor
 * 
 */
public final class FfaZone extends Controller {

	/**
	 * If the FFA zone is the risk zone.
	 */
	private boolean risk;

	/**
	 * If the player was in the ffa pvp area.
	 */
	private transient boolean wasInArea;

	@Override
	public void start() {
		if (getArguments() == null || getArguments().length < 1) {
			this.setRisk(player.getX() >= 2948 && player.getY() >= 5508 && player.getX() <= 3071 && player.getY() <= 5631);
		} else {
			this.setRisk((Boolean) getArguments()[0]);
		}
		moved();
		sendInterfaces();
	}

	@Override
	public void sendInterfaces() {
		int interfaceId = 789;
		int resizableId = 10;
		int normalId = 8;
		boolean shouldAdd = !player.getInterfaceManager().containsInterface(interfaceId);
		if (shouldAdd) {
			player.getInterfaceManager().sendTab(player.getInterfaceManager().hasResizableScreen() ? resizableId : normalId, interfaceId);
		}
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
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					if (isRisk()) {
						Player killer = player.getMostDamageReceivedSourcePlayer();
						if (killer != null) {
							killer.removeDamage(player);
							killer.increaseKillCount(player);
							if (isRisk())
								player.sendItemsOnDeath(killer, true);
						}
					}
					player.setNextWorldTile(new WorldTile(2993, 9679, 0));
					player.getControllerManager().startController("clan_wars_request");
					player.reset();
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		player.getControllerManager().forceStop();
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		switch (object.getId()) {
		case 38700:
			player.setNextWorldTile(new WorldTile(2993, 9679, 0));
			player.getControllerManager().forceStop();
			player.getControllerManager().startController("clan_wars_request");
			return false;
		}
		return true;
	}

	@Override
	public void moved() {
		boolean inArea = inPvpArea(player);
		if (inArea && !wasInArea) {
			player.setCanPvp(true);
			wasInArea = true;
			Wilderness.checkBoosts(player);
		} else if (!inArea && wasInArea) {
			player.setCanPvp(false);
			wasInArea = false;
		}
	}

	@Override
	public boolean keepCombating(Entity victim) {
		if (!(victim instanceof Player))
			return true;
		return player.isCanPvp() && ((Player) victim).isCanPvp();
	}

	@Override
	public void forceClose() {
		player.setCanPvp(false);
		player.getPackets().closeInterface(player.getInterfaceManager().hasResizableScreen() ? 10 : 8);
	}

	@Override
	public boolean logout() {
		setArguments(new Object[] { isRisk() });
		return false;
	}

	@Override
	public boolean login() {
		moved();
		sendInterfaces();
		return false;
	}

	/**
	 * Checks if the location is in a ffa pvp zone.
	 * 
	 * @param t
	 *            The world tile.
	 * @return {@code True} if so.
	 */
	public static boolean inPvpArea(WorldTile t) {
		return (t.getX() >= 2948 && t.getY() >= 5512 && t.getX() <= 3071 && t.getY() <= 5631) // Risk
				// area.
				|| (t.getX() >= 2756 && t.getY() >= 5512 && t.getX() <= 2879 && t.getY() <= 5631); // Safe
		// area.
	}

	/**
	 * Checks if the location is in a ffa zone.
	 * 
	 * @param t
	 *            The world tile.
	 * @return {@code True} if so.
	 */
	public static boolean inArea(WorldTile t) {
		return (t.getX() >= 2948 && t.getY() >= 5508 && t.getX() <= 3071 && t.getY() <= 5631) // Risk
				// area.
				|| (t.getX() >= 2756 && t.getY() >= 5508 && t.getX() <= 2879 && t.getY() <= 5631); // Safe
		// area.
	}

	/**
	 * Checks if a player's overload effect is changed (due to being in the risk
	 * ffa zone, in pvp)
	 * 
	 * @param player
	 *            The player.
	 * @return {@code True} if so.
	 */
	public static boolean isOverloadChanged(Player player) {
		if (!(player.getControllerManager().getController() instanceof FfaZone)) {
			return false;
		}
		return player.isCanPvp() && ((FfaZone) player.getControllerManager().getController()).isRisk();
	}

	/**
	 * @return the risk
	 */
	public boolean isRisk() {
		return risk;
	}

	/**
	 * @param risk the risk to set
	 */
	public void setRisk(boolean risk) {
		this.risk = risk;
	}
}