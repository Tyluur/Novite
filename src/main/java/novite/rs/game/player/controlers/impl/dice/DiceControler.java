package novite.rs.game.player.controlers.impl.dice;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.RouteEvent;
import novite.rs.game.player.controlers.Controller;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 8, 2014
 */
public class DiceControler extends Controller {

	@Override
	public void start() {
		player.getPackets().sendPlayerOption("Challenge", 1, false);
	}

	@Override
	public boolean login() {
		start();
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public void forceClose() {
		remove();
	}

	@Override
	public void moved() {
		if (!isAtDiceArea(player)) {
			removeControler();
			remove();
		}
	}

	@Override
	public boolean canPlayerOption1(final Player target) {
		player.stopAll();
		player.setRouteEvent(new RouteEvent(target, new Runnable() {

			@Override
			public void run() {
				if (target.getInterfaceManager().containsScreenInterface() || target.isLocked()) {
					player.getPackets().sendGameMessage("The other player is busy.");
					return;
				}
				if (target.getTemporaryAttributtes().get("dice_challenger") == player) {
					player.getControllerManager().removeControlerWithoutCheck();
					target.getControllerManager().removeControlerWithoutCheck();
					
					target.getTemporaryAttributtes().remove("dice_challenger");
					
					player.setDiceSession(new DiceSession(player, target));
					target.setDiceSession(new DiceSession(target, player));
					
					player.getControllerManager().startController("DiceGame", target);
					target.getControllerManager().startController("DiceGame", player);
					return;
				}
				player.getTemporaryAttributtes().put("dice_challenger", target);
				target.getPackets().sendDuelChallengeRequestMessage(player, false);
				player.getPackets().sendGameMessage("Sending " + target.getDisplayName() + " a dice-duel request...");
			}

		}, true));
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		removeControler();
		remove();
	}

	/**
	 * If the tile is at the dice area
	 * 
	 * @param tile
	 *            The tile
	 * @return
	 */
	public static boolean isAtDiceArea(WorldTile tile) {
		int x = tile.getX();
		int y = tile.getY();
		
		if ((x > 2956 && x < 2978) && (y >= 9690 && y <= 9715) && tile.getPlane() == 0)
			return true;
		return false;
	}

	/**
	 * Removes the challenge option
	 */
	private void remove() {
		player.getPackets().sendPlayerOption("null", 1, false);
	}

}
