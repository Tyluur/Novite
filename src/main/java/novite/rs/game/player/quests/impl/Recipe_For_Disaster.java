package novite.rs.game.player.quests.impl;

import java.util.ArrayList;
import java.util.List;

import novite.rs.game.WorldObject;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.game.player.dialogues.impl.SimpleItemMessage;
import novite.rs.game.player.quests.Quest;
import novite.rs.game.player.quests.QuestInfo;
import novite.rs.game.player.quests.QuestRequirement;
import novite.rs.utility.game.ChatColors;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 24, 2014
 */
@QuestInfo(enumClass = Recipe_For_Disaster.Stages.class)
public class Recipe_For_Disaster extends Quest<Recipe_For_Disaster.Stages> {

	@Override
	public String getName() {
		return "Recipe for Disaster";
	}

	@Override
	public String[] getReward() {
		return new String[] { "Unlimited access to the culinomancer gloves." };
	}
	
	@Override
	public void startQuest(Player player) {
		super.startQuest(player);
		for (int i = 0; i < killedBosses.length; i++) {
			killedBosses[i] = false;
		}
	}

	@Override
	public String[] getInformation(Player player) {
		if (!startedQuest(player))
			return new String[] { "You have not yet started this quest!", "Speak to the Gloves Gypsy at the Questing Dome to start." };
		switch (getQuestStage(player)) {
		case FIGHTING:
			List<String> messages = new ArrayList<String>();
			messages.add("You must defeat all of these bosses! Progress:");
			messages.add("<br>");
			for (int i = 0; i < getKilledBosses().length; i++) {
				String name = (i == AGRITH_NA_NA_INDEX ? "Agrith Na Na" : i == DESSOURT_INDEX ? "Dessourt" : i == FLAMBEED_INDEX ? "Flambeed" : i == KARAMEL_INDEX ? "Karamel" : "");
				if (getKilledBosses()[i]) {
					messages.add("<str>" + name);
				} else {
					messages.add(name);
				}
			}
			return messages.toArray(new String[messages.size()]);
		case FINISHED:
			return new String[] { "<str><col=" + ChatColors.MAROON + ">QUEST COMPLETE!", "Purchase Culinomancer gloves from the Gypsy at any time." };
		}
		return new String[] {};
	}

	@Override
	public void addRequirements(Player player) {
		addQuestRequirement(new QuestRequirement("Level 20 Agility", player.getSkills().getLevelForXp(Skills.AGILITY) >= 20));
	}

	@Override
	public boolean handleItem(Player player, Item item) {
		if (item.getId() >= 7454 && item.getId() <= 7462) {
			int waves = player.getFacade().getLastRFDWave();
			int max = (waves == 4 ? 7462 : waves == 3 ? 7461 : waves == 2 ? 7458 : waves == 1 ? 7456 : 7454);
			if (item.getId() > max) {
				player.getDialogueManager().startDialogue(SimpleItemMessage.class, item.getId(), "You cannot equip this item yet!", "In order to wield these gloves you need to kill more", "waves of monsters in Recipe for Disaster");
				return true;
			}
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
		case 3385:
			switch (getQuestStage(player)) {
			case FIGHTING:
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						sendNPCDialogue(npcId, ChatAnimation.FURIOUS, "You best be ready to fight these monsters!");
					}

					@Override
					public void run(int interfaceId, int option) {
						switch (stage) {
						case -1:
							sendOptionsDialogue("Fight Monsters?", "Yes", "No");
							stage = 0;
							break;
						case 0:
							switch (option) {
							case FIRST:
								player.getControllerManager().startController("RFDQuest");
								break;
							case SECOND:
								break;
							}
							end();
							break;
						}
					}

					@Override
					public void finish() {
					}
				});
				break;
			case FINISHED:
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Purchase any of my gloves from the chest next", "to me!");
					}

					@Override
					public void run(int interfaceId, int option) {
						end();
					}

					@Override
					public void finish() {
					}
				});
				break;
			}
			return true;
		}
		return false;
	}

	/**
	 * @return the killedBosses
	 */
	public boolean[] getKilledBosses() {
		return killedBosses;
	}

	private final boolean[] killedBosses = new boolean[4];

	public enum Stages {
		FIGHTING, FINISHED
	}

	public static final int AGRITH_NA_NA_INDEX = 0, FLAMBEED_INDEX = 1, KARAMEL_INDEX = 2, DESSOURT_INDEX = 3;
	private static final long serialVersionUID = 5373249978439839713L;
}
