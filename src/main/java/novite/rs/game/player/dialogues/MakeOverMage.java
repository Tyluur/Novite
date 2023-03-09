package novite.rs.game.player.dialogues;

import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.game.player.content.PlayerLook;

public class MakeOverMage extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		int v = (Integer) parameters[1];
		if (v == 0) {
			sendEntityDialogue(SEND_3_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "Hello there! I am know as the Makeover Mage! I have", "spent many years researching magicks that can change", "your physical appearence." }, IS_NPC, npcId, 9827);
		} else if (v == 1) {
			setStage(-2);
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "That is no different from what you already have. I guess I", "shouldn't charge you if I'm not changing anything." }, IS_NPC, npcId, 9827);
		} else if (v == 2) {
			setStage(19);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "Whew! That was lucky." }, IS_NPC, npcId, 9827);
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (getStage() == -1) {
			setStage(0);
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "I call it a 'makeover'.", "Would you like to perform my magicks on you?" }, IS_NPC, npcId, 9827);
		} else if (getStage() == 0) {
			setStage(1);
			sendDialogue(SEND_4_OPTIONS, DEFAULT_OPTIONS_TI, "Tell me more about this 'makeover'.", "Sure, do it.", "No thanks.", "Cool amulet! Can i have one?");
		} else if (getStage() == 1) {
			if (componentId == 1) {
				setStage(2);
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Tell me more about this 'makeover'." }, IS_PLAYER, player.getIndex(), 9827);
			} else if (componentId == 2) {
				setStage(11);
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Sure, do it." }, IS_PLAYER, player.getIndex(), 9827);
			} else if (componentId == 3) {
				setStage(13);
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "No thanks. I'm happy as Saradomin made me." }, IS_PLAYER, player.getIndex(), 9827);
			} else {
				setStage(14);
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Cool amulet! Can i have one?" }, IS_PLAYER, player.getIndex(), 9827);
			}
		} else if (getStage() == 2) {
			setStage(3);
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "Why, of course! Basically, and I will explain so that", "you understand it correctly," }, IS_NPC, npcId, 9827);
		} else if (getStage() == 3) {
			setStage(4);
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "I use my secret magical technique to melt your body down", "into a puddle of its elements" }, IS_NPC, npcId, 9827);

		} else if (getStage() == 4) {
			setStage(5);
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "When I have broken down all components of your body, I", "then rebuilt it into the form I am thinking of." }, IS_NPC, npcId, 9827);

		} else if (getStage() == 5) {
			setStage(6);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "Or, you know, something vaguely close enough, anyway." }, IS_NPC, npcId, 9827);
		} else if (getStage() == 6) {
			setStage(7);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Uh... that doesn't sound particualry safe to me." }, IS_PLAYER, player.getIndex(), 9827);
		} else if (getStage() == 7) {
			setStage(8);
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "It's as safe as houses. Why, I have only had thirty-six", "major accidents this month!" }, IS_NPC, npcId, 9827);

		} else if (getStage() == 8) {
			setStage(9);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "So what do you say? Feel like a change?" }, IS_NPC, npcId, 9827);
		} else if (getStage() == 9) {
			setStage(10);
			sendDialogue(SEND_2_OPTIONS, DEFAULT_OPTIONS_TI, "Sure do it.", "No thanks.");
		} else if (getStage() == 10) {
			if (componentId == 1) {
				setStage(11);
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Sure, do it." }, IS_PLAYER, player.getIndex(), 9827);
			} else {
				setStage(13);
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "No thanks. I'm happy as Saradomin made me." }, IS_PLAYER, player.getIndex(), 9827);
			}
		} else if (getStage() == 11) {
			setStage(12);
			sendEntityDialogue(SEND_3_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "You, of course, agree that if by some accident you are", "turned into a frog you have no rights for compensation or", "refund." }, IS_NPC, npcId, 9827);
		} else if (getStage() == 12) {
			PlayerLook.openMageMakeOver(player);
			end();
		} else if (getStage() == 13) {
			setStage(-2);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "Ehhh...suit yourself." }, IS_NPC, npcId, 9827);
		} else if (getStage() == 14) {
			setStage(15);
			sendEntityDialogue(SEND_3_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "No problem, but please remember that the amulet I will", "sell you is only a copy of my own. It contains no magical", "powers and, as such, it will only cost you 100 coins." }, IS_NPC, npcId, 9827);
		} else if (getStage() == 15) {
			setStage(16);
			sendDialogue(SEND_2_OPTIONS, DEFAULT_OPTIONS_TI, "Sure, here you go.", "No way! That's too expensive.");
		} else if (getStage() == 16) {
			if (componentId == 1) {
				if (!player.getInventory().containsItem(995, 100)) {
					end();
				} else {
					setStage(17);
					sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Sure, here you go." }, IS_PLAYER, player.getIndex(), 9827);
				}
			} else {
				setStage(-2);
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "No way! That's too expensive." }, IS_PLAYER, player.getIndex(), 9827);
			}
		} else if (getStage() == 17) {
			setStage(18);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { "", "You receive an amulet in exchange for 100 coins." }, IS_ITEM, 7803, SEND_NO_EMOTE);
			player.getInventory().deleteItem(995, 100);
			player.getInventory().addItem(7803, 1);
		} else if (getStage() == 18) {
			setStage(0);
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "I can alter you physical form if you wish. Would you like", " me to perform my magicks on you?" }, IS_NPC, npcId, 9827);
		} else if (getStage() == 19) {
			setStage(20);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "What was?" }, IS_PLAYER, player.getIndex(), 9827);
		} else if (getStage() == 20) {
			setStage(21);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Nothing! It's all fine you seem alive anyway." }, IS_NPC, npcId, 9827);
		} else if (getStage() == 21) {
			setStage(-2);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "	Uh, thanks, I guess." }, IS_PLAYER, player.getIndex(), 9827);
		} else {
			end();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
