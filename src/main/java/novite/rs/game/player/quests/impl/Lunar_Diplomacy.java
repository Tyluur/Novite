package novite.rs.game.player.quests.impl;

import novite.rs.game.WorldObject;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.game.player.quests.Quest;
import novite.rs.game.player.quests.QuestInfo;
import novite.rs.game.player.quests.QuestRequirement;
import novite.rs.utility.game.ChatColors;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 29, 2014
 */
@QuestInfo(enumClass = Lunar_Diplomacy.Stages.class)
public class Lunar_Diplomacy extends Quest<Lunar_Diplomacy.Stages> {

	@Override
	public String getName() {
		return "Lunar Diplomacy";
	}

	@Override
	public String[] getReward() {
		return new String[] { "Full access to the Lunar spellbook" };
	}

	@Override
	public String[] getInformation(Player player) {
		if (!startedQuest(player))
			return new String[] { "You have not yet started this quest!", "Speak to the Etheral Man at the Questing Dome to start." };
		switch (getQuestStage(player)) {
		case COMPLETED:
			return new String[] { "<str><col=" + ChatColors.MAROON + ">QUEST COMPLETE!", "You now have full access to the lunar spellbook" };
		case FIGHTING:
			return new String[] { "You must defeat the full phase of yourself to be enlightened with the power of the lunar spellbook. " };
		default:
			return new String[] {};
		}
	}

	@Override
	public void addRequirements(Player player) {
		addQuestRequirement(new QuestRequirement("Level 40 Defence", player.getSkills().getLevelForXp(Skills.DEFENCE) >= 40));
		addQuestRequirement(new QuestRequirement("Level 55 Woodcutting", player.getSkills().getLevelForXp(Skills.WOODCUTTING) >= 55));
	}

	@Override
	public boolean handleItem(Player player, Item item) {
		return false;
	}

	@Override
	public boolean handleObject(Player player, WorldObject object) {
		return false;
	}

	@Override
	public boolean handleNPC(Player player, NPC npc) {
		final int npcId = npc.getId();
		switch (npcId) {
		case 4501:
			switch (getQuestStage(player)) {
			case FIGHTING:
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You must prove yourself worthy to me in battle", "with yourself to have access to my mystical book.", "<col=" + ChatColors.RED + "> You will lose your items if you die in this battle!");
					}

					@Override
					public void run(int interfaceId, int option) {
						switch (stage) {
						case -1:
							sendOptionsDialogue("Start the Battle?", "Yes", "No");
							stage = 0;
							break;
						case 0:
							end();
							if (option == FIRST) {
								player.getControllerManager().startController("LDQuest");
							}
							break;
						}
					}

					@Override
					public void finish() {
					}
				});
				return true;
			case COMPLETED:
				player.getDialogueManager().startDialogue(new Dialogue() {
					
					@Override
					public void start() {
						sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You are a brave warrior. If you ever want to use my", "spellbook, change it with the lectern at home.");
					}
					
					@Override
					public void run(int interfaceId, int option) {
						end();
					}
					
					@Override
					public void finish() {
					}
				});
				return true;
			}
			break;
		}
		return false;
	}

	public enum Stages {
		FIGHTING, COMPLETED
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6226319285972320879L;

}
