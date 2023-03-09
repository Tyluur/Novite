package novite.rs.api.event.listeners.interfaces;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.clans.ClansManager;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Dec 12, 2013
 */
public class ClanChatButtonEvent extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 1110, 1096, 1089, 1105 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		switch (interfaceId) {
		case 1110: // the tab
			switch (buttonId) {
			case 85: // join urs
				ClansManager.joinClanChatChannel(player);
				break;
			case 76: // clan details
				ClansManager.openClanDetails(player);
				break;
			case 80: // clan settings
				ClansManager.openClanSettings(player);
				break;
			case 115: // leave
				ClansManager.leaveClan(player);
				break;
			case 95: // join another
				ClansManager.joinGuestClanChat(player);
				break;
			case 100: // ban a player from the clan
				ClansManager.banPlayer(player);
				break;
			case 105: // remove a player from the clan ban list
				ClansManager.unbanPlayer(player);
				break;
			case 11:
				ClansManager.unbanPlayer(player, slotId);
				break;
			}
			break;
		case 1096:
			if (buttonId == 111) {
				ClansManager.viewClammateDetails(player, slotId);
			} else if (buttonId == 94) {
				ClansManager.switchGuestsInChatCanEnterInterface(player);
			} else if (buttonId == 95) {
				ClansManager.switchGuestsInChatCanTalkInterface(player);
			} else if (buttonId == 96) {
				ClansManager.switchRecruitingInterface(player);
			} else if (buttonId == 97) {
				ClansManager.switchClanTimeInterface(player);
			} else if (buttonId == 118) {
				ClansManager.openClanMottifInterface(player);
			} else if (buttonId == 131) {
				ClansManager.openClanMottoInterface(player);
			} else if (buttonId == 240) {
				ClansManager.setTimeZoneInterface(player, -720 + slotId * 10);
			} else if (buttonId == 262) {
				player.getTemporaryAttributtes().put("editclanmatejob", slotId);
			} else if (buttonId == 276) {
				player.getTemporaryAttributtes().put("editclanmaterank", slotId);
			} else if (buttonId == 309) {
				ClansManager.kickClanmate(player);
			} else if (buttonId == 318) {
				ClansManager.saveClanmateDetails(player);
			} else if (buttonId == 290) {
				ClansManager.setWorldIdInterface(player, slotId);
			} else if (buttonId == 297) {
				ClansManager.openForumThreadInterface(player);
			} else if (buttonId == 346) {
				ClansManager.openNationalFlagInterface(player);
			} else if (buttonId == 118) {
				ClansManager.showClanSettingsClanMates(player);
			} else if (buttonId == 113) {
				ClansManager.showClanSettingsSettings(player);
			} else if (buttonId == 386) {
				ClansManager.showClanSettingsPermissions(player);
			} else if (buttonId == 489) {
				ClansManager.selectPermissionTab(player, 1);
			} else if (buttonId == 498) {
				ClansManager.selectPermissionTab(player, 2);
			} else if (buttonId == 506) {
				ClansManager.selectPermissionTab(player, 3);
			} else if (buttonId == 514) {
				ClansManager.selectPermissionTab(player, 4);
			} else if (buttonId == 522) {
				ClansManager.selectPermissionTab(player, 5);
			} else if (buttonId >= 395 && buttonId <= 475) {
				int selectedRank = (buttonId - 395) / 8;
				if (selectedRank == 10) {
					selectedRank = 125;
				} else if (selectedRank > 5) {
					selectedRank = 100 + selectedRank - 6;
				}
				// ClansManager.selectPermissionRank(player, selectedRank);
			}
			break;
		case 1105:
			if (buttonId == 63 || buttonId == 66) {
				ClansManager.setClanMottifTextureInterface(player, buttonId == 66, slotId);
			} else if (buttonId == 194) {
				player.getPackets().sendHideIComponent(1105, 35, false);
				player.getPackets().sendHideIComponent(1105, 36, false);
				player.getPackets().sendHideIComponent(1105, 37, false);
				player.getPackets().sendHideIComponent(1105, 37, false);
				player.getPackets().sendHideIComponent(1105, 38, false);
				player.getPackets().sendHideIComponent(1105, 39, false);
				player.getPackets().sendHideIComponent(1105, 43, false);
				player.getPackets().sendHideIComponent(1105, 44, false);
				player.getPackets().sendHideIComponent(1105, 45, false);
			} else if (buttonId == 182) {
				player.getPackets().sendHideIComponent(1105, 62, false);
				player.getPackets().sendHideIComponent(1105, 63, false);
				player.getPackets().sendHideIComponent(1105, 69, false);
			} else if (buttonId == 35) {
				ClansManager.openSetMottifColor(player, 0);
			} else if (buttonId == 80) {
				ClansManager.openSetMottifColor(player, 1);
			} else if (buttonId == 92) {
				ClansManager.openSetMottifColor(player, 2);
			} else if (buttonId == 104) {
				ClansManager.openSetMottifColor(player, 3);// try
			} else if (buttonId == 123) {
				player.stopAll();
			}
			break;
		case 1089: // flags
			if (buttonId == 30) {
				player.getTemporaryAttributtes().put("clanflagselection", slotId);
			} else if (buttonId == 26) {
				Integer flag = (Integer) player.getTemporaryAttributtes().remove("clanflagselection");
				player.stopAll();
				if (flag != null) {
					ClansManager.setClanFlagInterface(player, flag);
				}
			}
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
