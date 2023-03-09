package novite.rs.networking.protocol.js5;

import novite.rs.cache.Cache;
import novite.rs.networking.Session;
import novite.rs.networking.codec.Decoder;
import novite.rs.networking.codec.stream.InputStream;

public class GrabPacketsDecoder extends Decoder {

	public GrabPacketsDecoder(Session connection) {
		super(connection);
	}

	@Override
	public final void decode(InputStream stream) {
		while (stream.getRemaining() > 0 && session.getChannel().isConnected()) {
			int packetId = stream.readUnsignedByte();
			if (packetId == 0 || packetId == 1) {
				decodeRequestCacheContainer(stream, packetId == 1);
			} else {
				decodeOtherPacket(stream, packetId);
			}
		}
	}

	private final void decodeRequestCacheContainer(InputStream stream, boolean priority) {
		int indexId = stream.readUnsignedByte();
		int archiveId = stream.readInt();
		if (archiveId < 0) {
			System.out.println("Fake request: " + indexId + ", " + archiveId);
			return;
		}
		if (indexId != 255) {
			if (Cache.STORE.getIndexes().length <= indexId || Cache.STORE.getIndexes()[indexId] == null || !Cache.STORE.getIndexes()[indexId].archiveExists(archiveId)) {
				return;
			}
		} else if (archiveId != 255) {
			if (Cache.STORE.getIndexes().length <= archiveId || Cache.STORE.getIndexes()[archiveId] == null) {
				return;
			}
		}
		session.getGrabPackets().sendCacheArchive(indexId, archiveId, priority);
	}

	private final void decodeOtherPacket(InputStream stream, int packetId) {
		if (packetId == 7) {
			session.getChannel().close();
			System.out.println("Received packet 7 so closed session.");
			return;
		}
		if (packetId == 4) {
			session.getGrabPackets().setEncryptionValue(stream.readUnsignedByte());
			if (stream.readUnsignedShort() != 0) {
				System.out.println("Received packet != 0 so closed session.");
				session.getChannel().close();
			}
		} else {
			stream.skip(3);
		}
	}
}
