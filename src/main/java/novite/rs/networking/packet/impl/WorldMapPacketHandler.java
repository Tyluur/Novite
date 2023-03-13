package novite.rs.networking.packet.impl;

import novite.rs.game.player.Player;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketInformation;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 19, 2014
 */
@PacketInformation(listeners = "89")
public class WorldMapPacketHandler extends PacketHandler {

	@Override
	public void handle(Player player, Integer packetId, Integer length, InputStream stream) {
		int coordinateHash = stream.readInt();
		int x = coordinateHash >> 14;
		int y = coordinateHash & 0x3fff;
		int plane = coordinateHash >> 28;
		Integer hash = (Integer) player.getTemporaryAttributtes().get("worldHash");
		if (hash == null || coordinateHash != hash) {
			player.getTemporaryAttributtes().put("worldHash", coordinateHash);
		} else {
			player.getTemporaryAttributtes().remove("worldHash");
			player.getHintIconsManager().addHintIcon(x, y, plane, 20, 0, 2, -1, true);
			player.getVarsManager().sendVar(1159, coordinateHash);
		}
	}

}
