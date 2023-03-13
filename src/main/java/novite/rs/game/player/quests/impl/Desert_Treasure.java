package novite.rs.game.player.quests.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.game.WorldObject;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.game.player.dialogues.SimpleNPCMessage;
import novite.rs.game.player.quests.Quest;
import novite.rs.game.player.quests.QuestInfo;
import novite.rs.game.player.quests.QuestManager;
import novite.rs.game.player.quests.QuestRequirement;
import novite.rs.utility.game.ChatColors;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 29, 2014
 */
@QuestInfo(enumClass = Desert_Treasure.Stages.class)
public class Desert_Treasure extends Quest<Desert_Treasure.Stages> {

	@Override
	public String getName() {
		return "Desert Treasure";
	}

	@Override
	public String[] getReward() {
		return new String[] { "Access to the Ancient Magic spellbook" };
	}

	@Override
	public String[] getInformation(Player player) {
		if (!startedQuest(player))
			return new String[] { "You have not yet started this quest!", "Speak to Azzanandra at the Questing Dome to start." };
		switch (getQuestStage(player)) {
		case COMPLETE:
			return new String[] { "<str><col=" + ChatColors.MAROON + ">QUEST COMPLETE!", "You now have full access to the ancient spellbook." };
		case FIGHTING:
			List<String> messages = new ArrayList<String>();
			messages.add("You need to kill all four bosses to complete this quest.");
			messages.add("<br>");
			messages.add("Progress (" + player.getFacade().getDesertTreasureKills().size() + "/4):");
			for (Integer id : player.getFacade().getDesertTreasureKills()) {
				messages.add("<col=" + ChatColors.MAROON + ">" + NPCDefinitions.getNPCDefinitions(id).getName());
			}
			return messages.toArray(new String[messages.size()]);
		default:
			return new String[] {};
		}
	}

	@Override
	public void addRequirements(Player player) {
		addQuestRequirement(new QuestRequirement("Level 50 Magic", player.getSkills().getLevelForXp(Skills.MAGIC) >= 50));
		addQuestRequirement(new QuestRequirement("Level 50 Firemaking", player.getSkills().getLevelForXp(Skills.FIREMAKING) >= 50));
	}

	@Override
	public boolean handleItem(Player player, Item item) {
		if (item.getName().equalsIgnoreCase("Ancient staff") && getQuestStage(player) != Stages.COMPLETE) {
			player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 1971, "You must complete my Desert Treasure quest to use", "this item. Read the Quest Journal on Desert Treasure", "for more information.");
			player.getInterfaceManager().openGameTab(3);
			int index = -1;
			player.getQuestManager();
			Iterator<Entry<String, Quest<?>>> it = QuestManager.getQuests().entrySet().iterator();
			while (it.hasNext()) {
				index++;
				Entry<String, Quest<?>> entry = it.next();
				if (entry.getValue().getName().equals(QuestManager.getQuest(Desert_Treasure.class).getName())) {
					break;
				}
			}
			player.getPackets().sendConfig(1439, index);
			return true;
		}
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
		case 1971:
			switch (getQuestStage(player)) {
			case FIGHTING:
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Please choose which boss you which to defeat!");
					}

					@Override
					public void run(int interfaceId, int option) {
						switch (stage) {
						case -1:
							sendOptionsDialogue("Select an Option", "Kamil", "Dessous", "Fareed", "Damis");
							stage = 0;
							break;
						case 0:
							end();
							switch (option) {
							case FIRST:
								player.getControllerManager().startController("DesertTreasure", 0);
								break;
							case SECOND:
								player.getControllerManager().startController("DesertTreasure", 1);
								break;
							case THIRD:
								player.getControllerManager().startController("DesertTreasure", 2);
								break;
							case FOURTH:
								player.getControllerManager().startController("DesertTreasure", 3);
								break;
							}
							break;
						}
					}

					@Override
					public void finish() {

					}
				});
				return true;
			case COMPLETE:
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Hello, again! Would you like to change your", "spellbook to my ancient magics book?");
					}

					@Override
					public void run(int interfaceId, int option) {
						switch (stage) {
						case -1:
							sendOptionsDialogue("Select an Option", "Yes, please switch my spellbook over to yours, please!", "No, never mind.");
							stage = 0;
							break;
						case 0:
							switch (option) {
							case FIRST:
								player.getCombatDefinitions().setSpellBook(1);
								sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Make the most use out of this spellbook!");
								break;
							case SECOND:
								sendPlayerDialogue(ChatAnimation.NORMAL, "No, never mind...");
								break;
							}
							stage = -2;
							break;
						}
					}

					@Override
					public void finish() {

					}
				});
				return true;
			}
			return false;
		}
		return false;
	}

	public enum Stages {
		FIGHTING, COMPLETE
	}

	private static final long serialVersionUID = 8148247159417687293L;
}
