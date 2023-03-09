package novite.rs.game.player.controlers.impl;

import novite.rs.game.World;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.Magic;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class ObeliskControler extends Controller {

	public final WorldTile[] areas = { new WorldTile(3156, 3620, 0), new WorldTile(3219, 3656, 0), new WorldTile(3307, 3916, 0), new WorldTile(3106, 3794, 0), new WorldTile(2980, 3866, 0), new WorldTile(3035, 3732, 0) };

	int selection = Utils.getRandom(5);
	WorldTile selected = areas[selection];

	@Override
	public void start() {
		WorldObject object = (WorldObject) getArguments()[0];
		if (object.getDefinitions().name.equalsIgnoreCase("Obelisk") && object.getY() > 3527) { // if name equals Obelisk and is
			// north of wilderness wall.
			for (Player players : World.getPlayers()) {
				if (atLvl13(players) && atLvl13(player)) {
					sentTeleport(players);
				} else if (atLvl18(players) && atLvl18(player)) {
					sentTeleport(players);
				} else if (atLvl50(players) && atLvl50(player)) {
					sentTeleport(players);
				} else if (atLvl35(players) && atLvl35(player)) {
					sentTeleport(players);
				} else if (atLvl44(players) && atLvl44(player)) {
					sentTeleport(players);
				} else if (atLvl27(players) && atLvl27(player)) {
					sentTeleport(players);
				}
			}
		}
	}

	public void sentTeleport(final Player pl) {
		pl.getPackets().sendGameMessage("Obelisk has been activated..", true);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				if (player.hasFinished()) {
					stop();
				}
				Magic.sendTeleportSpell(pl, 8939, 8941, 661, -1, 0, 0, new WorldTile(selected), 3, true, Magic.OBJECT_TELEPORT);
				removeControler();
				pl.getControllerManager().startController("Wilderness");
				stop();
			}
		}, 5);
	}

	public boolean atLvl13(Player pl) {
		if ((pl.getX() >= 3154 && pl.getX() <= 3158) && (pl.getY() >= 3618 && pl.getY() <= 3622)) {
			return true;
		}
		return false;
	}

	public boolean atLvl18(Player pl) {
		if ((pl.getX() >= 3217 && pl.getX() <= 3221) && (pl.getY() >= 3654 && pl.getY() <= 3658)) {
			return true;
		}
		return false;
	}

	public boolean atLvl50(Player pl) {
		if ((pl.getX() >= 3305 && pl.getX() <= 3309) && (pl.getY() >= 3914 && pl.getY() <= 3918)) {
			return true;
		}
		return false;
	}

	public boolean atLvl35(Player pl) {
		if ((pl.getX() >= 3104 && pl.getX() <= 3108) && (pl.getY() >= 3792 && pl.getY() <= 3796)) {
			return true;
		}
		return false;
	}

	public boolean atLvl44(Player pl) {
		if ((pl.getX() >= 2978 && pl.getX() <= 2982) && (pl.getY() >= 3864 && pl.getY() <= 3868)) {
			return true;
		}
		return false;
	}

	public boolean atLvl27(Player pl) {
		if ((pl.getX() >= 3033 && pl.getX() <= 3037) && (pl.getY() >= 3730 && pl.getY() <= 3734)) {
			return true;
		}
		return false;
	}
}
