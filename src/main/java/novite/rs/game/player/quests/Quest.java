package novite.rs.game.player.quests;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import novite.rs.api.event.listeners.interfaces.Scrollable;
import novite.rs.cache.Cache;
import novite.rs.game.WorldObject;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.utility.Saving;
import novite.rs.utility.Utils;

/**
 * The abstract class of a quest handler.
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Jul 25, 2013
 */
public abstract class Quest<Stages extends Enum<Stages>> implements Serializable {

	/**
	 * The name of the quest.
	 */
	public abstract String getName();

	/**
	 * The reward from the quest.
	 */
	public abstract String[] getReward();

	/**
	 * The requirements of the quest.
	 *
	 * @return The requirements.
	 */
	public abstract String[] getInformation(Player player);

	/**
	 * If the player can start the quest.
	 *
	 * @return
	 */
	public boolean canStart(Player player) {
		questRequirements.clear();
		addRequirements(player);
		for (QuestRequirement requirement : questRequirements) {
			if (!requirement.isCompleted()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the quest is finished
	 */
	public boolean isFinished(Player player) {
		return getQuestStage(player) == getLastStage();
	}

	/**
	 * Starts a quest for the player
	 *
	 * @param player
	 *            The player
	 */
	public void startQuest(Player player) {
		sendStartInformation(player, new String[] { "You have now started: " + getName() + "!" });
		setQuestStage(player, getFirstStage(player));
	}

	/**
	 * Handles the adding of requirements
	 *
	 * @param player
	 *            The player
	 * @param requirements
	 *            The requirements
	 */
	public abstract void addRequirements(Player player);

	/**
	 * Gives the player the extra rewards, this method is not necessary unless
	 * the quest has extra rewards
	 * 
	 * @param player
	 *            The player
	 */
	public void giveRewards(Player player) {

	}

	/**
	 * Adds a quest requirement to its list of requirements
	 * 
	 * @param requirement
	 *            The quest requirement
	 */
	protected void addQuestRequirement(QuestRequirement requirement) {
		questRequirements.add(requirement);
	}

	/**
	 * Completes a quest for the player
	 *
	 * @param player
	 *            The player
	 */
	public void completeQuest(Player player) {
		player.stopAll();

		/**
		 * This block finds the last enum constant for the stage in the child
		 * class and sets it to that.
		 */
		Stages stages = getFirstStage(player);
		
		int numStages = stages.getDeclaringClass().getEnumConstants().length;
		Stages newStage = stages.getDeclaringClass().getEnumConstants()[numStages - 1];
		setQuestStage(player, newStage);

		if (player.hasStarted()) {
			int interfaceId = 277;

			int componentLength = Utils.getInterfaceDefinitionsComponentsSize(Cache.STORE, interfaceId);

			for (int i = 0; i < componentLength; i++) {
				player.getPackets().sendIComponentText(interfaceId, i, "");
			}

			player.getPackets().sendIComponentText(interfaceId, 3, "Congratulations!");
			player.getPackets().sendIComponentText(interfaceId, 4, "You have completed " + getName() + "!");

			player.getPackets().sendIComponentText(interfaceId, 9, "You are awarded:");
			int start = 10;
			for (String reward : getReward()) {
				player.getPackets().sendIComponentText(interfaceId, start, reward);
				start++;
			}
			player.getPackets().sendItemOnIComponent(interfaceId, 5, 9813, 1);
			player.getInterfaceManager().sendInterface(interfaceId);
			giveRewards(player);

			Saving.savePlayer(player);
		}
	}

	/**
	 * The list of quest requirements
	 */
	protected List<QuestRequirement> questRequirements = new ArrayList<QuestRequirement>();

	/**
	 * Handles a item specifically for a quest.
	 *
	 * @param player
	 *            The player
	 * @param item
	 *            The item
	 * @return
	 */
	public abstract boolean handleItem(Player player, Item item);

	/**
	 * Handles an object specifically for a quest.
	 *
	 * @param player
	 *            The player
	 * @param object
	 *            The object
	 * @return
	 */
	public abstract boolean handleObject(Player player, WorldObject object);

	/**
	 * Handles a npc interaction specifically for a quest.
	 *
	 * @param player
	 *            The player
	 * @param npc
	 *            The npc
	 * @return
	 */
	public abstract boolean handleNPC(Player player, NPC npc);

	/**
	 * Gets the first stage in the quest.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Stages getFirstStage(Player player) {
		for (Annotation annotation : getClass().getAnnotations()) {
			if (annotation instanceof QuestInfo) {
				return (Stages) ((QuestInfo) annotation).enumClass().getEnumConstants()[0];
			}
		}
		return null;
	}

	/**
	 * Gets the last stage in the quest.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Stages getLastStage() {
		for (Annotation annotation : getClass().getAnnotations()) {
			if (annotation instanceof QuestInfo) {
				return (Stages) ((QuestInfo) annotation).enumClass().getEnumConstants()[((QuestInfo) annotation).enumClass().getEnumConstants().length - 1];
			}
		}
		return null;
	}

	/**
	 * Finds the players quest stage from the map of stages
	 *
	 * @param player
	 *            The player who we're getting the stage for
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Stages getQuestStage(Player player) {
		Object stage = player.getQuestManager().getStage(getName());
		return stage == null ? null : (Stages) stage;
	}

	/**
	 * Sets the players quest stage to the parameterized one
	 * 
	 * @param player
	 *            The player who's having a change in stages
	 * @param stage
	 *            The stage to set
	 */
	public void setQuestStage(Player player, Stages stage) {
		player.getQuestManager().setStage(getName(), stage);
	}

	/**
	 * Sends information to the interface.
	 *
	 * @param text
	 *            The text
	 */
	public void sendStartInformation(Player player, String[] text) {
		Scrollable.sendScroll(player, "Quest Start Information", text);
	}

	/**
	 * Tells you if the player has started the quest yet
	 *
	 * @return
	 */
	public boolean startedQuest(Player player) {
		return getQuestStage(player) != null;
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -6918630016643051100L;

}
