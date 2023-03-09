package novite.rs.game.player.dialogues;

import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;

public class FremennikShipmaster extends Dialogue {

	int npcId;
	boolean backing;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		backing = (Boolean) parameters[1];
		if (backing) {
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "Do you want a lift back to the south?" }, IS_NPC, npcId, 9827);
		} else {
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "You want passage to Daemonheim?" }, IS_NPC, npcId, 9827);
		}

	}

	@Override
	public void run(int interfaceId, int componentId) {
		// TODO Auto-generated method stub
		if (backing) {
			if (getStage() == -1) {
				setStage(0);
				sendDialogue(SEND_3_LARGE_OPTIONS, DEFAULT_OPTIONS_TI, "Yes, please.", "Not right now, thanks.", "You look happy.");
			} else if (getStage() == 0) {
				if (componentId == 2) {
					setStage(1);
					sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Yes, please." }, IS_PLAYER, player.getIndex(), 9827);
				} else {
					// not coded options
					end();
				}
			} else if (getStage() == 1) {
				setStage(2);
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "All aboard, then." }, IS_NPC, npcId, 9827);
			} else if (getStage() == 2) {
				sail(player, backing);
				end();
			}
		} else {
			if (getStage() == -1) {
				setStage(0);
				sendDialogue(SEND_4_OPTIONS, DEFAULT_OPTIONS_TI, "Yes, please.", "Not right now, thanks.", "Daemonheim?", "Why are you so grumpy?");
			} else if (getStage() == 0) {
				if (componentId == 1) {
					setStage(1);
					sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Yes, please." }, IS_PLAYER, player.getIndex(), 9827);
				} else {
					// not coded options
					end();
				}
			} else if (getStage() == 1) {
				setStage(2);
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).getName(), "Well, don't stand arround. Get on board." }, IS_NPC, npcId, 9827);
			} else if (getStage() == 2) {
				sail(player, backing);
				end();
			}
		}

	}

	public static void sail(Player player, boolean backing) {
		player.useStairs(-1, backing ? new WorldTile(3254, 3171, 0) : new WorldTile(3511, 3692, 0), 2, 3);
		if (backing) {
			player.getControllerManager().forceStop();
		} else {
			player.getControllerManager().startController("Kalaboss");
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
