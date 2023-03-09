package novite.rs.api.event.listeners.interfaces;

import novite.rs.Constants;
import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.loyalty.LoyaltyAura;
import novite.rs.game.player.content.loyalty.LoyaltyEffect;
import novite.rs.game.player.content.loyalty.LoyaltyManager;
import novite.rs.game.player.content.loyalty.LoyaltyTitle;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class LoyaltyInterfaceListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 1143 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		// System.out.println("[buttonId=" + buttonId + "]");
		switch (buttonId) {
		case 132: // auras
			player.getPackets().sendSound(7653, 0, 2);
			player.getPackets().sendConfigByFile(9487, 1);
			player.getLoyaltyManager().setCurrentTabConfig(1);
			break;
		case 111:
			player.getPackets().sendSound(7653, 0, 2);
			player.getPackets().sendConfigByFile(9487, 9);
			player.getLoyaltyManager().setCurrentTabConfig(9);
			break;
		case 137:
			player.getPackets().sendSound(7653, 0, 2);
			player.getPackets().sendConfigByFile(9487, 2);
			player.getLoyaltyManager().setCurrentTabConfig(2);
			break;
		case 138:
			player.getPackets().sendSound(7653, 0, 2);
			player.getPackets().sendConfigByFile(9487, 3);
			player.getLoyaltyManager().setCurrentTabConfig(3);
			break;
		case 143:
			player.getPackets().sendSound(7653, 0, 2);
			player.getPackets().sendConfigByFile(9487, 4);
			player.getLoyaltyManager().setCurrentTabConfig(4);
			break;
		case 148:
			player.getPackets().sendSound(7653, 0, 2);
			player.getPackets().sendConfigByFile(9487, 5);
			player.getLoyaltyManager().setCurrentTabConfig(5);
			break;
		case 153:
			player.getPackets().sendSound(7653, 0, 2);
			player.getPackets().sendConfigByFile(9487, 6);
			player.getLoyaltyManager().setCurrentTabConfig(6);
			break;
		case 117:
			player.getPackets().sendSound(7653, 0, 2);
			player.getPackets().sendConfigByFile(9487, 8);
			player.getLoyaltyManager().setCurrentTabConfig(8);
			break;
		case 109:
			player.getLoyaltyManager().closeStore();
			break;
		case 45:
			LoyaltyAura a = LoyaltyManager.getAuraByButton(slotId);
			if (a != null) {
				if (!player.getLoyaltyManager().hasUnlockedAura(a)) {
					player.getLoyaltyManager().setViewingSlot(slotId);
					player.getLoyaltyManager().forceBuyMessage(a);
					player.getTemporaryAttributtes().put("current_loyalty_purchase", a);
				} else {
					// Reclaiming.
					int id = a.getItemId();
					for (Item i : player.getBank().getContainerCopy()) {
						if (i.getId() == id) {
							player.getLoyaltyManager().showReclaimAlreadyOwned(true);
							return true;
						}
					}
					for (Item i : player.getInventory().getItems().getItemsCopy()) {
						if (i != null) {
							if (i.getId() == id) {
								player.getLoyaltyManager().showReclaimAlreadyOwned(false);
								return true;
							}
						}
					}
					player.getLoyaltyManager().showReclaimSuccess();
					player.getInventory().addItem(id, 1);
				}
			} else {
				player.getLoyaltyManager().setViewingSlot(-1);
				if (Constants.DEBUG) {
					System.out.println("Unavailable aura [buttonId=" + buttonId + ", slotId=" + slotId + "]");
				} else {
					player.getLoyaltyManager().showUnavailable();
				}
			}
			break;
		case 48: // titles
			LoyaltyTitle title = LoyaltyTitle.getLoyaltyTitleBySlot(slotId);
			if (title != null) {
				if (!player.getLoyaltyManager().hasUnlockedTitle(title)) {
					player.getLoyaltyManager().setViewingSlot(slotId);
					player.getLoyaltyManager().forceBuyMessage(title);
					player.getTemporaryAttributtes().put("current_loyalty_purchase", title);
				} else {
					player.getAppearence().setTitle(title.getValue());
					player.getAppearence().generateAppearenceData();
					player.getLoyaltyManager().showReclaimSuccess();
				}
			}
			break;
		case 30:
			player.getLoyaltyManager().confirmPurchase();
			break;
		case 210:
		case 31:
		case 39:
			player.getLoyaltyManager().closeMessage();// hideBuyMessage();
			break;
		case 103: // close
			player.getLoyaltyManager().closeStore();
			break;
		case 67: // effects
			LoyaltyEffect e = LoyaltyManager.getEffectByButton(slotId);
			if (e != null) {
				if (!player.getLoyaltyManager().hasUnlockedEffect(e)) {
					player.getLoyaltyManager().setViewingSlot(slotId);
					player.getLoyaltyManager().forceBuyMessage(e);
					player.getTemporaryAttributtes().put("current_loyalty_purchase", e);
				} else {
					// Reclaiming.
					int id = e.getItemId();
					for (Item i : player.getBank().getContainerCopy()) {
						if (i.getId() == id) {
							player.getLoyaltyManager().showReclaimAlreadyOwned(true);
							return true;
						}
					}
					for (Item i : player.getInventory().getItems().getItemsCopy()) {
						if (i != null) {
							if (i.getId() == id) {
								player.getLoyaltyManager().showReclaimAlreadyOwned(false);
								return true;
							}
						}
					}
					player.getLoyaltyManager().showReclaimSuccess();
					player.getInventory().addItem(id, 1);
				}
			} else {
				player.getLoyaltyManager().setViewingSlot(-1);
				player.getLoyaltyManager().showUnavailable();
			}
			break;
		case 46: // emotes
		case 47: // costumes
		case 49: // re-colour
			player.getPackets().sendHideIComponent(1143, 180, false);
			player.getLoyaltyManager().showUnavailable();
			break;
		default:
			if (Constants.DEBUG)
				System.out.println("Unhandled button event: [buttonId=" + buttonId + "]");
			break;
		}
		return true;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleNPCClick(Player player, NPC npc, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
	}

}
