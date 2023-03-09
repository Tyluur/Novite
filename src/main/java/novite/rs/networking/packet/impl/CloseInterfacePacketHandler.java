package novite.rs.networking.packet.impl;

import novite.rs.game.player.Player;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketInformation;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 19, 2014
 */
@PacketInformation(listeners = "56")
public class CloseInterfacePacketHandler extends PacketHandler {

	@Override
	public void handle(Player player, Integer packetId, Integer length, InputStream stream) {
		if (!player.isRunning()) {
			player.run();
			return;
		}
		player.stopAll();
	}

}
