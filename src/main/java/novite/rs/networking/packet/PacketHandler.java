package novite.rs.networking.packet;

import novite.rs.game.player.Player;
import novite.rs.networking.codec.stream.InputStream;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 19, 2014
 */
public abstract class PacketHandler {

	/**
	 * Handles an incoming packet
	 * 
	 * @param player
	 *            The player
	 * @param packetId
	 *            The packet id
	 * @param length
	 *            The length of the packet
	 * @param stream
	 *            The packet stream
	 */
	public abstract void handle(Player player, Integer packetId, Integer length, InputStream stream);

}
