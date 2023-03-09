package novite.rs.game.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import novite.rs.game.World;
import novite.rs.game.player.clans.ClansManager;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;
import novite.rs.utility.game.GlobalPlayerInfo;
import novite.rs.utility.game.ServerInformation;

public class InterfaceManager {

	public static final int FIXED_WINDOW_ID = 548;
	public static final int RESIZABLE_WINDOW_ID = 746;
	public static final int CHAT_BOX_TAB = 13;
	public static final int FIXED_SCREEN_TAB_ID = 9;
	public static final int RESIZABLE_SCREEN_TAB_ID = 12;
	public static final int FIXED_INV_TAB_ID = 199;
	public static final int RESIZABLE_INV_TAB_ID = 87;

	private Player player;

	private final ConcurrentHashMap<Integer, int[]> openedinterfaces = new ConcurrentHashMap<Integer, int[]>();

	private boolean clientActive;
	private boolean resizableScreen;
	private int windowPane;

	public InterfaceManager(Player player) {
		this.player = player;
	}

	public void sendTab(int tabId, int interfaceId) {
		player.getPackets().sendInterface(true, resizableScreen ? 746 : 548, tabId, interfaceId);
	}

	public void sendScreenInterface(int backgroundInterface, int interfaceId) {
		player.getInterfaceManager().closeScreenInterface();
		if (hasResizableScreen()) {
			player.getPackets().sendInterface(false, RESIZABLE_WINDOW_ID, 80, backgroundInterface);
			player.getPackets().sendInterface(false, RESIZABLE_WINDOW_ID, 81, interfaceId);
		} else {
			player.getPackets().sendInterface(false, FIXED_WINDOW_ID, 19, backgroundInterface);
			player.getPackets().sendInterface(false, FIXED_WINDOW_ID, 20, interfaceId);
		}
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				if (hasResizableScreen()) {
					player.getPackets().closeInterface(80);
					player.getPackets().closeInterface(81);
				} else {
					player.getPackets().closeInterface(19);
					player.getPackets().closeInterface(20);
				}
			}
		});
	}

	public boolean removeTab(int tabId) {
		return openedinterfaces.remove(Integer.valueOf(tabId)) != null;
	}

	public void sendChatBoxInterface(int interfaceId) {
		player.getPackets().sendInterface(true, 752, 13, interfaceId);
	}

	public void closeChatBoxInterface() {
		player.getPackets().closeInterface(13);
	}

	public int getTabWindow(int tabId) {
		if (!openedinterfaces.containsKey(Integer.valueOf(tabId)))
			return 548;
		else
			return ((int[]) openedinterfaces.get(Integer.valueOf(tabId)))[1];
	}

	public boolean containsChatBoxInter() {
		return containsTab(13);
	}

	public boolean containsTab(int tabId) {
		return openedinterfaces.containsKey(Integer.valueOf(tabId));
	}

	public void setOverlay(final int interfaceId, final boolean fullScreen) {
		sendTab(player.getInterfaceManager().hasResizableScreen() ? 10 : 9, interfaceId);
	}

	public void removeOverlay(boolean fullScreen) {
		removeTab(player.getInterfaceManager().hasResizableScreen() ? 10 : 9);
	}

	public void sendInterface(int interfaceId) {
		player.getPackets().sendInterface(false, resizableScreen ? 746 : 548, resizableScreen ? 12 : 9, interfaceId);
	}

	public void sendInventoryInterface(int childId) {
		player.getPackets().sendInterface(false, resizableScreen ? 746 : 548, resizableScreen ? 87 : 199, childId);
	}

	public final void sendInterfaces() {
		if (player.getDisplayMode() == 2 || player.getDisplayMode() == 3) {
			resizableScreen = true;
			sendFullScreenInterfaces();
		} else {
			resizableScreen = false;
			sendFixedInterfaces();
		}
		player.getCombatDefinitions().sendUnlockAttackStylesButtons();
		player.getMusicsManager().unlockMusicPlayer();
		player.getEmotesManager().unlockEmotesBook();
		player.getInventory().unlockInventoryOptions();
		player.getPrayer().unlockPrayerBookButtons();
		ClansManager.unlockBanList(player);
		if (player.getFamiliar() != null && player.isRunning()) {
			player.getFamiliar().unlock();
		}
		player.getControllerManager().sendInterfaces();
		setClientActive(true);
	}

	public void replaceRealChatBoxInterface(int interfaceId) {
		player.getPackets().sendInterface(true, 752, 12, interfaceId);
	}

	public void closeReplacedRealChatBoxInterface() {
		player.getPackets().closeInterface(752, 12);
	}

	public boolean containsScreenInterface() {
		return containsTab(resizableScreen ? RESIZABLE_SCREEN_TAB_ID : FIXED_SCREEN_TAB_ID);
	}

	public void sendFullScreenInterfaces() {
		player.getPackets().sendWindowsPane(746, 0);
		sendTab(15, 745);
		sendTab(19, 751);
		sendTab(73, 752); // chatbox tab id
		player.getPackets().sendInterface(true, 752, 9, 137);
		sendTab(177, 748);
		sendTab(178, 749);
		sendTab(179, 750);
		sendTab(180, 747);
		sendCombatStyles();
		sendTaskTab(true);
		sendSkills();
		sendInfoTab();
		sendInventory();
		sendEquipment();
		sendPrayerBook();
		sendMagicBook();
		sendFriends();
		sendFriendsChat();
		sendClanChat();
		sendSettings();
		sendEmotes();
		sendMusicTab();
		// sendNotesTab();
		sendLogoutTab();
	}

	public void sendFixedInterfaces() {
		player.getPackets().sendWindowsPane(548, 0);
		sendTab(15, 745);
		sendTab(68, 751);
		sendTab(192, 752);
		player.getPackets().sendInterface(true, 752, 9, 137);
		sendTab(17, 754);
		sendTab(183, 748);
		sendTab(185, 749);
		sendTab(186, 750);
		sendTab(188, 747);
		sendCombatStyles();// combat styles
		sendTaskTab(true);
		sendCombatStyles();
		sendSkills();// skills
		sendInfoTab();// info tab
		sendInventory();
		sendEquipment();
		sendPrayerBook();
		sendMagicBook();
		sendFriends();
		sendFriendsChat();// friends chat
		sendClanChat();// clan chat
		sendSettings();
		sendEmotes();// emotes
		sendMusicTab();// music
		// Notes Interface
		// sendNotesTab();
		sendLogoutTab(); // Logout tab
	}

	public void sendLogoutTab() {
		sendTab(resizableScreen ? 108 : 222, 182);
	}

	public void closeLogoutTab() {
		removeTab(resizableScreen ? 108 : 222);
	}

	public void sendNotesTab() {
		sendTab(resizableScreen ? 105 : 219, 34);
	}

	public void closeNotesTab() {
		removeTab(resizableScreen ? 105 : 219);
	}

	public void sendInfoTab() {
		int interfaceId = 34;
		if (!containsInterface(interfaceId)) {
			sendTab(resizableScreen ? 93 : 207, interfaceId);
		}
		player.getPackets().sendIComponentText(interfaceId, 1, "Quests");
		player.getPackets().sendHideIComponent(interfaceId, 8, true);
	}

	public void closeInfoTab() {
		removeTab(resizableScreen ? 93 : 207);
	}

	public void sendMusicTab() {
		sendTab(resizableScreen ? 104 : 218, 187);
	}

	public void closeMusicTab() {
		removeTab(resizableScreen ? 104 : 218);
	}

	public void sendClanChat() {
		sendTab(resizableScreen ? 101 : 215, 1110);
	}

	public void closeClanChat() {
		removeTab(resizableScreen ? 101 : 215);
	}

	public void sendFriendsChat() {
		sendTab(resizableScreen ? 100 : 214, 1109);
	}

	public void closeFriendsChat() {
		removeTab(resizableScreen ? 100 : 214);
	}

	public void sendEmotes() {
		sendTab(resizableScreen ? 103 : 217, 464);
	}

	public void closeEmotes() {
		removeTab(resizableScreen ? 103 : 217);
	}

	public void sendFriends() {
		sendTab(resizableScreen ? 99 : 213, 550);
	}

	public void closeFriends() {
		removeTab(resizableScreen ? 99 : 213);
	}

	public void sendSkills() {
		sendTab(resizableScreen ? 92 : 206, 320);
	}

	public void closeSkills() {
		removeTab(resizableScreen ? 92 : 206);
	}

	public void sendCombatStyles() {
		sendTab(resizableScreen ? 90 : 204, 884);
	}

	public void closeCombatStyles() {
		removeTab(resizableScreen ? 90 : 204);
	}

	public void sendEquipment() {
		sendTab(resizableScreen ? 95 : 209, 387);
	}

	public void closeEquipment() {
		removeTab(resizableScreen ? 95 : 209);
	}

	public void sendInventory() {
		sendTab(resizableScreen ? 94 : 208, Inventory.INVENTORY_INTERFACE);
	}

	public void closeInventory() {
		removeTab(resizableScreen ? 94 : 208);
	}

	public void sendSettings() {
		sendSettings(261);
	}

	public void sendSettings(int interfaceId) {
		sendTab(resizableScreen ? 102 : 216, interfaceId);
	}

	public void sendPrayerBook() {
		sendTab(resizableScreen ? 96 : 210, 271);
	}

	public void closePrayerBook() {
		removeTab(resizableScreen ? 96 : 210);
	}

	public void sendMagicBook() {
		sendTab(resizableScreen ? 97 : 211, player.getCombatDefinitions().getSpellBook());
	}

	public void closeMagicBook() {
		removeTab(resizableScreen ? 97 : 211);
	}

	public void closeScreenInterface() {
		player.getPackets().closeInterface(resizableScreen ? 12 : 9);
	}

	public boolean containsInventoryInter() {
		return containsTab(resizableScreen ? 87 : 199);
	}

	public void closeInventoryInterface() {
		player.getPackets().closeInterface(resizableScreen ? RESIZABLE_INV_TAB_ID : FIXED_INV_TAB_ID);
	}

	public boolean addInterface(int windowId, int tabId, int childId) {
		if (openedinterfaces.containsKey(Integer.valueOf(tabId)))
			player.getPackets().closeInterface(tabId);
		openedinterfaces.put(Integer.valueOf(tabId), new int[] { childId, windowId });
		return ((int[]) openedinterfaces.get(Integer.valueOf(tabId)))[0] == childId;
	}

	public boolean containsInterface(int tabId, int childId) {
		if (childId == windowPane)
			return true;
		if (!openedinterfaces.containsKey(Integer.valueOf(tabId)))
			return false;
		return ((int[]) openedinterfaces.get(Integer.valueOf(tabId)))[0] == childId;
	}

	public boolean containsInterface(int childId) {
		if (childId == windowPane)
			return true;
		for (Iterator<int[]> iterator = openedinterfaces.values().iterator(); iterator.hasNext();) {
			int value[] = (int[]) iterator.next();
			if (value[0] == childId)
				return true;
		}
		return false;
	}

	public void removeAll() {
		openedinterfaces.clear();
	}

	public void setFadingInterface(int backgroundInterface) {
		sendTab(hasResizableScreen() ? 12 : 11, backgroundInterface);
	}

	public void closeFadingInterface() {
		removeTab(hasResizableScreen() ? 12 : 11);
	}

	public void setScreenInterface(int backgroundInterface, int interfaceId) {
		System.out.println("Whats green!?!?!?");
		closeScreenInterface();
		sendTab(hasResizableScreen() ? 40 : 9, backgroundInterface);
		sendTab(hasResizableScreen() ? 41 : 11, interfaceId);

		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				System.out.println("We should remove tbh");
				removeTab(hasResizableScreen() ? 40 : 9);
				removeTab(hasResizableScreen() ? 41 : 11);
			}
		});
	}

	public boolean hasResizableScreen() {
		return resizableScreen;
	}

	public void setWindowsPane(int windowsPane) {
		this.windowPane = windowsPane;
	}

	public int getWindowsPane() {
		return windowPane;
	}

	public void sendTaskTab(boolean tab) {
		int interfaceId = 930;

		StringBuilder bldr = new StringBuilder();
		bldr.append("Uptime: <col=" + ChatColors.WHITE + ">" + ServerInformation.get().getGameUptime() + "<br>");
		bldr.append("Online: <col=" + ChatColors.WHITE + ">" + GlobalPlayerInfo.get().getPlayersOnline() + "<br>");

		bldr.append("<br>");

		bldr.append("Playtime</col>: <col=" + ChatColors.WHITE + ">" + player.getTimePlayed() + "<br>");
		bldr.append("Rank: <col=" + ChatColors.WHITE + ">" + Utils.formatPlayerNameForDisplay(player.getMainGroup().name()) + "<br>");
		if (player.getFacade().getModifiers() != null) {
			bldr.append("<br>Combat-XP: <col=" + ChatColors.WHITE + ">x" + player.getFacade().getModifiers()[Facade.COMBAT_MODIFIER_INDEX] + "<br>");
			bldr.append("Skill-XP: <col=" + ChatColors.WHITE + ">x" + player.getFacade().getModifiers()[Facade.SKILL_MODIFIER_INDEX] + "<br>");
			bldr.append("Drop-Rate: <col=" + ChatColors.WHITE + ">x" + player.getFacade().getModifiers()[Facade.LOOT_MODIFIER_INDEX] + "<br>");
			bldr.append("<br>");
		}

		bldr.append("Gold Points: <col=" + ChatColors.WHITE + ">" + Utils.format(player.getFacade().getGoldPoints()) + "<br>");
		bldr.append("Slay Points: <col=" + ChatColors.WHITE + ">" + Utils.format(player.getSlayerManager().getPoints()) + "<br>");

		if (player.getSlayerTask() != null) {
			bldr.append("<br>Task: <col=" + ChatColors.WHITE + ">" + player.getSlayerTask().getName() + "<br>");
			bldr.append("Amount: <col=" + ChatColors.WHITE + ">" + (player.getSlayerTask().getStartAmount() - player.getSlayerTask().getAmount()) + "/" + player.getSlayerTask().getStartAmount() + "<br>");
		}

		bldr.append("<br><col=" + ChatColors.RED + "><u>Staff Online<br><br>");

		List<Player> players = new ArrayList<Player>(World.getPlayers());
		Collections.sort(players, new Comparator<Player>() {

			@Override
			public int compare(Player o1, Player o2) {
				return ((Integer) o1.getRights()).compareTo(o2.getRights());
			}
		});
		Collections.reverse(players);
		int count = 0;
		for (Player p : players) {
			if (p == null)
				continue;
			if (count >= 10) {
				break;
			}
			if (p.getRights() > 0 || p.isSupporter()) {
				count++;
				bldr.append(count + ". <img=" + p.getChatIcon() + ">" + p.getDisplayName() + "<br>");
			}
		}
		/**
		 * Sending all of the text from the StringBuilder onto the interface.
		 */
		player.getPackets().sendIComponentText(interfaceId, 16, bldr.toString());
		/** Sending the title of the tab */
		player.getPackets().sendIComponentText(interfaceId, 10, "<col=" + ChatColors.RED + ">Re-open Tab to Refresh");

		if (tab) {
			sendTab(resizableScreen ? 91 : 205, interfaceId);
		}
	}

	public void closeTaskTab() {
		removeTab(resizableScreen ? 91 : 205);
	}

	/*
	 * returns lastGameTab
	 */
	public int openGameTab(int tabId) {
		player.getPackets().sendGlobalConfig(168, tabId);
		int lastTab = 4; // tabId
		// tab = tabId;
		return lastTab;
	}

	/**
	 * @return the clientActive
	 */
	public boolean isClientActive() {
		return clientActive;
	}

	/**
	 * @param clientActive
	 *            the clientActive to set
	 */
	public void setClientActive(boolean clientActive) {
		this.clientActive = clientActive;
	}

}
