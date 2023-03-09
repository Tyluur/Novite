package novite.rs.networking.packet.impl;

import novite.rs.game.player.Player;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketInformation;
import novite.rs.utility.Utils;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 19, 2014
 */
@PacketInformation(listeners = "84,29,68,75,93")
public class FrameInteractionPacketHandler extends PacketHandler {

	private final static int WINDOW_SWITCH_PACKET = 93;

	@Override
	public void handle(Player player, Integer packetId, Integer length, InputStream stream) {
		/*if (packetId == 68 || packetId == 84) { // Typing & clicking
			player.setPacketsDecoderPing(Utils.currentTimeMillis());
		}*/
		if (packetId == WINDOW_SWITCH_PACKET) {
			int active = stream.readByte();
			player.getInterfaceManager().setClientActive(active == 1);
			return;
		}
	}

}
