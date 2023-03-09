package novite.rs.game.player.dialogues;

import java.util.ArrayList;
import java.util.List;

import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;

public abstract class Dialogue {

	protected Player player;
	protected byte stage = -1;

	public Dialogue() {

	}

	public Object[] parameters;

	public void setPlayer(Player player) {
		this.player = player;
	}

	public abstract void start();

	public abstract void run(int interfaceId, int option);

	public abstract void finish();

	protected final void end() {
		player.getDialogueManager().finishDialogue();
	}

	protected static final short SEND_1_TEXT_INFO = 210;
	protected static final short SEND_2_TEXT_INFO = 211;
	protected static final short SEND_3_TEXT_INFO = 212;
	protected static final short SEND_4_TEXT_INFO = 213;
	protected static final String DEFAULT_OPTIONS_TI = "Select an Option";
	protected static final short SEND_2_OPTIONS = 236;
	protected static final short SEND_3_OPTIONS = 235;
	protected static final short SEND_4_OPTIONS = 237;
	protected static final short SEND_5_OPTIONS = 238;
	protected static final short SEND_2_LARGE_OPTIONS = 229;
	protected static final short SEND_3_LARGE_OPTIONS = 231;
	protected static final short SEND_1_TEXT_CHAT = 241;
	protected static final short SEND_2_TEXT_CHAT = 242;
	protected static final short SEND_3_TEXT_CHAT = 243;
	protected static final short SEND_4_TEXT_CHAT = 244;
	protected static final short SEND_NO_CONTINUE_1_TEXT_CHAT = 245;
	protected static final short SEND_NO_CONTINUE_2_TEXT_CHAT = 246;
	protected static final short SEND_NO_CONTINUE_3_TEXT_CHAT = 247;
	protected static final short SEND_NO_CONTINUE_4_TEXT_CHAT = 248;
	protected static final short SEND_NO_EMOTE = -1;
	protected static final byte IS_NOTHING = -1;
	protected static final byte IS_PLAYER = 0;
	protected static final byte IS_NPC = 1;
	protected static final byte IS_ITEM = 2;

	private static int[] getIComponentsIds(short interId) {
		int childOptions[];
		switch (interId) {

		case 458:
			childOptions = new int[4];
			for (int i = 0; i < childOptions.length; i++) {
				childOptions[i] = i;
			}
			break;
		case 210:
			childOptions = new int[1];
			childOptions[0] = 1;
			break;

		case 211:
			childOptions = new int[2];
			childOptions[0] = 1;
			childOptions[1] = 2;
			break;

		case 212:
			childOptions = new int[3];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			break;

		case 213:
			childOptions = new int[4];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			childOptions[3] = 4;
			break;

		case 229:
			childOptions = new int[3];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			break;

		case 231:
			childOptions = new int[4];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			childOptions[3] = 4;
			break;

		case 235:
			childOptions = new int[4];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			childOptions[3] = 4;
			break;

		case 236:
			childOptions = new int[3];
			childOptions[0] = 0;
			childOptions[1] = 1;
			childOptions[2] = 2;
			break;

		case 237:
			childOptions = new int[5];
			childOptions[0] = 0;
			childOptions[1] = 1;
			childOptions[2] = 2;
			childOptions[3] = 3;
			childOptions[4] = 4;
			break;

		case 238:
			childOptions = new int[6];
			childOptions[0] = 0;
			childOptions[1] = 1;
			childOptions[2] = 2;
			childOptions[3] = 3;
			childOptions[4] = 4;
			childOptions[5] = 5;
			break;

		case 64:
			childOptions = new int[2];
			childOptions[0] = 3;
			childOptions[1] = 4;
			break;

		case 65:
			childOptions = new int[3];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			break;

		case 66:
			childOptions = new int[4];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			childOptions[3] = 6;
			break;

		case 67:
			childOptions = new int[5];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			childOptions[3] = 6;
			childOptions[4] = 7;
			break;

		case 241:
		case 245:
			childOptions = new int[2];
			childOptions[0] = 3;
			childOptions[1] = 4;
			break;

		case 242:
		case 246:
			childOptions = new int[3];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			break;

		case 243:
		case 247:
			childOptions = new int[4];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			childOptions[3] = 6;
			break;

		case 244:
		case 248:
			childOptions = new int[5];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			childOptions[3] = 6;
			childOptions[4] = 7;
			break;

		case 214:
		case 215:
		case 216:
		case 217:
		case 218:
		case 219:
		case 220:
		case 221:
		case 222:
		case 223:
		case 224:
		case 225:
		case 226:
		case 227:
		case 228:
		case 230:
		case 232:
		case 233:
		case 234:
		case 239:
		case 240:
		default:
			return null;
		}
		return childOptions;
	}

	public static final int OPTION_1 = 1, OPTION_2 = 2, OPTION_3 = 3, OPTION_4 = 4, OPTION_5 = 5;

	/**
	 * Sends an npc dialogue without the continue button available. After the
	 * {@link #ticks} ticks have passed, it will then be visible
	 * 
	 * @param npcId
	 *            The npc id to send the dialogue of
	 * @param animation
	 *            The animation of the dialogue
	 * @param ticks
	 *            The ticks to display the continue button afte
	 * @param message
	 *            The message to send
	 */
	public void sendNPCDialogueNoContinue(int npcId, ChatAnimation animation, int ticks, String... message) {
		int interfaceId = 240;
		for (int i = 0; i < message.length; i++) {
			interfaceId++;
		}
		final int hideLine = getHideLine(interfaceId);
		if (hideLine != -1) {
			player.getPackets().sendHideIComponent(interfaceId, hideLine, true);
			final int interf = interfaceId;
			if (ticks != -1)
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						player.getPackets().sendHideIComponent(interf, hideLine, false);
					}
				}, ticks);
		}
		sendEntityDialogue(true, npcId, animation, message);
	}

	/**
	 * Sends a player dialogue without the continue button available. After the
	 * {@link #ticks} ticks have passed, it will then be visible
	 * 
	 * @param animation
	 *            The animation on the dialogue
	 * @param ticks
	 *            The ticks to pass for the continue button to display
	 * @param message
	 *            The dialogue message
	 */
	public void sendPlayerDialogueNoContinue(ChatAnimation animation, int ticks, String... message) {
		int interfaceId = 63;
		for (int i = 0; i < message.length; i++) {
			interfaceId++;
		}
		final int hideLine = getHideLine(interfaceId);
		if (hideLine != -1) {
			player.getPackets().sendHideIComponent(interfaceId, hideLine, true);
			final int interf = interfaceId;
			if (ticks != -1)
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						player.getPackets().sendHideIComponent(interf, hideLine, false);
					}
				}, ticks);
		}
		sendEntityDialogue(false, player.getIndex(), animation, message);
	}

	public void sendNPCDialogue(int npcId, ChatAnimation animation, String... message) {
		sendEntityDialogue(true, npcId, animation, message);
	}

	public void sendPlayerDialogue(ChatAnimation animation, String... message) {
		sendEntityDialogue(false, player.getIndex(), animation, message);
	}

	public void sendItemDialogue(int itemId, String... messages) {
		int l = messages.length;
		short interfaceId = (l == 1 ? SEND_1_TEXT_CHAT : l == 2 ? SEND_2_TEXT_CHAT : l == 3 ? SEND_3_TEXT_CHAT : SEND_4_TEXT_CHAT);
		List<String> text = new ArrayList<String>();
		text.add("");
		for (String m : messages) {
			text.add(m);
		}
		String[] message = text.toArray(new String[text.size()]);
		sendEntityDialogue(interfaceId, message, IS_ITEM, (Integer) parameters[0], 1);
	}

	public void sendDialogue(String... text) {
		int l = text.length;
		short interfaceId = (l == 4 ? SEND_4_TEXT_INFO : l == 3 ? SEND_3_TEXT_INFO : l == 2 ? SEND_2_TEXT_INFO : SEND_1_TEXT_INFO);
		sendDialogue(interfaceId, text);
	}

	public boolean sendEntityDialogue(short interId, String[] talkDefinitons, byte type, int entityId, int animationId) {
		int[] componentOptions = getIComponentsIds(interId);
		if (componentOptions == null) {
			return false;
		}
		player.getInterfaceManager().sendChatBoxInterface(interId);
		if (talkDefinitons.length != componentOptions.length) {
			return false;
		}
		for (int childOptionId = 0; childOptionId < componentOptions.length; childOptionId++) {
			player.getPackets().sendIComponentText(interId, componentOptions[childOptionId], talkDefinitons[childOptionId]);
		}
		if (type == IS_PLAYER || type == IS_NPC) {
			player.getPackets().sendEntityOnIComponent(type == IS_PLAYER, entityId, interId, 2);
			if (animationId != -1) {
				player.getPackets().sendIComponentAnimation(animationId, interId, 2);
			}
		} else if (type == IS_ITEM) {
			player.getPackets().sendItemOnIComponent(interId, 2, entityId, animationId);
		}
		return true;
	}

	@SuppressWarnings("unused")
	private void sendEntityDialogue(boolean npc, int entityId, ChatAnimation animation, String... message) {
		StringBuilder bldr = new StringBuilder();
		int interfaceId = npc ? 240 : 63;
		for (String element : message) {
			interfaceId++;
		}
		for (String element : message) {
			bldr.append(" " + element);
		}
		int[] componentOptions = getIComponentsIds((short) interfaceId);
		String title = npc ? NPCDefinitions.getNPCDefinitions(entityId).getName() : player.getDisplayName();
		String[] messages = getMessages(title, message);
		if (componentOptions == null || (messages.length) != componentOptions.length) {
			return;
		}
		player.getInterfaceManager().sendChatBoxInterface(interfaceId);
		for (int i = 0; i < componentOptions.length; i++) {
			player.getPackets().sendIComponentText(interfaceId, componentOptions[i], messages[i]);
		}
		player.getPackets().sendEntityOnIComponent(!npc, entityId, interfaceId, 2);
		player.getPackets().sendIComponentAnimation(animation.getAnimation(), interfaceId, 2);
	}

	private static int getHideLine(int interfaceId) {
		switch (interfaceId) {
		case 64:
		case 241:
			return 5;
		case 65:
		case 242:
			return 6;
		case 66:
		case 243:
			return 7;
		case 67:
		case 244:
			return 8;
		}
		return -1;
	}

	private static String[] getMessages(String title, String[] message) {
		List<String> textList = new ArrayList<String>();
		textList.add(title);
		for (String chats : message) {
			textList.add(chats);
		}
		String[] messages = new String[textList.size()];
		for (int i = 0; i < textList.size(); i++) {
			messages[i] = textList.get(i);
		}
		return messages;
	}

	public void sendOptionsDialogue(String... text) {
		int l = text.length;
		int interfaceId = (l == 6 ? 238 : l == 5 ? 237 : l == 4 ? 458 : 236);

		String[] messages = new String[text.length + 1];
		for (int i = 0; i < text.length; i++) {
			messages[i] = text[i];
		}
		sendDialogue((short) interfaceId, messages);
	}

	/*
	 * public void sendOptionsDialogue(String... text) { short interfaceId =
	 * (short) getOptionsInterface(text.length - 1);
	 * System.out.println(interfaceId); sendEntityDialogue(interfaceId, text,
	 * IS_PLAYER, player.getIndex(), 9827); }
	 */
	public int getOptionsInterface(int messages) {
		switch (messages) {
		case 2:
			return SEND_2_OPTIONS;
		case 3:
			return SEND_3_LARGE_OPTIONS;
		case 4:
			return SEND_4_OPTIONS;
		case 5:
			return SEND_5_OPTIONS;
		}
		return -1;
	}

	public static final int FIRST = 1, SECOND = 2, THIRD = 3, FOURTH = 4, FIFTH = 5;

	public boolean sendDialogue(short interId, String... talkDefinitons) {
		int[] componentOptions = getIComponentsIds(interId);
		if (componentOptions == null) {
			return false;
		}
		if (player == null)
			return false;
		player.getInterfaceManager().sendChatBoxInterface(interId);
		int properLength = (interId > 213 ? talkDefinitons.length - 1 : talkDefinitons.length);
		if (properLength != componentOptions.length) {
			return false;
		}
		for (int childOptionId = 0; childOptionId < componentOptions.length; childOptionId++) {
			player.getPackets().sendIComponentText(interId, componentOptions[childOptionId], talkDefinitons[childOptionId]);
		}
		return true;
	}

	/*
	 * public boolean sendDialogue(short interId, String talkDefinitons[]) { int
	 * componentOptions[] = getIComponentsIds(interId); if (componentOptions ==
	 * null) return false; player.getPacketSender().sendPacket(new
	 * ChatboxInterfaceDisplayer(interId)); if (talkDefinitons.length - 1 !=
	 * componentOptions.length) return false; for (int i = 0; i <
	 * componentOptions.length; i++){ player.getGameSession().write(new
	 * StringOverInterface(player, talkDefinitons[i], interId,
	 * componentOptions[i])); } return true; }
	 */

	public void run1(int interfaceId, int componentId) {

	}

	public static boolean sendNPCDialogueNoContinue(Player player, int npcId, int animationId, String... text) {
		return sendEntityDialogueNoContinue(player, IS_NPC, npcId, animationId, text);
	}

	public static boolean sendPlayerDialogueNoContinue(Player player, int animationId, String... text) {
		return sendEntityDialogueNoContinue(player, IS_PLAYER, -1, animationId, text);
	}

	/*
	 * 
	 * auto selects title, new dialogues
	 */
	public static boolean sendEntityDialogueNoContinue(Player player, int type, int entityId, int animationId, String... text) {
		String title = "";
		if (type == IS_PLAYER) {
			title = player.getDisplayName();
		} else if (type == IS_NPC) {
			title = NPCDefinitions.getNPCDefinitions(entityId).getName();
		} else if (type == IS_ITEM) {
			title = ItemDefinitions.getItemDefinitions(entityId).getName();
		}
		return sendEntityDialogueNoContinue(player, type, title, entityId, animationId, text);
	}

	public static boolean sendEntityDialogueNoContinue(Player player, int type, String title, int entityId, int animationId, String... texts) {
		boolean npc = type != IS_PLAYER;

		StringBuilder bldr = new StringBuilder();
		int interfaceId = npc ? 240 : 63;
		for (String element : texts) {
			interfaceId++;
			bldr.append(" " + element);
		}
		int[] componentOptions = getIComponentsIds((short) interfaceId);
		String[] messages = getMessages(title, texts);
		if (componentOptions == null || (messages.length) != componentOptions.length) {
			return false;
		}
		player.getInterfaceManager().sendChatBoxInterface(interfaceId);
		for (int i = 0; i < componentOptions.length; i++) {
			player.getPackets().sendIComponentText(interfaceId, componentOptions[i], messages[i]);
		}
		player.getPackets().sendEntityOnIComponent(!npc, entityId, interfaceId, 2);
		player.getPackets().sendIComponentAnimation(animationId, interfaceId, 2);
		return true;
	}

	public static boolean sendEmptyDialogue(Player player) {
		player.getInterfaceManager().replaceRealChatBoxInterface(89);
		return true;
	}

	public static void closeNoContinueDialogue(Player player) {
		player.getInterfaceManager().closeReplacedRealChatBoxInterface();
	}

	/**
	 * @return the stage
	 */
	public byte getStage() {
		return stage;
	}

	/**
	 * @param stage
	 *            the stage to set
	 */
	public void setStage(int stage) {
		this.stage = (byte) stage;
	}
}
