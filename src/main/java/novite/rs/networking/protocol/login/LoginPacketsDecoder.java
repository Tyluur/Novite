package novite.rs.networking.protocol.login;

import novite.Main;
import novite.rs.Constants;
import novite.rs.api.database.ForumIntegration;
import novite.rs.api.database.ForumIntegration.IntegrationReturns;
import novite.rs.cache.Cache;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.networking.ConnectionFilteration;
import novite.rs.networking.Session;
import novite.rs.networking.codec.Decoder;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.utility.IsaacKeyPair;
import novite.rs.utility.Saving;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ReturnCode;
import novite.rs.utility.game.json.impl.PunishmentLoader;
import novite.rs.utility.game.punishments.Punishment.PunishmentType;

public final class LoginPacketsDecoder extends Decoder {

	public LoginPacketsDecoder(Session session) {
		super(session);
	}

	@Override
	public void decode(InputStream stream) {
		session.setDecoder(-1);
		if (Main.STARTUP_TIME == -1) {
			session.getLoginPackets().sendClientPacket(ReturnCode.SERVER_IS_UPDATING);
			return;
		}
		int packetId = stream.readUnsignedByte();
		if (packetId == 16) {
			decodeWorldLogin(stream);
		} else {
			if (Constants.DEBUG) {
				System.out.println("PacketId " + packetId);
			}
			session.getChannel().close();
			System.out.println("Didnt receive login packet of 16[" + packetId + "] so closed session");
		}
	}

	private static final Object LOCK = new Object();

	public void decodeWorldLogin(InputStream stream) {
		if (World.exiting_start != 0) {
			session.getLoginPackets().sendClientPacket(ReturnCode.SERVER_IS_UPDATING);
			return;
		}
		int packetSize = stream.readUnsignedShort();
		if (packetSize != stream.getRemaining()) {
			System.out.println("Packet size was not appropriate so closed session.");
			session.getChannel().close();
			return;
		}
		if (stream.readInt() != Constants.REVISION || stream.readInt() != Constants.CUSTOM_CLIENT_BUILD) {
			session.getLoginPackets().sendClientPacket(ReturnCode.NOVITE_HAS_BEEN_UPDATED);
			return;
		}
		stream.readUnsignedByte();
		if (stream.readUnsignedByte() != 10) { // rsa block check
			session.getLoginPackets().sendClientPacket(ReturnCode.INVALID_SESSION_ID);
			return;
		}
		int[] isaacKeys = new int[4];
		for (int i = 0; i < isaacKeys.length; i++) {
			isaacKeys[i] = stream.readInt();
		}
		if (stream.readLong() != 0L) { // rsa block check, pass part
			session.getLoginPackets().sendClientPacket(ReturnCode.INVALID_SESSION_ID);
			return;
		}
		String password = stream.readString();
		Utils.longToString(stream.readLong());
		stream.readLong(); // random value
		stream.decodeXTEA(isaacKeys, stream.getOffset(), stream.getLength());
		String username = Utils.formatPlayerNameForProtocol(stream.readString());
		stream.readUnsignedByte(); // unknown
		int displayMode = stream.readUnsignedByte();
		int screenWidth = stream.readUnsignedShort();
		int screenHeight = stream.readUnsignedShort();
		stream.readUnsignedByte();
		stream.skip(24); // 24bytes directly from a file, no idea whats there
		stream.readString();
		stream.readInt();
		stream.skip(stream.readUnsignedByte()); // useless settings
		if (stream.readUnsignedByte() != 5) {
			session.getLoginPackets().sendClientPacket(ReturnCode.INVALID_SESSION_ID);
			return;
		}
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		stream.readUnsignedShort();
		stream.readUnsignedByte();
		stream.read24BitInt();
		stream.readUnsignedShort();
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		stream.readJagString();
		stream.readJagString();
		stream.readJagString();
		stream.readJagString();
		stream.readUnsignedByte();
		stream.readUnsignedShort();
		stream.readInt();
		stream.readLong();
		boolean hasAditionalInformation = stream.readUnsignedByte() == 1;
		if (hasAditionalInformation) {
			stream.readString(); // aditionalInformation
		}
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		for (int index = 0; index < Cache.STORE.getIndexes().length; index++) {
			int crc = Cache.STORE.getIndexes()[index] == null ? 0 : Cache.STORE.getIndexes()[index].getCRC();
			int receivedCRC = stream.readInt();
			if (crc != receivedCRC && index < 32) {
				session.getLoginPackets().sendClientPacket(ReturnCode.NOVITE_HAS_BEEN_UPDATED);
				return;
			}
		}
		synchronized (LOCK) {
			if (Utils.invalidAccountName(username)) {
				session.getLoginPackets().sendClientPacket(ReturnCode.INVALID_USERNAME);
				return;
			}
			if (World.containsPlayer(username)) {
				session.getLoginPackets().sendClientPacket(ReturnCode.YOUR_ACCOUNT_IS_STILL_ONLINE);
				return;
			}
			if (Constants.isVPS && ConnectionFilteration.getAmountConnected(session.getIP()) > 3) {
				session.getLoginPackets().sendClientPacket(ReturnCode.LOGIN_LIMIT_EXCEEDED);
				return;
			}
			if (Constants.SQL_ENABLED) {
				try {
					IntegrationReturns returns = ForumIntegration.correctCredentials(username, password);
					switch (returns) {
					case WRONG_CREDENTIALS:
						session.getLoginPackets().sendClientPacket(ReturnCode.INVALID_USERNAME_OR_PASSWORD);
						return;
					case SQL_ERROR:
						session.getLoginPackets().sendClientPacket(ReturnCode.DATABASE_CONNECTION_ERROR);
						return;
					case NON_EXISTANT_USERNAME:
						ForumIntegration.registerUser(username, password);
						break;
					default:
						break;
					}
				} catch (Throwable t) {
					t.printStackTrace();
					session.getLoginPackets().sendClientPacket(ReturnCode.UNABLE_TO_CONNECT_LOGINSERVER);
					return;
				}
			}
			boolean newPlayer = false;
			Player player;
			if (!Saving.containsPlayer(username)) {
				player = new Player(password);
				newPlayer = true;
			} else {
				try {
					player = Saving.loadPlayer(username);
					if (player == null) {
						session.getLoginPackets().sendClientPacket(ReturnCode.NULLED_ACCOUNT);
						return;
					}
				} catch (Exception e) {
					session.getLoginPackets().sendClientPacket(ReturnCode.NULLED_ACCOUNT);
					return;
				}
			}
			if (PunishmentLoader.isBanned(username, PunishmentType.BAN) || PunishmentLoader.isBanned(session.getIP(), PunishmentType.IPBAN)) {
				session.getLoginPackets().sendClientPacket(ReturnCode.YOUR_ACCOUNT_HAS_BEEN_DISABLED);
				return;
			}
			World.getLoginWorker().offer(player, new Object[] { session, username, displayMode, screenWidth, screenHeight, new IsaacKeyPair(isaacKeys), newPlayer });
		}
	}

}
