package novite.rs.networking.packet.impl;

import novite.rs.game.player.Player;
import novite.rs.networking.codec.handlers.ObjectHandler;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketInformation;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 19, 2014
 */
@PacketInformation(listeners = "11,2,76,47")
public class ObjectInteractionPacketHandler extends PacketHandler {

	private final static int OBJECT_CLICK1_PACKET = 11;
	private final static int OBJECT_CLICK2_PACKET = 2;
	private final static int OBJECT_CLICK3_PACKET = 76;
	private final static int OBJECT_EXAMINE_PACKET = 47;
	
	@Override
	public void handle(Player player, Integer packetId, Integer length, InputStream stream) {
		switch(packetId) {
		case OBJECT_CLICK1_PACKET:
			ObjectHandler.handleObjectInteraction(player, 1, stream);
			break;
		case OBJECT_CLICK2_PACKET:
			ObjectHandler.handleObjectInteraction(player, 2, stream);
			break;
		case OBJECT_CLICK3_PACKET:
			ObjectHandler.handleObjectInteraction(player, 3, stream);
			break;
		case OBJECT_EXAMINE_PACKET:
			ObjectHandler.handleObjectInteraction(player, 4, stream);
			break;
		}
	}

}
