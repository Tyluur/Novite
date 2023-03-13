package novite.rs.networking.packet.impl;

import novite.rs.api.event.command.CommandHandler;
import novite.rs.game.player.Player;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketInformation;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 19, 2014
 */
@PacketInformation(listeners = "70")
public class CommandsPacketHandler extends PacketHandler {

	@Override
	public void handle(Player player, Integer packetId, Integer length, InputStream stream) {
		if (!player.isRunning()) {
			return;
		}
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		String command = stream.readString();
		CommandHandler.get().handleCommand(player, command);
	}

}
