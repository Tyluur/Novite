package novite.rs.game.player.actions;

import novite.rs.Constants;
import novite.rs.game.Animation;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.Magic;
import novite.rs.utility.Utils;

public class HomeTeleport extends Action {

	protected static final int HOME_ANIMATION = 16385;
	protected static final int HOME_GRAPHIC = 3017;
	protected static final int DONE_ANIMATION = 16386;

	private int currentTime;
	private WorldTile tile;

	@Override
	public boolean start(final Player player) {
		tile = Constants.DEATH_TILE;
		if (!player.getControllerManager().processMagicTeleport(tile)) {
			return false;
		}
		return process(player);
	}

	@Override
	public int processWithDelay(Player player) {
		player.getWalkSteps().clear();
		if (currentTime++ == 0) {
			player.setNextAnimation(new Animation(HOME_ANIMATION));
			player.setNextGraphics(new Graphics(HOME_GRAPHIC));
		} else if (currentTime == 17) {
			WorldTile teleTile = tile;
			// attemps to randomize tile by 4x4 area
			for (int trycount = 0; trycount < 10; trycount++) {
				teleTile = new WorldTile(tile, 2);
				if (World.canMoveNPC(tile.getPlane(), teleTile.getX(), teleTile.getY(), player.getSize())) {
					break;
				}
				teleTile = tile;
			}
			player.setNextWorldTile(teleTile);
			player.setNextAnimation(new Animation(HOME_ANIMATION + 1));
			player.setNextGraphics(new Graphics(HOME_GRAPHIC + 1));
			player.getControllerManager().magicTeleported(Magic.MAGIC_TELEPORT);
			if (player.getControllerManager().getController() == null) {
				Magic.teleControlersCheck(player, teleTile);
				// return 0;
			}
		} else if (currentTime == 21) {
			player.setNextAnimation(new Animation(-1));
			player.setNextGraphics(new Graphics(-1));
			return -1;
		}
		return 0;
	}

	@Override
	public boolean process(Player player) {
		if (player.getAttackedByDelay() + 10000 > Utils.currentTimeMillis()) {
			player.getPackets().sendGameMessage("You can't home teleport until 10 seconds after the end of combat.");
			return false;
		}
		return true;
	}

	@Override
	public void stop(Player player) {
	}

}