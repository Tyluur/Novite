package novite.rs.networking.packet.impl;

import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.game.player.QuickChatMessage;
import novite.rs.game.player.content.FriendChatsManager;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketInformation;
import novite.rs.utility.Utils;
import novite.rs.utility.game.json.impl.PunishmentLoader;
import novite.rs.utility.game.punishments.Punishment.PunishmentType;
import novite.rs.utility.huffman.Huffman;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 19, 2014
 */
@PacketInformation(listeners = "1,41,32,72,79")
public class FriendChatPacketHandler extends PacketHandler {

	private final static int JOIN_FRIEND_CHAT_PACKET = 1;
	private final static int CHANGE_FRIEND_RANK = 41;
	private final static int KICK_FRIEND_CHAT_PACKET = 32;
	private final static int SEND_FRIEND_MESSAGE_PACKET = 72;
	private final static int SEND_FRIEND_QUICK_CHAT_PACKET = 79;

	@Override
	public void handle(Player player, Integer packetId, Integer length, InputStream stream) {
		switch (packetId) {
		case JOIN_FRIEND_CHAT_PACKET:
			if (!player.hasStarted()) {
				return;
			}
			FriendChatsManager.joinChat(stream.readString(), player);
			break;
		case CHANGE_FRIEND_RANK:
			if (!player.hasStarted() || !player.getInterfaceManager().containsInterface(1108)) {
				return;
			}
			player.getFriendsIgnores().changeRank(stream.readString(), stream.readUnsignedByteC());
			break;
		case KICK_FRIEND_CHAT_PACKET:
			if (!player.hasStarted()) {
				return;
			}
			player.setLastPublicMessage(Utils.currentTimeMillis() + 1000); // avoids-message-appearing
			player.kickPlayerFromFriendsChannel(stream.readString());
			break;
		case SEND_FRIEND_MESSAGE_PACKET:
			if (!player.hasStarted()) {
				return;
			}
			if (PunishmentLoader.isPunished(player.getUsername(), PunishmentType.MUTE) || PunishmentLoader.isPunished(player.getSession().getIP(), PunishmentType.IPMUTE)) {
				player.sendMessage("You are muted. Check back in 48 hours.");
				return;
			}
			String username = stream.readString();
			Player p2 = World.getPlayerByDisplayName(username);
			if (p2 == null) {
				return;
			}
			player.getFriendsIgnores().sendMessage(p2, Utils.fixChatMessage(Huffman.readEncryptedMessage(150, stream)));
			break;
		case SEND_FRIEND_QUICK_CHAT_PACKET:
			sendFriendQuickChat(player, packetId, length, stream);
			break;
		}
	}

	private void sendFriendQuickChat(Player player, Integer packetId, Integer length, InputStream stream) {
		if (!player.hasStarted()) {
			return;
		}
		String username = stream.readString();
		int fileId = stream.readUnsignedShort();
		byte[] data = null;
		if (length > 3 + username.length()) {
			data = new byte[length - (3 + username.length())];
			stream.readBytes(data);
		}
		data = Utils.completeQuickMessage(player, fileId, data);
		Player p2 = World.getPlayerByDisplayName(username);
		if (p2 == null) {
			return;
		}
		player.getFriendsIgnores().sendQuickChatMessage(p2, new QuickChatMessage(fileId, data));
	}
}
