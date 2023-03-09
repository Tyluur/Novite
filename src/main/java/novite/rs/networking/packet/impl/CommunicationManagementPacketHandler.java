package novite.rs.networking.packet.impl;

import novite.rs.game.player.Player;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketInformation;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 19, 2014
 */
@PacketInformation(listeners = "51,17,38,8")
public class CommunicationManagementPacketHandler extends PacketHandler {

	private final static int ADD_FRIEND_PACKET = 51;
	private final static int ADD_IGNORE_PACKET = 17;
	private final static int REMOVE_IGNORE_PACKET = 38;
	private final static int REMOVE_FRIEND_PACKET = 8;

	@Override
	public void handle(Player player, Integer packetId, Integer length, InputStream stream) {
		switch (packetId) {
		case ADD_FRIEND_PACKET:
			if (!player.hasStarted()) {
				return;
			}
			player.getFriendsIgnores().addFriend(stream.readString());
			break;
		case ADD_IGNORE_PACKET:
			if (!player.hasStarted()) {
				return;
			}
			player.getFriendsIgnores().addIgnore(stream.readString(), stream.readUnsignedByte() == 1);
			break;
		case REMOVE_FRIEND_PACKET:
			if (!player.hasStarted()) {
				return;
			}
			player.getFriendsIgnores().removeFriend(stream.readString());
			break;
		case REMOVE_IGNORE_PACKET:
			if (!player.hasStarted()) {
				return;
			}
			player.getFriendsIgnores().removeIgnore(stream.readString());
			break;
		}
	}

}
