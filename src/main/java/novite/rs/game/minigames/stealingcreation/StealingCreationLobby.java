package novite.rs.game.minigames.stealingcreation;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import novite.rs.engine.CoresManager;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.utility.Utils;

public class StealingCreationLobby {

	private static final int[] TOTAL_SKILL_IDS = { Skills.WOODCUTTING, Skills.MINING, Skills.FISHING, Skills.HUNTER, Skills.COOKING, Skills.HERBLORE, Skills.CRAFTING, Skills.SMITHING, Skills.FLETCHING, Skills.RUNECRAFTING, Skills.CONSTRUCTION };
	private static final int[] TOTAL_COMBAT_IDS = { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.HITPOINTS, Skills.RANGE, Skills.MAGIC, Skills.PRAYER, Skills.SUMMONING };
	private static List<Player> redTeam, blueTeam;
	private static LobbyTimer lobbyTask;

	static {
		reset();
	}

	private static class LobbyTimer extends TimerTask {

		public int minutes;

		public LobbyTimer() {
			minutes = 2;
		}

		@Override
		public void run() {
			minutes--;
			if (minutes == 0) {
				passToGame(); // cancel since all players left
				removeTask();
				return;
			}
			updateInterfaces();
		}

		public int getMinutes() {
			return minutes;
		}

		private void passToGame() { // should be safe right?
			StealingCreationManager.createGame(8, blueTeam, redTeam);
			reset();
		}
	}

	public static void reset() {
		redTeam = new ArrayList<Player>();
		blueTeam = new ArrayList<Player>();
	}

	private static boolean hasRequiredPlayers() {
		if (redTeam.size() >= 5 && blueTeam.size() >= 5) {
			return true;
		}
		return false;
	}

	public static boolean enterTeamLobby(Player player, boolean onRedTeam) {
		if (!canEnter(player, onRedTeam)) {
			return false;
		}
		if (onRedTeam) {
			if (!redTeam.contains(player)) {
				redTeam.add(player);
			}
		} else {
			if (!blueTeam.contains(player)) {
				blueTeam.add(player);
			}
		}
		if (hasRequiredPlayers() && lobbyTask == null) {
			CoresManager.fastExecutor.scheduleAtFixedRate(lobbyTask = new LobbyTimer(), 60000, 60000);
		}
		player.getControllerManager().startController("StealingCreationLobby");
		updateInterfaces();
		return true;
	}

	public static void removePlayer(Player player) {
		if (redTeam.contains(player)) {
			redTeam.remove(player);
		} else if (blueTeam.contains(player)) {
			blueTeam.remove(player);
		}
		if (!hasRequiredPlayers()) {
			removeTask();
		}
		player.getInterfaceManager().removeOverlay(false);
		player.getControllerManager().removeControlerWithoutCheck();
		updateInterfaces();
	}

	public static void removeTask() {
		if (lobbyTask == null) {
			return;
		}
		lobbyTask.cancel();
		lobbyTask = null;
	}

	public static void updateInterfaces() {
		for (Player player : redTeam) {
			updateTeamInterface(player, true);
		}
		for (Player player : blueTeam) {
			updateTeamInterface(player, false);
		}
	}

	public static void updateTeamInterface(Player player, boolean inRedTeam) {
		int skillTotal = getTotalLevel(TOTAL_SKILL_IDS, inRedTeam);
		int combatTotal = getTotalLevel(TOTAL_COMBAT_IDS, inRedTeam);
		int otherSkillTotal = getTotalLevel(TOTAL_SKILL_IDS, !inRedTeam);
		int otherCombatTotal = getTotalLevel(TOTAL_COMBAT_IDS, !inRedTeam);
		if (lobbyTask != null) {
			player.getPackets().sendHideIComponent(804, 2, true);
			player.getPackets().sendIComponentText(804, 1, "Game Start : " + lobbyTask.getMinutes() + " mins");
		} else {
			player.getPackets().sendHideIComponent(804, 2, false);
			int players = 5 - (inRedTeam ? redTeam.size() : blueTeam.size());
			player.getPackets().sendIComponentText(804, 34, String.valueOf(players < 0 ? 0 : players));
			players = 5 - (inRedTeam ? blueTeam.size() : redTeam.size());
			player.getPackets().sendIComponentText(804, 33, String.valueOf(players < 0 ? 0 : players));
		}
		player.getPackets().sendIComponentText(804, 4, "" + skillTotal);
		player.getPackets().sendIComponentText(804, 5, "" + combatTotal);
		player.getPackets().sendIComponentText(804, 6, "" + otherCombatTotal);
		player.getPackets().sendIComponentText(804, 7, "" + otherSkillTotal);
	}

	private static boolean canEnter(Player player, boolean inRedTeam) {
		int skillTotal = getTotalLevel(TOTAL_SKILL_IDS, inRedTeam);
		int combatTotal = getTotalLevel(TOTAL_COMBAT_IDS, inRedTeam);
		int otherSkillTotal = getTotalLevel(TOTAL_SKILL_IDS, !inRedTeam);
		int otherCombatTotal = getTotalLevel(TOTAL_COMBAT_IDS, !inRedTeam);
		if ((skillTotal + combatTotal) > (otherSkillTotal + otherCombatTotal)) {
			player.getPackets().sendGameMessage("This team is too strong for you to join at present.");
			return false;
		} else if (player.getEquipment().wearingArmour() || player.getInventory().getFreeSlots() != 28 || player.getFamiliar() != null || player.getPet() != null) {
			player.getPackets().sendGameMessage("You may not take any items into Stealing Creation. You can use the nearby bank deposit bank to empty your inventory and storn wore items.");
			return false;
		} else if (player.getInventory().getNumberOf(995) != 0) {
			player.getPackets().sendGameMessage("The mystics sneer at your greed, as you try to smuggle coins in.");
			player.getPackets().sendGameMessage("Deposite your money pouch's coins at the local deposite box near you.");
			return false;
		} else if (player.getTemporaryAttributtes().get("SC_PENALTY") != null && (Long) player.getTemporaryAttributtes().get("SC_PENALTY") >= Utils.currentTimeMillis()) {
			long time = (Long) player.getTemporaryAttributtes().get("SC_PENALTY");
			player.getDialogueManager().startDialogue("You have betrayed the mystics and must wait " + (int) (time / 60000) + "minutes.");
			return false;
		}
		return true;
	}

	private static int getTotalLevel(int[] ids, boolean inRedTeam) {
		int skillTotal = 0;
		for (Player player : inRedTeam ? redTeam : blueTeam) {
			if (player == null) {
				continue;
			}
			for (int skillRequested : ids) {
				skillTotal += player.getSkills().getLevel(skillRequested);
			}
		}
		return skillTotal;
	}

	public static List<Player> getRedTeam() {
		return redTeam;
	}

	public static List<Player> getBlueTeam() {
		return blueTeam;
	}
}
