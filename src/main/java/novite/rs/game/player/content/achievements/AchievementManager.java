package novite.rs.game.player.content.achievements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import novite.rs.api.event.listeners.interfaces.Scrollable;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;
import novite.rs.utility.tools.FileClassLoader;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 20, 2014
 */
public class AchievementManager implements Serializable {

	public AchievementManager(Player player) {
		this.player = player;
	}

	public static void load() {
		for (Object clazz : FileClassLoader.getClassesInDirectory(AchievementManager.class.getPackage().getName() + ".impl")) {
			Achievement achievement = (Achievement) clazz;
			getAchievements().put(clazz.getClass().getSimpleName(), achievement);
		}
	}

	/**
	 * Sends the notification to the achievement tab with an arrow pointing and
	 * removes after a task
	 */
	public void sendFlashingTab() {
		player.getPackets().sendConfig(1021, 13);
		player.getTemporaryAttributtes().put("flashing_tab_flag", true);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				player.getPackets().sendConfig(1021, 0);
			}
		});
	}

	/**
	 * Increases the amount completed for an achievement
	 *
	 * @param key
	 *            The key in the {@link #data} map
	 * @param achievement
	 *            The achievement
	 */
	public void increaseAmount(String key) {
		updateAmount(key, getAmount(key) + 1);
	}

	/**
	 * Gets the level at which we unlock the type
	 * 
	 * @param type
	 * @return
	 */
	private int getUnlockLevel(Types type) {
		switch (type) {
		case MEDIUM:
			return MEDIUM_UNLOCK_TOTAL;
		case HARD:
			return HARD_UNLOCK_TOTAL;
		default:
			break;
		}
		return 0;
	}

	/**
	 * Finds out if the player has unlocked the achievement type
	 * 
	 * @param achievement
	 * @return
	 */
	public boolean hasUnlocked(Types type) {
		return player.getSkills().getTotalLevel() >= getUnlockLevel(type);
	}

	/**
	 * Gets the achievement from the {@link #ACHIEVEMENTS} map by the clazz.
	 * This will use the class name to find it.
	 *
	 * @param clazz
	 *            The class which is used to get an achievement
	 * @return
	 */
	public static Achievement getAchievement(Class<?> clazz) {
		Iterator<Entry<String, Achievement>> it = getAchievements().entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Achievement> entry = it.next();
			String achievementClassName = entry.getKey();
			if (achievementClassName.equals(clazz.getSimpleName())) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * Notifys the player that the achievement has received an update
	 *
	 * @param key
	 *            The key
	 * @param achievement
	 *            The achievement
	 */
	public void notifyUpdate(Class<?> clazz) {
		Achievement achievement = getAchievement(clazz);
		if (achievement == null) {
			return;
		}
		/* If they have not unlocked this type it won't be needed */
		if (!unlockedAchievement(achievement) || !hasUnlocked(achievement.getType())) {
			return;
		}
		/* If the task has been completed already it doesnt need to be handled */
		if (!completeAchievement(achievement)) {
			increaseAmount(achievement.getKey());
			int total = achievement.getTotalAmount();
			int amt = achievement.getAmountFinished(player);
			if (achievement.isComplete(player)) {
				achievement.giveReward(player);
				sendFlashingTab();
				Scrollable.sendQuestScroll(player, "Achievement Complete!", "You have completed achievement:", "<col=" + ChatColors.MAROON + ">" + achievement.getTitle() + "!", "<br><br>", "Reward:" + achievement.getRewardInfo());
				sendNotification("Completed Achievement: " + achievement.getTitle());
				complete.add(achievement.getTitle());
			} else {
				if (total % amt == 0) {
					sendNotification("You have completed " + amt + " of " + total + " for " + achievement.getType().name().toLowerCase() + " task: " + achievement.getTitle());
				}
			}
		}
	}

	/**
	 * Finds out if you have unlocked access to this achievement
	 *
	 * @param achievement
	 *            The achievement
	 * @return
	 */
	public boolean unlockedAchievement(Achievement achievement) {
		return achievement.unlocked(player);
	}

	/**
	 * Finds out if you have completed the achievement
	 *
	 * @param achievement
	 *            The achievement
	 * @return
	 */
	public boolean completeAchievement(Achievement achievement) {
		return complete.contains(achievement.getTitle());
	}

	/**
	 * Sends the player a neat notification for the achievement progress
	 *
	 * @param message
	 *            The message to send
	 */
	public void sendNotification(String message) {
		String[] regex = message.split(": ");
		player.getDialogueManager().startDialogue("SimpleMessage", "Achievement System Notification", regex[0] + ":", regex[1]);
		player.sendMessage(message);
	}

	/**
	 * Gets the amount complete by the name
	 * 
	 * @param key
	 *            The key
	 * @return
	 */
	public int getAmount(String key) {
		Integer amount = data.get(key);
		if (amount == null) {
			data.put(key, 0);
			amount = 0;
		}
		return amount;
	}

	/**
	 * Updates the amount
	 *
	 * @param key
	 *            The key in the {@link #data} map
	 * @param value
	 *            The value to update with
	 */
	public void updateAmount(String key, int value) {
		data.put(key, value);
	}

	/**
	 * Displays all of the achievements for the type
	 *
	 * @param type
	 *            The type of achievement to display
	 */
	public void displayAchievements(Types type) {
		player.closeInterfaces();
		if (!hasUnlocked(type)) {
			int total = (type == Types.HARD ? HARD_UNLOCK_TOTAL : MEDIUM_UNLOCK_TOTAL);
			player.getDialogueManager().startDialogue("SimpleMessage", "You have not yet unlocked this achievement type.", "You need a total level of " + Utils.format(total) + " to unlock " + type.name().toLowerCase() + " achievements");
			player.sendMessage("You need a total level of " + Utils.format(total) + " to unlock " + type.name().toLowerCase() + " achievements");
		}
		StringBuilder[] bldr = new StringBuilder[30];
		Iterator<Entry<String, Achievement>> it = getAchievements().entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Achievement> entry = it.next();
			Achievement achievement = entry.getValue();
			if (achievement.getType() == type) {
				int freeIndex = -1;
				for (int i = 0; i < bldr.length; i++) {
					if (bldr[i] == null) {
						freeIndex = i;
						break;
					}
				}
				StringBuilder b = new StringBuilder();
				b.append("<col=" + ChatColors.GREEN + ">" + achievement.getTitle() + "<br>Progress:<col=" + ChatColors.WHITE + ">" + achievement.getAmountFinished(player) + "/" + achievement.getTotalAmount() + "<br>Reward:<col=" + ChatColors.WHITE + ">" + achievement.getRewardInfo() + "");
				if (!unlockedAchievement(achievement) || !hasUnlocked(achievement.getType())) {
					b = new StringBuilder();
					b.append("<col=" + ChatColors.RED + ">LOCKED!<br>To Unlock:<col=" + ChatColors.RED + ">" + achievement.getUnlockInfo());
				}
				if (completeAchievement(achievement)) {
					b = new StringBuilder();
					b.append("<str><col=" + ChatColors.GREEN + ">" + achievement.getTitle() + "<br><str>COMPLETE!");
				}
				bldr[freeIndex] = b;
			}
		}
		int interfaceId = 467;
		for (int i = 0; i < bldr.length; i++) {
			if (bldr[i] == null) {
				player.getPackets().sendHideIComponent(interfaceId, LOCKED_INTERFACE_SLOTS[i], false);
				continue;
			}
			player.getPackets().sendIComponentText(interfaceId, INTERFACE_SLOTS[i], bldr[i].toString());
		}
		player.getPackets().sendIComponentText(interfaceId, 136, Utils.formatPlayerNameForDisplay(type.name()) + " Achievements");
		player.getInterfaceManager().sendInterface(interfaceId);
	}

	/**
	 * Sends the achievement interface
	 */
	public void sendAchievements() {
		player.closeInterfaces();
		int interfaceId = 467;
		StringBuilder[] bldr = new StringBuilder[INTERFACE_SLOTS.length];

		ValueComparator bvc = new ValueComparator(getAchievements());
		TreeMap<String, Achievement> sorted_map = new TreeMap<String, Achievement>(bvc);
		sorted_map.putAll(getAchievements());

		Iterator<Entry<String, Achievement>> it = sorted_map.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, Achievement> entry = it.next();
			Achievement achievement = entry.getValue();
			int freeIndex = -1;
			for (int i = 0; i < bldr.length; i++) {
				if (bldr[i] == null) {
					freeIndex = i;
					break;
				}
			}
			if (freeIndex == -1) {
				break;
			}
			StringBuilder b = new StringBuilder();
			b.append("<col=" + ChatColors.GREEN + ">" + achievement.getTitle() + "<br>Progress:<col=" + ChatColors.WHITE + ">" + achievement.getAmountFinished(player) + "/" + achievement.getTotalAmount() + "<br>Reward:<col=" + ChatColors.WHITE + ">" + achievement.getRewardInfo() + "");
			if (!unlockedAchievement(achievement) || !hasUnlocked(achievement.getType())) {
				b = new StringBuilder();
				if (!unlockedAchievement(achievement))
					b.append("<col=" + ChatColors.RED + ">LOCKED!<br>To Unlock:<col=" + ChatColors.RED + ">" + achievement.getUnlockInfo());
				else
					b.append("<col=" + ChatColors.RED + ">LOCKED!<br>Unlock At Total Level: " + getUnlockLevel(achievement.getType()));
			}
			if (completeAchievement(achievement)) {
				b = new StringBuilder();
				b.append("<str><col=" + ChatColors.GREEN + ">" + achievement.getTitle() + "<br><str>COMPLETE!");
			}
			bldr[freeIndex] = b;
		}

		for (int i = 0; i < bldr.length; i++) {
			if (bldr[i] == null) {
				player.getPackets().sendHideIComponent(interfaceId, LOCKED_INTERFACE_SLOTS[i], false);
				continue;
			}
			player.getPackets().sendIComponentText(interfaceId, INTERFACE_SLOTS[i], bldr[i].toString());
		}
		player.getPackets().sendIComponentText(interfaceId, 136, "Achievement Management");
		player.getInterfaceManager().sendInterface(interfaceId);
	}

	/**
	 * Displays all achievement information
	 * 
	 * @param player
	 *            The player
	 */
	public void displayInformation(Player player) {
		Scrollable.sendQuestScroll(player, "Achievement Help", "<col=" + ChatColors.MAROON + ">Information:", "You have already started all the achievements, all you need to do is progress in them to complete them. Press \"easy achievements\" and check out some examples", "", "<col=" + ChatColors.MAROON + ">Achievement Points:", "These are points that are given by some achievements that can be exchanged in the achievement store for <col=" + ChatColors.RED + ">crazy</col> rewards!", "", "<col=" + ChatColors.MAROON + ">Other Rewards", "Some achievement give item rewards to you. If you are not doing a minigame, they will be given in your inventory as long as you have space for it. <col=" + ChatColors.PURPLE + ">If you don't have space, it is placed in your bank!");
	}

	/**
	 * @return the achievements
	 */
	public static Map<String, Achievement> getAchievements() {
		return ACHIEVEMENTS;
	}

	/**
	 * The player
	 */
	private final Player player;

	/**
	 * The map of data for achievements
	 */
	private final Map<String, Integer> data = new HashMap<String, Integer>();

	/**
	 * The list of complete achievements by name
	 */
	private final List<String> complete = new ArrayList<String>();

	/**
	 * The slots that can be written over
	 */
	private static final int[] INTERFACE_SLOTS = new int[] { 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35, 37, 39, 41, 43, 45, 47, 49, 51, 53, 55, 57, 59, 61, 63, 65, 67, 69, 71, 73 };

	/**
	 * The slots that are locked will be unlocked with these components
	 */
	private static final int[] LOCKED_INTERFACE_SLOTS = new int[] { 77, 79, 81, 83, 85, 87, 89, 91, 93, 95, 97, 99, 101, 103, 105, 107, 109, 111, 113, 115, 117, 119, 121, 123, 125, 127, 129, 131, 133, 135 };

	/**
	 * The total level which you unlock hard tasks with
	 */
	private static final int HARD_UNLOCK_TOTAL = 1000;

	/**
	 * The total level which you unlock medium tasks at
	 */
	private static final int MEDIUM_UNLOCK_TOTAL = 300;

	/**
	 * The list of achievement classes loaded
	 */
	private static final Map<String, Achievement> ACHIEVEMENTS = new HashMap<>();

	private static final long serialVersionUID = -1859181502364192569L;

}

class ValueComparator implements Comparator<String> {

	Map<String, Achievement> base;

	public ValueComparator(Map<String, Achievement> map) {
		this.base = map;
	}

	public int compare(String a, String b) {
		Achievement a1 = base.get(a);
		Achievement a2 = base.get(b);
		if (a1.getType().ordinal() > a2.getType().ordinal())
			return 1;
		return -1;
	}
}
