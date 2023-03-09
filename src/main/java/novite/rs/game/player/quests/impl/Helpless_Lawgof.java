package novite.rs.game.player.quests.impl;

import novite.rs.cache.loaders.ItemDefinitions;
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
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 29, 2014
 */
@QuestInfo(enumClass = Helpless_Lawgof.Stages.class)
public class Helpless_Lawgof extends Quest<Helpless_Lawgof.Stages> {

	@Override
	public String getName() {
		return "Helpless Lawgof";
	}

	@Override
	public String[] getReward() {
		return new String[] { "Access to the Dwarf Multicannon", "Ability to create cannonballs" };
	}

	@Override
	public String[] getInformation(Player player) {
		if (!startedQuest(player))
			return new String[] { "You have not yet started this quest!", "Speak to Captain Lawgof at the Questing dome to start." };
		switch (getQuestStage(player)) {
		case STARTED:
			return new String[] { "I should talk to Lawgof for more information." };
		case GATHERING_SUPPLIES:
			return new String[] { "I need to gather a Constructor's Hat and a Skull Sceptre.", "<br>", "I can find the hat in a chest around Draynor Village.", "The sceptre should be in a bookcase near the Falador city." };
		case KILLING_GOBLIN:
			return new String[] { "I need to kill a goblin for Lawgof. More specifically, the ones north-west from Falador with red platemails." };
		case FINISHED:
			return new String[] { "<str><col=" + ChatColors.RED + ">QUEST COMPLETE!", "I can now buy cannonballs and cannons from Lawgof." };
		default:
			return new String[] {};
		}
	}

	@Override
	public void addRequirements(Player player) {
		addQuestRequirement(new QuestRequirement("Level 20 Smithing", player.getSkills().getLevelForXp(Skills.SMITHING) >= 20));
	}

	@Override
	public boolean handleItem(Player player, Item item) {
		return false;
	}

	@Override
	public boolean handleObject(Player player, WorldObject object) {
		if (getQuestStage(player) == Stages.GATHERING_SUPPLIES) {
			if (object.getId() == 9523) {
				int itemId = 9013;
				if (!player.containsOneItem(true, itemId)) {
					player.getInventory().addItem(new Item(itemId));
					player.getDialogueManager().startDialogue(SimpleItemMessage.class, itemId, "You find a " + ItemDefinitions.getItemDefinitions(itemId).getName() + " in the bookcase!");
				}
				return true;
			}
			if (object.getId() == 46243 && object.getX() == 3096) {
				int itemId = 21446;
				if (!player.containsOneItem(true, itemId)) {
					player.getInventory().addItem(new Item(itemId));
					player.getDialogueManager().startDialogue(SimpleItemMessage.class, itemId, "You find a " + ItemDefinitions.getItemDefinitions(itemId).getName() + " in the chest!");
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean handleNPC(Player player, final NPC npc) {
		final int npcId = npc.getId();
		switch (npcId) {
		case 208:
			switch (getQuestStage(player)) {
			case STARTED:
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						sendNPCDialogue(npcId, ChatAnimation.NORMAL, "I am glad I found a person like you! There will be a good", "reward for you at the end for your troubles.");
					}

					@Override
					public void run(int interfaceId, int option) {
						switch (stage) {
						case -1:
							sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Me and my friends are hosting a party and I need", "an outfit which looks both dangerous and brave.", "I've come up with something, but I can't get it myself.");
							stage = 0;
							break;
						case 0:
							sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You need to get me a <col=" + ChatColors.BLUE + ">constructor's hat</col> and a <col=" + ChatColors.BLUE + ">skull sceptre</col>.");
							stage = 1;
							break;
						case 1:
							sendPlayerDialogue(ChatAnimation.NORMAL, "Where would I find such items?");
							stage = 2;
							break;
						case 2:
							end();
							setQuestStage(player, Stages.GATHERING_SUPPLIES);
							handleNPC(player, npc);
							break;
						}
					}

					@Override
					public void finish() {
					}
				});
				break;
			case GATHERING_SUPPLIES:
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						if (!player.getInventory().containsItems(new int[] { 9013, 21446 }, new int[] { 1, 1 })) {
							sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You can find the hat somewhere in a chest in Draynor.", "The skull sceptre should be in a bookcase", "near the centre of Falador city.");
						} else {
							sendPlayerDialogue(ChatAnimation.NORMAL, "I've found your materials for you!");
						}
					}

					@Override
					public void run(int interfaceId, int option) {
						if (!player.getInventory().containsItems(new int[] { 9013, 21446 }, new int[] { 1, 1 }))
							end();
						else {
							switch (stage) {
							case -1:
								player.getInventory().deleteItem(9013, 1);
								player.getInventory().deleteItem(21446, 1);
								setQuestStage(player, Stages.KILLING_GOBLIN);
								handleNPC(player, npc);
								break;
							}
						}
					}

					@Override
					public void finish() {
					}
				});
				break;
			case KILLING_GOBLIN:
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Thanks for your help with the items.");
					}

					@Override
					public void run(int interfaceId, int option) {
						switch (stage) {
						case -1:
							sendNPCDialogue(npcId, ChatAnimation.NORMAL, "I have a problem with a certain clan of goblins.", "They are refusing to give me the money they owe me!");
							stage = 0;
							break;
						case 0:
							sendPlayerDialogue(ChatAnimation.LISTENING, "So what do you want me to do?");
							stage = 1;
							break;
						case 1:
							sendNPCDialogue(npcId, ChatAnimation.NORMAL, "I want you to kill one of the goblins.", "They are located north-west from falador.", "They are the ones with the RED platemails.");
							stage = -2;
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
						sendNPCDialogue(npcId, ChatAnimation.NORMAL, "I really appreciate your help back there. The party", "was a blast! Now how can I help you?");
					}

					@Override
					public void run(int interfaceId, int option) {
						switch (stage) {
						case -1:
							sendOptionsDialogue("Select an Option", "May I purchase a dwarf cannon?", "May I purchase a cannon mould to make cannonballs?");
							stage = 0;
							break;
						case 0:
							switch (option) {
							case FIRST:
								sendPlayerDialogue(ChatAnimation.NORMAL, "May I purchase a dwarf cannon?");
								stage = 1;
								break;
							case SECOND:
								sendPlayerDialogue(ChatAnimation.NORMAL, "May I purchase a cannon mould to make cannonballs?");
								stage = 4;
								break;
							}
							break;
						case 1:
							sendNPCDialogue(npcId, ChatAnimation.LISTEN_LAUGH, "Sure! Anything for you. They are quite expensive.", "though. Do you have 1,000,000 coins to pay for it?");
							stage = 2;
							break;
						case 2:
							sendOptionsDialogue("Select an Option", "Yes, I have 1,000,000 coins to pay.", "No.");
							stage = 3;
							break;
						case 3:
							if (option == FIRST) {
								if (player.takeMoney(1000000)) {
									player.getInventory().addDroppable(new Item(6, 1));
									player.getInventory().addDroppable(new Item(8, 1));
									player.getInventory().addDroppable(new Item(10, 1));
									player.getInventory().addDroppable(new Item(12, 1));
									end();
									player.getDialogueManager().startDialogue(SimpleItemMessage.class, 6, "You receive a full dwarf cannon set.");
								} else {
									sendPlayerDialogue(ChatAnimation.SAD, "I don't have enough money...");
								}
								stage = -2;
							} else {
								end();
							}
							break;
						case 4:
							sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Normally these are 100K, but I'll give you one for free.", "Since you go out of your way just for me.");
							player.getInventory().addDroppable(new Item(4));
							stage = -2;
							break;
						}
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
	 * 
	 */
	private static final long serialVersionUID = 5261913027497981489L;

	public enum Stages {
		STARTED, GATHERING_SUPPLIES, KILLING_GOBLIN, FINISHED
	}

}
