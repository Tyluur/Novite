package novite.rs.game.player.quests;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import novite.rs.api.event.listeners.interfaces.Scrollable;
import novite.rs.game.WorldObject;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.utility.game.ChatColors;
import novite.rs.utility.tools.FileClassLoader;

/**
 * Handles all quests
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Jul 25, 2013
 */
public class QuestManager implements Serializable {

	public QuestManager(Player player) {
		synchronized (this) {
			this.player = player;
		}
	}

	/**
	 * Handles what to do with this class when the player logs in
	 */
	public void login() {
		synchronized (this) {
			sendQuestTabInformation();
		}
	}

	/**
	 * Loads all of the quests for players to do.
	 */
	public static void load() {
		synchronized (LOCK) {
			for (Object packet : FileClassLoader.getClassesInDirectory(QuestManager.class.getPackage().getName() + ".impl")) {
				Quest<?> quest = (Quest<?>) packet;
				getQuests().put(quest.getClass().getSimpleName(), quest);
			}
		}
	}

	/**
	 * Gets the amount of completed quests you have.
	 *
	 * @return A {@code Integer} {@code Object}
	 */
	public int getCompletedQuests() {
		synchronized (this) {
			int size = 0;
			Iterator<Entry<String, Quest<?>>> it$ = getProgressed().entrySet().iterator();
			while (it$.hasNext()) {
				Entry<String, Quest<?>> entry = it$.next();
				Quest<?> quest = entry.getValue();
				if (quest.isFinished(player)) {
					size++;
				}
			}
			return size;
		}
	}

	/**
	 * Sends the quest tab information with all the player quests that can be
	 * completed.
	 */
	public void sendQuestTabInformation() {
		synchronized (this) {
			for (int i = 0; i < getQuests().size(); i++) {
				player.getPackets().sendGlobalString(149 + i, "" + getQuestByIndex(i).getName());
			}
			int colour = 0;
			for (int i = 0; i < getQuests().size(); i++) {
				Quest<?> quest = getProgressedQuest(getQuestByIndex(i).getName());
				if (quest == null) {
					quest = getQuestByIndex(i);
				}
				if (startedQuest(quest)) {
					if (quest.isFinished(player)) {
						colour += colourize(1, i);
					} else {
						colour += colourize(2, i);
					}
				} else {
					colour += colourize(3, i);
				}
				player.getPackets().sendConfig(1440, colour);
			}
			player.getPackets().sendHideIComponent(34, 1, true);
			player.getPackets().sendIComponentText(34, 2, "Quest Points: " + getCompletedQuests());
		}
	}

	/**
	 * Colourizes text in the note tab
	 * 
	 * @param colour
	 *            The colour id for the text
	 * @param noteId
	 *            The note id to colour
	 * @return
	 */
	public static int colourize(int colour, int noteId) {
		return (int) (Math.pow(4, noteId) * colour);
	}

	/**
	 * Gets the quest from the static map by the index id.
	 *
	 * @param index
	 *            The index to search by
	 * @return A {@code Quest} {@code Object}
	 */
	public Quest<?> getQuestByIndex(int index) {
		synchronized (this) {
			int size = 0;
			Iterator<Entry<String, Quest<?>>> it = getQuests().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Quest<?>> entry = it.next();
				if (size == index) {
					return entry.getValue();
				}
				size++;
			}
			return null;
		}
	}

	/**
	 * Removes a quest by name from your progressed map
	 * 
	 * @param name
	 *            The name of the quest to remove.
	 */
	public void removeQuest(String name) {
		synchronized (this) {
			Iterator<Entry<String, Quest<?>>> it$ = getProgressed().entrySet().iterator();
			while (it$.hasNext()) {
				Entry<String, Quest<?>> entry = it$.next();
				if (entry.getKey().equalsIgnoreCase(name)) {
					it$.remove();
				}
			}
		}
	}

	/**
	 * Tells you if the player has completed the quest by the name
	 *
	 * @param name
	 *            The quest to check if the player has completed
	 * @return A {@code Boolean} object, {@code True} if complete.
	 */
	public boolean completedQuest(String name) {
		synchronized (this) {
			Iterator<Entry<String, Quest<?>>> it$ = getProgressed().entrySet().iterator();
			while (it$.hasNext()) {
				Entry<String, Quest<?>> entry = it$.next();
				Quest<?> quest = entry.getValue();
				if (quest.getName().equalsIgnoreCase(name)) {
					if (quest.isFinished(player)) {
						return true;
					}
				}
			}
			return false;
		}
	}

	/**
	 * This method figures out whether or not you have started a quest.
	 * 
	 * @param quest
	 *            The quest to check if you have started or not.
	 * @return
	 */
	private boolean startedQuest(Quest<?> quest) {
		synchronized (this) {
			Iterator<Entry<String, Quest<?>>> it$ = getProgressed().entrySet().iterator();
			while (it$.hasNext()) {
				Entry<String, Quest<?>> entry = it$.next();
				Quest<?> q = entry.getValue();
				if (q.getName().equals(quest.getName())) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Gets the quest by the name of it
	 * 
	 * @param name
	 *            The name of the quest
	 * @return
	 */
	private Quest<?> getQuestByName(String name) {
		Iterator<Entry<String, Quest<?>>> it = SYSTEM_QUESTS.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Quest<?>> entry = it.next();
			Quest<?> quest = entry.getValue();
			if (quest.getName().equalsIgnoreCase(name))
				return quest;
		}
		return null;
	}

	/**
	 * Sends the information of the quest on the interface.
	 *
	 * @param string
	 */
	public void sendInformation(String name) {
		synchronized (this) {
			Quest<?> quest = getProgressedQuest(name);

			if (quest == null) {
				quest = getQuestByName(name);
				if (quest == null) {
					System.err.println("[QUEST-MANAGER]No such quest by name: " + name);
					return;
				}
				quest.setQuestStage(player, null);
			}

			quest.questRequirements.clear();
			quest.addRequirements(player);

			List<String> messages = new ArrayList<String>();
			for (String i : quest.getInformation(player)) {
				messages.add(i);
			}
			if (!startedQuest(quest)) {
				if (quest.questRequirements.size() > 0) {
					messages.add("<br>");
				}
				messages.add("<col=" + ChatColors.DARK_RED + ">Requirements</col>");
				for (QuestRequirement req : quest.questRequirements) {
					messages.add((req.isCompleted() ? "<str>" : "") + "<col=" + ChatColors.MAROON + ">" + req.getName());
				}
			}
			String[] info = messages.toArray(new String[messages.size()]);
			Scrollable.sendQuestScroll(player, "<col=" + ChatColors.MAROON + ">" + quest.getName(), info);
		}
	}

	/**
	 * Sets the stage of the quest
	 * 
	 * @param name
	 *            The name of the quest
	 * @param stage
	 *            The stage to set the quest to
	 */
	public void setStage(String name, Object stage) {
		if (getStages().get(name) == null) {
			getStages().put(name, stage);
		} else {
			Iterator<Entry<String, Object>> it = getStages().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				String entryName = entry.getKey();
				if (entryName.equals(name)) {
					it.remove();
				}
			}
			getStages().put(name, stage);
		}
	}

	/**
	 * Gets the stage of the quest
	 * 
	 * @param name
	 *            The name of the quest to get the stage of
	 * @return
	 */
	public Object getStage(String name) {
		return getStages().get(name);
	}

	/**
	 * Starts a quest
	 *
	 * @param name
	 *            The name of the quest.
	 */
	public void startQuest(Class<?> clazz) {
		synchronized (this) {
			Quest<?> quest = getQuest(clazz);
			if (quest == null) {
				System.err.println("[QUEST-MANAGER] " + clazz.getName() + " attempted to start quest.");
				return;
			}
			String name = quest.getName();
			if (quest.canStart(player)) {
				getProgressed().put(name, quest);
				quest.startQuest(getPlayer());
			} else if (!quest.canStart(player)) {
				player.sendMessage("<col=" + ChatColors.RED + ">You do not have the requirements to start this quest.");
				sendInformation(name);
			}
		}
	}

	/**
	 * Gets the quest by the class from the static map
	 * 
	 * @param clazz
	 *            The class to get the quest by
	 * @return
	 */
	public static Quest<?> getQuest(Class<?> clazz) {
		Iterator<Entry<String, Quest<?>>> it = SYSTEM_QUESTS.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Quest<?>> entry = it.next();
			String questClassName = entry.getKey();
			if (questClassName.equals(clazz.getSimpleName())) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * Completes a quest.
	 *
	 * @param name
	 */
	public void finishQuest(String name) {
		synchronized (this) {
			Iterator<Entry<String, Quest<?>>> it$ = getProgressed().entrySet().iterator();
			while (it$.hasNext()) {
				Entry<String, Quest<?>> entry = it$.next();
				Quest<?> quest = entry.getValue();
				if (quest.getName().equalsIgnoreCase(name)) {
					quest.completeQuest(player);
				}
			}
		}
	}

	/**
	 * Loops through the progressed quest map and checks for any quest with a
	 * name similar to the one in the parameters
	 *
	 * @param name
	 *            The name to check for
	 * @return A {@code Quest} {@code Object}
	 */
	public Quest<?> getProgressedQuest(String name) {
		synchronized (this) {
			Iterator<Entry<String, Quest<?>>> it$ = getProgressed().entrySet().iterator();
			while (it$.hasNext()) {
				Entry<String, Quest<?>> entry = it$.next();
				Quest<?> quest = entry.getValue();
				if (entry.getKey().equalsIgnoreCase(name)) {
					return quest;
				}
			}
			return null;
		}
	}

	/**
	 * Handles the npc interaction for the npc in a quest.
	 *
	 * @param player
	 *            The player
	 * @param npc
	 *            The npc
	 * @return
	 */
	public boolean handleNPC(Player player, NPC npc) {
		synchronized (this) {
			Iterator<Entry<String, Quest<?>>> it$ = getProgressed().entrySet().iterator();
			while (it$.hasNext()) {
				Entry<String, Quest<?>> entry = it$.next();
				Quest<?> quest = entry.getValue();
				if (quest.getQuestStage(player) == null) {
					continue;
				}
				if (quest.handleNPC(player, npc)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Handles the object interaction for the object in a quest.
	 *
	 * @param player
	 *            The player
	 * @param object
	 *            The object
	 * @return
	 */
	public boolean handleObject(Player player, WorldObject object) {
		synchronized (this) {
			Iterator<Entry<String, Quest<?>>> it$ = getProgressed().entrySet().iterator();
			while (it$.hasNext()) {
				Entry<String, Quest<?>> entry = it$.next();
				Quest<?> quest = entry.getValue();
				if (quest.getQuestStage(player) == null) {
					continue;
				}
				if (quest.handleObject(player, object)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Handles the item interaction for the item in a quest.
	 *
	 * @param player
	 *            The player
	 * @param item
	 *            The item
	 * @return
	 */
	public boolean handleItem(Player player, Item item) {
		synchronized (this) {
			for (int i = 0; i < getQuests().size(); i++) {
				Quest<?> quest = getProgressedQuest(getQuestByIndex(i).getName());
				if (quest == null) {
					quest = getQuestByIndex(i);
				}
				if (quest.handleItem(player, item)) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		synchronized (this) {
			return player;
		}
	}

	public static Map<String, Quest<?>> getQuests() {
		synchronized (LOCK) {
			return SYSTEM_QUESTS;
		}
	}

	public Map<String, Quest<?>> getProgressed() {
		synchronized (this) {
			return progressed;
		}
	}

	public void setProgressed(Map<String, Quest<?>> progressed) {
		synchronized (this) {
			this.progressed = progressed;
		}
	}

	public Map<String, Object> getStages() {
		return stages;
	}

	public void setStages(Map<String, Object> stages) {
		this.stages = stages;
	}

	/**
	 * The list of possible quests.
	 */
	private static final Map<String, Quest<?>> SYSTEM_QUESTS = new HashMap<String, Quest<?>>();

	/**
	 * The map of quests in progression
	 */
	private Map<String, Quest<?>> progressed = new HashMap<String, Quest<?>>();

	/**
	 * The map of stages of quests
	 */
	private Map<String, Object> stages = new HashMap<String, Object>();

	/**
	 * The player
	 */
	private final Player player;

	/**
	 * The lock object through which all quests are synchronized
	 */
	private static final Object LOCK = new Object();

	private static final long serialVersionUID = -5565220126642615801L;
}
