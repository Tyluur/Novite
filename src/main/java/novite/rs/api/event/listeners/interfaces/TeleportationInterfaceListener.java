package novite.rs.api.event.listeners.interfaces;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.Magic;
import novite.rs.game.player.controlers.impl.FightCaves;
import novite.rs.game.player.controlers.impl.Wilderness;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.game.player.dialogues.SimpleNPCMessage;
import novite.rs.game.player.dialogues.impl.TeleportConfirmation;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;
import novite.rs.utility.game.TeleportLocations;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 21, 2014
 */
public class TeleportationInterfaceListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 583 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		Teleports teleport = getTeleport(buttonId);
		if (teleport != null)
			teleport.onClick(player);
		return true;
	}

	/**
	 * Displays the teleportation interface to the player
	 * 
	 * @param player
	 */
	public static void display(Player player) {
		int interfaceId = 583;
		int length = Utils.getInterfaceDefinitionsComponentsSize(interfaceId);

		for (int i = 0; i < length; i++) {
			player.getPackets().sendIComponentText(interfaceId, i, "");
		}

		player.getPackets().sendConfig(1045, -1);
		player.getPackets().sendConfig(1046, -1);
		player.getPackets().sendConfig(1047, -1);
		player.getPackets().sendConfig(1501, -1);

		for (int i = 0; i < COMPONENT_IDS.length; i++) {
			Teleports teleport = getTeleport(COMPONENT_IDS[i]);
			if (teleport != null) {
				int index = i + 1;
				String col = "FFFFFF";
				if (index >= 1 && index <= 8)
					col = "77F0D8";
				else if (index <= 13)
					col = "5872E9";
				else if (index <= 22)
					col = "CA10A2";
				else
					col = "E4C52A";
				player.getPackets().sendIComponentText(interfaceId, COMPONENT_IDS[i], "<col=" + col + ">" + teleport.text);
			}
		}
		player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 3637, "Organization order: The first page is for monster teleports.", "When you scroll to the bottom you will find skilling locations.");

		player.getPackets().sendIComponentText(interfaceId, 14, "Which place would you like to go to?");
		player.getInterfaceManager().sendInterface(interfaceId);
	}

	/**
	 * Teleports the player to the location
	 * 
	 * @param player
	 *            The player
	 * @param tile
	 *            The tile
	 */
	private static void teleportPlayer(Player player, WorldTile tile) {
		player.closeInterfaces();
		if (Wilderness.isAtWild(tile)) {
			player.getDialogueManager().startDialogue(TeleportConfirmation.class, tile);
		} else {
			Magic.sendGreenTeleportSpell(player, tile);
		}
	}

	/**
	 * Gets the teleport based on the button id clicked
	 * 
	 * @param buttonId
	 *            The button id clicked
	 * @return
	 */
	private static Teleports getTeleport(int buttonId) {
		for (int i = 0; i < COMPONENT_IDS.length; i++) {
			if (COMPONENT_IDS[i] == buttonId) {
				for (Teleports teleport : Teleports.values()) {
					if (teleport.ordinal() == i) {
						return teleport;
					}
				}
				break;
			}
		}
		return null;
	}

	/**
	 * The array of component ids relevant to this interface
	 */
	private static final int[] COMPONENT_IDS = new int[] { 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 70, 59, 60, 61, 63, 62, 64, 69, 65, 66, 67, 68, 71, 72, 79, 73, 74, 75, 76, 77, 78 };

	private enum Teleports {

		TRAINING_ISLAND("Training Island", TeleportLocations.TRAINING_ISLAND), EDGEVILLE_DUNGEON("Edgeville Dungeon", TeleportLocations.EDGEVILLE_DUNGEON), APE_ATOLL("Ape Atoll Training", TeleportLocations.APE_ATOLL_MONKEYS), ROCK_CRAB_ISLAND("Rock Crab Training", null) {
			@Override
			public void onClick(Player player) {
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						sendOptionsDialogue("Select a Location", "Waterbirth Island Crabs", "Relleka East Crabs");
					}

					@Override
					public void run(int interfaceId, int option) {
						switch (option) {
						case FIRST:
							teleportPlayer(player, TeleportLocations.ROCK_CRAB_ISLAND);
							break;
						case SECOND:
							teleportPlayer(player, TeleportLocations.RELLEKA_CRABS);
							break;
						}
						end();
					}

					@Override
					public void finish() {

					}
				});
			}
		},
		TAVERLY_DUNGEON("Taverly Dungeon", TeleportLocations.TAVERLY_DUNGEON), KALPHITE_DUNGEON("Kalphite Dungeon", TeleportLocations.KALPHITE_DUNGEON), BRIMHAVEN_DUNGEON("Brimhaven Dungeon", TeleportLocations.BRIMHAVEN_DUNGEON), ANCIENT_CAVERN("Ancient Cavern Dungeon", TeleportLocations.ANCIENT_CAVERN), WARRIORS_GUILD("Warriors Guild", TeleportLocations.WARRIORS_GUILD), BARROWS("Barrows", TeleportLocations.BARROWS), GODWARS("Godwars", TeleportLocations.GODWARS_ENTRANCE), SLAYER_TOWER("Slayer Tower", TeleportLocations.SLAYER_TOWER), TZHAAR_CAVES("Tzhaar Caves", FightCaves.OUTSIDE), ICE_PLATEAU("Ice Plateau <col=" + ChatColors.RED + ">(Wild</col>)", TeleportLocations.ICE_PLATEAU), ROCK_LOBSTERS("Rock Lobsters", TeleportLocations.ROCK_LOBSTERS), DAGANNOTH_KINGS("Dagannoth Kings", TeleportLocations.DAGANNOTH_KINGS), KALPHITE_QUEEN("Kalphite Queen", TeleportLocations.KALPHITE_QUEEN), GLACORS("Glacors", TeleportLocations.GLACORS), STRYKEWYRM_DUNGEON("Strykewyrms", TeleportLocations.STRYKEWYRM_DUNGEON), CORPOREAL_BEAST("Corporeal Beast", TeleportLocations.CORPOREAL_BEAST), KING_BLACK_DRAGON("King Black Dragon", TeleportLocations.KING_BLACK_DRAGON), JADINKO_LAIR("Jadinko Liar", TeleportLocations.JADINKO_LAIR), TORMENTED_DEMONS("Tormented Demons", TeleportLocations.TORMENTED_DEMONS), AGILITY_TRAINING("Agility Training", null) {
			@Override
			public void onClick(Player player) {
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						sendOptionsDialogue("Select a Location", "Gnome Agility", "Barbarian Agility", "Wilderness Agility", "Cancel");
					}

					@Override
					public void run(int interfaceId, int option) {
						end();
						switch (option) {
						case FIRST:
							teleportPlayer(player, TeleportLocations.GNOME_AGILITY);
							break;
						case SECOND:
							teleportPlayer(player, TeleportLocations.BARBARIAN_AGILITY);
							break;
						case THIRD:
							teleportPlayer(player, TeleportLocations.WILDERNESS_AGILITY);
							break;
						}
					}

					@Override
					public void finish() {

					}
				});
			}
		},
		HUNTER_TRAINING("Hunting Jungle", TeleportLocations.HUNTER_TRAINING), FALADOR_MINING("Falador Mining Dungeon", TeleportLocations.FALADOR_MINING), FISHING_GUILD("Fishing Guild", TeleportLocations.FISHING_GUILD), LIVING_ROCK_CAVERNS("Living Rock Caverns", TeleportLocations.LIVING_ROCK_CAVERNS);

		Teleports(String text) {
			this.text = text;
			this.tile = null;
		}

		Teleports(String text, WorldTile tile) {
			this.text = text;
			this.tile = tile;
		}

		/**
		 * The text over the button
		 */
		private final String text;

		/**
		 * The worldtile teleported to
		 */
		private final WorldTile tile;

		/**
		 * What to do when the button is clicked
		 * 
		 * @param player
		 */
		public void onClick(Player player) {
			if (tile != null)
				teleportPlayer(player, tile);
			else {
				System.err.println("[SEVERE] Attempted to teleport a player in teleportation interface but tele tile was null");
			}
		}
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		return false;
	}

	@Override
	public boolean handleNPCClick(Player player, NPC npc, ClickOption option) {
		return false;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		return false;
	}
}