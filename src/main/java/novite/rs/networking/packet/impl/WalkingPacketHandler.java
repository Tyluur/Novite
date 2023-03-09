package novite.rs.networking.packet.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.route.RouteFinder;
import novite.rs.game.route.strategy.FixedTileStrategy;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketInformation;
import novite.rs.utility.Utils;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 19, 2014
 */
@PacketInformation(listeners = "12,83")
public class WalkingPacketHandler extends PacketHandler {

	@Override
	public void handle(Player player, Integer packetId, Integer length, InputStream stream) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead() || !player.getControllerManager().canWalk()) {
			return;
		}
		long currentTime = Utils.currentTimeMillis();
		if (player.getLockDelay() > currentTime) {
			return;
		}
		if (player.isFrozen()) {
			player.getPackets().sendGameMessage("A magical force prevents you from moving.");
			return;
		}
		int x = stream.readUnsignedShortLE128();
		int y = stream.readUnsignedShortLE128();
		boolean forceRun = stream.readUnsignedByte() == 1;
		player.stopAll();
		if (forceRun) {
			player.setRun(forceRun);
		}

		int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, player.getX(), player.getY(), player.getPlane(), player.getSize(), new FixedTileStrategy(x, y), true);
		int[] bufferX = RouteFinder.getLastPathBufferX();
		int[] bufferY = RouteFinder.getLastPathBufferY();
		int last = -1;
		for (int i = steps - 1; i >= 0; i--) {
			if (!player.addWalkSteps(bufferX[i], bufferY[i], 25, true)) {
				break;
			}
			last = i;
		}
		if (last != -1) {
			WorldTile tile = new WorldTile(bufferX[last], bufferY[last], player.getPlane());
			player.getPackets().sendMinimapFlag(tile.getLocalX(player.getLastLoadedMapRegionTile(), player.getMapSize()), tile.getLocalY(player.getLastLoadedMapRegionTile(), player.getMapSize()));
		} else {
			player.getPackets().sendResetMinimapFlag();
		}
	}

}