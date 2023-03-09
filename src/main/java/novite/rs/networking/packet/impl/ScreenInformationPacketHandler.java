package novite.rs.networking.packet.impl;

import novite.rs.game.player.Player;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketInformation;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 19, 2014
 */
@PacketInformation(listeners = "87")
public class ScreenInformationPacketHandler extends PacketHandler {

	@Override
	public void handle(Player player, Integer packetId, Integer length, InputStream stream) {
		int displayMode = stream.readUnsignedByte();
		player.setScreenWidth(stream.readUnsignedShort());
		player.setScreenHeight(stream.readUnsignedShort());

		stream.readUnsignedByte();
		if (!player.hasStarted() || player.hasFinished() || displayMode == player.getDisplayMode() || !player.getInterfaceManager().containsInterface(742)) {
			return;
		}
		player.setDisplayMode(displayMode);
		player.getInterfaceManager().removeAll();
		player.getInterfaceManager().sendInterfaces();
		player.getInterfaceManager().sendInterface(742);
	}

}
