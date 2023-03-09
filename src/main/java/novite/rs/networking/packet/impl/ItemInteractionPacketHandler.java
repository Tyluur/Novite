package novite.rs.networking.packet.impl;

import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.Animation;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.item.FloorItem;
import novite.rs.game.player.Player;
import novite.rs.game.player.RouteEvent;
import novite.rs.game.player.content.exchange.ExchangeManagement;
import novite.rs.networking.codec.handlers.InventoryOptionsHandler;
import novite.rs.networking.codec.handlers.ObjectHandler;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketInformation;
import novite.rs.utility.ItemExamines;
import novite.rs.utility.Utils;
import novite.rs.utility.logging.types.FileLogger;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 19, 2014
 */
@PacketInformation(listeners = "73,27,42,13,24")
public class ItemInteractionPacketHandler extends PacketHandler {

	private static final int ITEM_ON_ITEM = 73;
	private final static int ITEM_EXAMINE_PACKET = 27;
	private final static int ITEM_ON_OBJECT_PACKET = 42;
	private final static int GRAND_EXCHANGE_SELECTION = 13;
	private final static int ITEM_TAKE_PACKET = 24;

	@Override
	public void handle(Player player, Integer packetId, Integer length, InputStream stream) {
		switch (packetId) {
		case ITEM_TAKE_PACKET:
			handleGroundItem(player, packetId, length, stream);
			break;
		case ITEM_ON_ITEM:
			InventoryOptionsHandler.handleItemOnItem(player, stream);
			break;
		case ITEM_EXAMINE_PACKET:
			final int id = stream.readUnsignedShort128();
			stream.readByte();
			int y = stream.readUnsignedShort();
			int x = stream.readUnsignedShortLE();
			final WorldTile tile = new WorldTile(x, y, player.getPlane());
			final int regionId = tile.getRegionId();
			final FloorItem item = World.getRegion(regionId).getFloorItem(id, tile, player);
			if (item == null) {
				return;
			}
			player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
			break;
		case ITEM_ON_OBJECT_PACKET:
			ObjectHandler.handleItemOnObject(player, stream);
			break;
		case GRAND_EXCHANGE_SELECTION:
			int itemId = stream.readShort();
			if (!ItemDefinitions.getItemDefinitions(itemId).isExchangeable()) // incase
																				// user
																				// somehow
																				// modifies
																				// the
																				// client
																				// list
				return;
			ExchangeManagement.chooseBuyItem(player, itemId);
			break;
		}
	}

	private void handleGroundItem(final Player player, Integer packetId, Integer length, InputStream stream) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead()) {
			return;
		}
		long currentTime = Utils.currentTimeMillis();
		if (player.getLockDelay() > currentTime) {
			return;
		}
		final int id = stream.readUnsignedShort128();

		stream.readByte();
		int y = stream.readUnsignedShort();
		int x = stream.readUnsignedShortLE();
		final WorldTile tile = new WorldTile(x, y, player.getPlane());
		final int regionId = tile.getRegionId();
		if (!player.getMapRegionsIds().contains(regionId)) {
			return;
		}
		final FloorItem item = World.getRegion(regionId).getFloorItem(id, tile, player);
		if (item == null) {
			return;
		}
		player.stopAll();
		player.setRouteEvent(new RouteEvent(item, new Runnable() {
			@Override
			public void run() {
				final FloorItem item = World.getRegion(regionId).getFloorItem(id, tile, player);
				if (item == null) {
					return;
				}
				player.setNextAnimation(new Animation(-1));
				player.setNextFaceWorldTile(tile);
				World.removeGroundItem(player, item);
				String owner = null;
				if (item.getOwner() != null && item.getOwner().getDisplayName() != null)
					owner = item.getOwner().getDisplayName();
				FileLogger.getFileLogger().writeLog("pickup/", player.getDisplayName() + " picked up " + item.getAmount() + "x [itemId=" + item.getId() + ", name=" + item.getName() + "] at region: " + regionId + ". Dropee: " + owner, true);
			}
		}, true));
	}

}
