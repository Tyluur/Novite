package novite.rs.api.event.listeners.interfaces;

import novite.rs.api.event.EventListener;
import novite.rs.api.input.IntegerInputAction;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.actions.summoning.Summoning;
import novite.rs.networking.protocol.game.DefaultGameDecoder;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 5, 2014
 */
public class SummoningCreation extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 666, 672 };
	}

	@Override
	public boolean handleButtonClick(final Player player, int interfaceId, int buttonId, int packetId, final int slotId, final int itemId) {
		if (interfaceId == 672) {
			switch (buttonId) {
			case 16:
				switch (packetId) {
				case DefaultGameDecoder.ACTION_BUTTON1_PACKET:
					Summoning.handlePouchInfusion(player, slotId, 1);
					return true;
				case DefaultGameDecoder.ACTION_BUTTON2_PACKET:
					Summoning.handlePouchInfusion(player, slotId, 5);
					return true;
				case DefaultGameDecoder.ACTION_BUTTON3_PACKET:
					Summoning.handlePouchInfusion(player, slotId, 10);
					return true;
				case DefaultGameDecoder.ACTION_BUTTON4_PACKET:
					player.getPackets().sendInputIntegerScript("Enter amount:", new IntegerInputAction() {

						@Override
						public void handle(int input) {
							Summoning.handlePouchInfusion(player, slotId, input);
						}
					});
					return true;
				case DefaultGameDecoder.ACTION_BUTTON5_PACKET:
					Summoning.handlePouchInfusion(player, slotId, 28);
					return true;
				case DefaultGameDecoder.ACTION_BUTTON6_PACKET:
					Summoning.sendItemList(player, (boolean) player.getTemporaryAttributtes().get("infusing_scroll"), 1, slotId);
					return true;
				}
				return true;
			case 19:
				Summoning.switchInfusionOption(player);
				return true;
			}
		} else {
			switch (buttonId) {
			case 16:
				switch (packetId) {
				case DefaultGameDecoder.ACTION_BUTTON1_PACKET:
					Summoning.createScroll(player, itemId, 1);
					break;
				case DefaultGameDecoder.ACTION_BUTTON2_PACKET:
					Summoning.createScroll(player, itemId, 5);
					break;
				case DefaultGameDecoder.ACTION_BUTTON3_PACKET:
					Summoning.createScroll(player, itemId, 10);
					break;
				case DefaultGameDecoder.ACTION_BUTTON4_PACKET:
					Summoning.createScroll(player, itemId, 28);
					break;
				case DefaultGameDecoder.ACTION_BUTTON5_PACKET:
					player.getPackets().sendInputIntegerScript("Enter amount:", new IntegerInputAction() {

						@Override
						public void handle(int input) {
							Summoning.createScroll(player, itemId, input);
						}
					});
					break;
				case DefaultGameDecoder.ACTION_BUTTON9_PACKET:
					Summoning.sendItemList(player, (boolean) player.getTemporaryAttributtes().get("infusing_scroll"), 1, slotId);
					break;
				}
				break;
			case 18:
				Summoning.switchInfusionOption(player);
				return true;
			}
		}
		return true;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		return false;
	}

	@Override
	public boolean handleNPCClick(Player player, NPC npc, ClickOption option) {
		return false;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		return false;
	}

}
