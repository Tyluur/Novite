package novite.rs.game.player.dialogues;

public class StrangeFace extends Dialogue {

	@Override
	public void start() {
		sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Hello?" }, IS_PLAYER, player.getIndex(), 9827);

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (getStage() == -1) {
			setStage(0);
			sendDialogue(SEND_1_TEXT_INFO, "Hello.");
			player.getPackets().sendVoice(7890);
			// set camera
		} else if (getStage() == 0) {
			setStage(1);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Woah!" }, IS_PLAYER, player.getIndex(), 9827);
		} else if (getStage() == 1) {
			setStage(2);
			sendDialogue(SEND_2_TEXT_INFO, "It is intrigring that you took so long, before coming to me. Fearful,", "traveller?");
			player.getPackets().sendVoice(7895);
		} else if (getStage() == 2) {
			setStage(3);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Should I be?" }, IS_PLAYER, player.getIndex(), 9827);
		} else if (getStage() == 3) {
			setStage(4);
			sendDialogue(SEND_3_TEXT_INFO, "It is my duty to inform you that many warriors fight here, and they", "all succumb to defeat eventually. If that instills terror in you, walk", "away now.");
			player.getPackets().sendVoice(7881);
		} else if (getStage() == 4) {
			setStage(5);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "There are monsters in the tower?" }, IS_PLAYER, player.getIndex(), 9827);
		} else if (getStage() == 5) {
			setStage(6);
			sendDialogue(SEND_4_TEXT_INFO, "If that is the terminolgy you would use, yes. Through the powers", "bestowed upon me by my creator, I can generate opponents for you", "based on your memories of them. Men and women have fought here", "for generations.");
			player.getPackets().sendVoice(7908);
		} else if (getStage() == 6) {
			setStage(7);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Impressive. So you control the tower?" }, IS_PLAYER, player.getIndex(), 9827);

		} else if (getStage() == 7) {
			setStage(8);
			sendDialogue(SEND_2_TEXT_INFO, "The Tower is I, and I have control of the tower. I see what happens,", "in any corner of any floor. I am always watching.");
			player.getPackets().sendVoice(7909);
		} else if (getStage() == 8) {
			setStage(9);
			sendDialogue(SEND_1_TEXT_INFO, "So you believe yourself a mighty warrior?");
			player.getPackets().sendVoice(7907);
		} else if (getStage() == 9) {
			setStage(10);
			sendDialogue(SEND_2_LARGE_OPTIONS, DEFAULT_OPTIONS_TI, "Only the greatest warrior that ever lived!", "I'm pretty handy with a weapon.");
		} else if (getStage() == 10) {
			setStage((byte) (componentId == 2 ? 100 : 101));
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), componentId == 2 ? "Only the greatest warrior that ever lived!" : "I'm pretty handy with a weapon." }, IS_PLAYER, player.getIndex(), 9827);
		} else if (getStage() == 100 || getStage() == 101) {
			sendDialogue(SEND_1_TEXT_INFO, "Intriguing. " + (getStage() == 100 ? "Such belief in your own abilities..." : "I sence humility in you."));
			if (getStage() == 101) {
				player.getPackets().sendVoice(7887);
			} else {
				player.getPackets().sendVoice(7906);
			}
			setStage(12);
		} else if (getStage() == 12) {
			setStage(13);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "What?" }, IS_PLAYER, player.getIndex(), 9827);
		} else if (getStage() == 13) {
			setStage(14);
			sendDialogue(SEND_2_TEXT_INFO, "Your confidence may have a foundation, but judgement will come in", "battle.");
			player.getPackets().sendVoice(7896);
		} else if (getStage() == 14) {
			setStage(15);
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { player.getDisplayName(), "You mentioned that you were created by someone, but", "why?" }, IS_PLAYER, player.getIndex(), 9827);
		} else if (getStage() == 15) {
			setStage(16);
			sendDialogue(SEND_1_TEXT_INFO, "My purpose...must never stop...");
			player.getPackets().sendVoice(7902);
		} else if (getStage() == 16) {
			setStage(17);
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Sorry? Are you alright?" }, IS_PLAYER, player.getIndex(), 9827);
		} else if (getStage() == 17) {
			setStage(18);
			sendDialogue(SEND_2_TEXT_INFO, "You must fight in the tower, warrior. Demonstrate your ability to", "others and learn.");
			player.getPackets().sendVoice(7879);
		} else if (getStage() == 18) {
			setStage(19);
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { player.getDisplayName(), "I'd thought that, as a guide, you'd be a little more", "welcoming." }, IS_PLAYER, player.getIndex(), 9827);
		} else if (getStage() == 19) {
			setStage(20);
			sendDialogue(SEND_1_TEXT_INFO, "You will find I am welcoming enough.");
			player.getPackets().sendVoice(7911);
		} else if (getStage() == 20) {
			setStage(21);
			sendDialogue(SEND_3_TEXT_INFO, "Now, I can offer you more guidance; or, if you overflow with", "confidence, you can figure out yourself. I am the tower, I am", "ever-present, so come to me if you change your mind.");
			player.getPackets().sendVoice(7872);
		} else if (getStage() == 21) {
			setStage(22);
			sendDialogue(SEND_2_LARGE_OPTIONS, "Receive further instruction?", "Yes.", "No.");
			player.getDominionTower().setTalkedWithFace(true);
		} else if (getStage() == 22) {
			setStage(23);
			if (componentId == 3) {
				sendDialogue(SEND_1_TEXT_INFO, "Your choice. Come back if you change your mind.");
				player.getPackets().sendVoice(7878);
			} else {
				player.getDominionTower().talkToFace(true);
				end();
			}
		} else {
			end();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
