package novite.rs.api.event.listeners.interfaces;

import java.util.ArrayList;
import java.util.List;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 20, 2014
 */
public class Scrollable extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 1142 };
	}

	/**
	 * Sends text over the scroll interface which has a maximum of 67 allowed
	 * lines in it and automatically formats the text to fit the lines
	 *
	 * @param player
	 *            The player to display the interface to
	 * @param title
	 *            The title of the interface
	 * @param messageList
	 *            The list of messages to write onto the interface.
	 */
	public static void sendScroll(Player player, String title, String... messageList) {
		player.closeInterfaces();
		int interfaceId = 1142;
		String text = "";
		int entries = 0;
		for (String message : messageList) {
			if (entries++ >= 66) {
				break;
			}
			text += message + "<br>";
		}
		player.getPackets().sendIComponentText(interfaceId, 2, title);
		player.getPackets().sendIComponentText(interfaceId, 5, text);
		player.getInterfaceManager().sendInterface(interfaceId);
	}

	/**
	 * Sends the quest interface to the player with the parameterized title and
	 * list of messages. The messages will be formatted to never overlap one
	 * line, but to go to the next one if it passes the limit of characters on a
	 * line.
	 * 
	 * @param player
	 *            The player
	 * @param title
	 *            The title of the quest interface
	 * @param messageList
	 *            The list of messages to send. a {@code String} {@code Array}
	 *            {@code Object}
	 */
	public static void sendQuestScroll(Player player, String title, String... messageList) {
		player.closeInterfaces();
		
		final List<String> messages = new ArrayList<String>();
		final int interfaceId = 275;
		final int endLine = 315;
		final int maxLength = 65;
		final int lineCount = Utils.getInterfaceDefinitionsComponentsSize(interfaceId);
		
		int startLine = 16;
		
		for (int i = 0; i < messageList.length; i++) {
			String message = messageList[i];
			char[] unformatted = message.toCharArray();
			String[] newMessage = new String[(int) Math.ceil((double) unformatted.length / maxLength) + 1];
			for (int j = 0; j < unformatted.length; j++) {
				int index = j == 0 ? 1 : (int) Math.ceil((double) j / maxLength);
				char character = unformatted[j];
				newMessage[index] += character;
			}
			for (String m : newMessage) {
				if (m == null)
					continue;
				messages.add(m.replaceAll("null", ""));
			}
		}
		for (int k = 0; k < lineCount; k++) {
			player.getPackets().sendIComponentText(interfaceId, k, "");
		}
		
		for (String message : messages) {
			if (startLine > endLine)
				break;
			player.getPackets().sendIComponentText(interfaceId, startLine, message);
			startLine++;
		}
		
	
		player.getPackets().sendHideIComponent(interfaceId, 14, true);
		player.getPackets().sendRunScript(1207, new Object[] { messages.size() } );
		player.getPackets().sendIComponentText(interfaceId, 2, title);
		player.getInterfaceManager().sendInterface(interfaceId);
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		return false;
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
		// TODO Auto-generated method stub
		return false;
	}

}
