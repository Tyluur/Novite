package novite.rs.networking.packet.impl;

import novite.rs.game.player.Player;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketInformation;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 19, 2014
 */
@PacketInformation(listeners = "-50")
public class SwitchDetailPacketHandler extends PacketHandler {

	@Override
	public void handle(Player player, Integer packetId, Integer length, InputStream stream) {
		int hash = stream.readInt();
		if (hash != -100508416) {
			player.getSession().getChannel().close();
			return;
		}
	}

}
