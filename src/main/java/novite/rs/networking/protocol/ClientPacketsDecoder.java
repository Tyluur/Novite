package novite.rs.networking.protocol;

import novite.rs.Constants;
import novite.rs.cache.Cache;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.networking.Session;
import novite.rs.networking.codec.Decoder;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.utility.IsaacKeyPair;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ServerInformation;

public final class ClientPacketsDecoder extends Decoder {

	public ClientPacketsDecoder(Session connection) {
		super(connection);
	}

	@Override
	public final void decode(InputStream stream) {
		session.setDecoder(-1);
		int packetId = stream.readUnsignedByte();
		switch (packetId) {
		case 1997:
			decodeBotLogin(stream);
			break;
		case 14:
			decodeLogin(stream);
			break;
		case 15:
			decodeGrab(stream);
			break;
		default:
			session.getChannel().close();
			System.out.println("Received packetId " + packetId + " so closed session");
			break;
		}
	}

	@SuppressWarnings("unused")
	public void decodeBotLogin(InputStream stream) {
		session.setEncoder(1);
		boolean unknownEquals14 = stream.readUnsignedByte() == 1;
		int rsaBlockSize = stream.readUnsignedShort();
		if (stream.readUnsignedByte() != 10) {
			session.getLoginPackets().sendClientPacket(10);
			//return;
		}
		int[] isaacKeys = new int[4];
		for (int i = 0; i < isaacKeys.length; i++) {
			isaacKeys[i] = stream.readInt();
		}
		if (stream.readLong() != 0L) { // rsa block check, pass part
			session.getLoginPackets().sendClientPacket(10);
			//return;
		}
		String password = stream.readString();
		// password = Encrypt.encryptSHA1(password);
		String unknown = Utils.longToString(stream.readLong());
		stream.readLong(); // random value
		stream.readLong(); // random value
		//stream.decodeXTEA(isaacKeys, stream.getOffset(), stream.getLength());
		boolean stringUsername = stream.readUnsignedByte() == 1; // unknown
		String username = Utils.formatPlayerNameForProtocol(stringUsername ? stream.readString() : Utils.longToString(stream.readLong()));
		int displayMode = stream.readUnsignedByte();
		int screenWidth = stream.readUnsignedShort();
		int screenHeight = stream.readUnsignedShort();
		int unknown2 = stream.readUnsignedByte();
		stream.skip(24); // 24bytes directly from a file, no idea whats there
		String mac = stream.readString();
		String settings = stream.readString();
		int affid = stream.readInt();
		stream.skip(stream.readUnsignedByte()); // useless settings value is 36
		int unknown3 = stream.readInt();
		long userFlow = stream.readLong();
		boolean hasAditionalInformation = stream.readUnsignedByte() == 1;
		if (hasAditionalInformation)
			stream.readString(); // aditionalInformation
		boolean hasJagtheora = stream.readUnsignedByte() == 1;
		boolean js = stream.readUnsignedByte() == 1;
		boolean hc = stream.readUnsignedByte() == 1;
		int unknown4 = stream.readByte();
		int unknown5 = stream.readInt();
		String unknown6 = stream.readString();
		boolean unknown7 = stream.readUnsignedByte() == 1;
		for (int index = 0; index < Cache.STORE.getIndexes().length; index++) {
			int crc = Cache.STORE.getIndexes()[index] == null ? -1011863738 : Cache.STORE.getIndexes()[index].getCRC();
			int receivedCRC = stream.readInt();
			/*
			 * System.out.println(crc + " - " + receivedCRC + " - " + index); if
			 * (crc != receivedCRC && index < 32) { if
			 * (!Constants.ALLOW_ALL_CLIENTS) {
			 * session.getLoginPackets().sendClientPacket(6); return; } }
			 */
		}
		Player player = new Player(password);
		long inUse = Runtime.getRuntime().totalMemory();
		System.out.println("[memoryUsage=" + ServerInformation.get().readable(inUse, true) + ", players=" + World.getPlayers().size() + "]");

		player.init(session, username, displayMode, screenWidth, screenHeight, new IsaacKeyPair(isaacKeys));
		session.getLoginPackets().sendLoginDetails(player);
		session.setDecoder(3, player);
		session.setEncoder(2, player);
		player.setPassword(password);
		player.start();
	}

	
	private final void decodeLogin(InputStream stream) {
		if (stream.getRemaining() != 0) {
			session.getChannel().close();
			System.out.println("Remaining from decoding login wasnt 0 so closed session");
			return;
		}
		session.setDecoder(2);
		session.setEncoder(1);
		session.getLoginPackets().sendStartUpPacket();
	}

	private final void decodeGrab(InputStream stream) {
		if (stream.getRemaining() != 8) {
			System.out.println("Invalid remaining amount: " + stream.getRemaining());
			session.getChannel().close();
			return;
		}
		session.setEncoder(0);
		int build = stream.readInt();
		boolean readsub = true;
		if (readsub) {
			int sub = stream.readInt();
			if (build != Constants.REVISION || sub != Constants.CUSTOM_CLIENT_BUILD) {
				session.setDecoder(-1);
				session.getGrabPackets().sendOutdatedClientPacket();
				return;
			}
		}
		session.setDecoder(1);
		session.getGrabPackets().sendStartUpPacket();
	}
}
