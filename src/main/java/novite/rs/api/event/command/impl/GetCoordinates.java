package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 19, 2014
 */
public class GetCoordinates extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.ADMINISTRATOR;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "coords", "pos" };
	}

	@Override
	public void execute(Player player) {
		player.getPackets().sendGameMessage("Coords: " + player.getX() + ", " + player.getY() + ", " + player.getPlane() + ", regionId: " + player.getRegionId() + ", rx: " + player.getChunkX() + ", ry: " + player.getChunkY(), true);
		System.out.println("new WorldTile(" + player.getX() + ", " + player.getY() + ", " + player.getPlane() + ")");
	}

}
