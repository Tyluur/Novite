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
import novite.rs.game.player.dialogues.impl.TeleportConfirmation;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;
import novite.rs.utility.game.TeleportLocations;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 29, 2014
 */
public class TeleportationSelectListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 72 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		Pages currentPage = (Pages) player.getAttributes().get("tele_page");
		if (currentPage == null)
			return true;
		int optionIndex = -1;
		String option = null;
		for (int i = 0; i < COMPONENT_IDS.length; i++) {
			if (COMPONENT_IDS[i][1] == buttonId) {
				if (i >= currentPage.options.length) {
					break;
				}
				option = currentPage.options[i].replaceAll("\\<[^>]*>", "");
				optionIndex = i;
				break;
			}
		}
		if (option == null)
			return true;
		Pages pageToOpen = null;
		for (Pages pages : Pages.values()) {
			if (Utils.formatPlayerNameForDisplay(pages.name()).equalsIgnoreCase(option)) {
				pageToOpen = pages;
				break;
			}
		}
		if (pageToOpen != null) {
			openPage(player, pageToOpen);
		} else {
			if (option.equalsIgnoreCase("Back")) {
				openPage(player, currentPage.getBackPage());
			} else if (!currentPage.handleCustomOption(player, option)) {
				teleportPlayer(player, currentPage.getTeleportTiles()[optionIndex]);
			}
		}
		return true;
	}

	/**
	 * Displays the interface for the player
	 * 
	 * @param player
	 *            The player
	 */
	public static void display(Player player) {
		openPage(player, Pages.SELECT_A_TELEPORT);
	}

	/**
	 * Opens the parameterized page for the player
	 * 
	 * @param player
	 *            The player
	 * @param page
	 *            The page to open
	 */
	private static void openPage(Player player, Pages page) {
		player.closeInterfaces();
		int interfaceId = 72;
		int length = Utils.getInterfaceDefinitionsComponentsSize(interfaceId);

		for (int i = 0; i < length; i++) {
			player.getPackets().sendIComponentText(interfaceId, i, "");
		}
		for (int i = 0; i < page.options.length; i++) {
			String option = page.options[i];

			player.getPackets().sendIComponentText(interfaceId, COMPONENT_IDS[i][0], option);
		}

		player.getAttributes().put("tele_page", page);
		player.getPackets().sendIComponentText(interfaceId, 55, Utils.formatPlayerNameForDisplay(page.name()));
		player.getInterfaceManager().sendInterface(interfaceId);
	}

	public enum Pages {

		SELECT_A_TELEPORT("<col=" + ChatColors.YELLOW + ">Training Locations</col>", "Monster Dungeons", "Boss Lairs", "Minigames", "Skill Training Locations", "City Teleports"),
		/** Training Page */
		TRAINING_LOCATIONS("<col=" + ChatColors.YELLOW + ">Training Island", "Edgeville Dungeon", "Ape Atoll", "Rock Crabs", "Rock Lobsters", BACK) {
			@Override
			public WorldTile[] getTeleportTiles() {
				return new WorldTile[] { TeleportLocations.TRAINING_ISLAND, TeleportLocations.EDGEVILLE_DUNGEON, TeleportLocations.APE_ATOLL_MONKEYS, TeleportLocations.RELLEKA_CRABS, TeleportLocations.ROCK_LOBSTERS };
			}

			@Override
			public boolean handleCustomOption(Player player, String option) {
				if (option.equalsIgnoreCase("Rock Crabs")) {
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
					return true;
				} else if (option.equalsIgnoreCase("Ape atoll")) {
					player.getDialogueManager().startDialogue(new Dialogue() {

						@Override
						public void start() {
							sendOptionsDialogue("Select an Option", "Guards", "Skeletons");
						}

						@Override
						public void run(int interfaceId, int option) {
							end();
							if (option == FIRST) {
								teleportPlayer(player, TeleportLocations.APE_ATOLL_MONKEYS);
							} else {
								teleportPlayer(player, TeleportLocations.APE_ATOLL_SKELETONS);
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
		},
		/** City Teleports */
		CITY_TELEPORTS("Varrock", "Lumbridge", "Falador", "Seers Village", "Ardougne", "Miscellania", "Catherby", "Taverly", BACK) {
			@Override
			public WorldTile[] getTeleportTiles() {
				return new WorldTile[] { TeleportLocations.VARROCK, TeleportLocations.LUMBRIDGE, TeleportLocations.FALADOR, TeleportLocations.CAMELOT, TeleportLocations.ARDOUGNE, TeleportLocations.MISCELLANIA, TeleportLocations.CATHERBY, TeleportLocations.TAVERLY };
			}
		},
		/** Skilling */
		SKILL_TRAINING_LOCATIONS("Agility Training", "Hunting Jungle", "Falador Mining Dungeon", "Fishing Guild", "Living Rock Cavern", BACK) {
			@Override
			public WorldTile[] getTeleportTiles() {
				return new WorldTile[] { null, TeleportLocations.HUNTER_TRAINING, TeleportLocations.FALADOR_MINING, TeleportLocations.FISHING_GUILD, TeleportLocations.LIVING_ROCK_CAVERNS };
			}

			@Override
			public boolean handleCustomOption(Player player, String option) {
				if (option.equalsIgnoreCase("Agility training")) {
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
					return true;
				}
				return false;
			}
		},
		/** Monster Dungeons */
		MONSTER_DUNGEONS("Taverly Dungeon", "Kalphite Dungeon", "Brimhaven Dungeon", "Ancient Cavern", "Ice Plateau <col=" + ChatColors.RED + ">(Wild</col>)", "Slayer Tower", "Fremennik Dungeon", BACK) {
			@Override
			public WorldTile[] getTeleportTiles() {
				return new WorldTile[] { TeleportLocations.TAVERLY_DUNGEON, TeleportLocations.KALPHITE_DUNGEON, TeleportLocations.BRIMHAVEN_DUNGEON, TeleportLocations.ANCIENT_CAVERN, TeleportLocations.ICE_PLATEAU, TeleportLocations.SLAYER_TOWER, TeleportLocations.RELEKKA_SLAYER_DUNGEON };
			}
		},
		/** Minigames */
		MINIGAMES("Warriors Guild", "Barrows", "Fight Caves", BACK) {
			@Override
			public WorldTile[] getTeleportTiles() {
				return new WorldTile[] { TeleportLocations.WARRIORS_GUILD, TeleportLocations.BARROWS, FightCaves.OUTSIDE };
			}
		},
		/** Boss Lairs */
		BOSS_LAIRS("Godwars", "Glacors", "Ice Strykewyrms", "Corporeal Beast", "Tormented Demons", "Jadinko Lair", "Kalphite Lair", "King Black Dragon Lair", "Dagganoth Lair", BACK) {
			@Override
			public WorldTile[] getTeleportTiles() {
				return new WorldTile[] { TeleportLocations.GODWARS_ENTRANCE, TeleportLocations.GLACORS, TeleportLocations.STRYKEWYRM_DUNGEON, TeleportLocations.CORPOREAL_BEAST, TeleportLocations.TORMENTED_DEMONS, TeleportLocations.JADINKO_LAIR, TeleportLocations.KALPHITE_DUNGEON, TeleportLocations.KING_BLACK_DRAGON, TeleportLocations.DAGANNOTH_KINGS };
			}

			@Override
			public boolean handleCustomOption(Player player, String option) {
				if (option.equalsIgnoreCase("Godwars")) {
					player.getDialogueManager().startDialogue(new Dialogue() {

						@Override
						public void start() {
							sendOptionsDialogue("Select an Option", "Godwars Entrance", "Nex Arena");
						}

						@Override
						public void run(int interfaceId, int option) {
							if (option == FIRST) {
								teleportPlayer(player, TeleportLocations.GODWARS_ENTRANCE);
							} else {
								teleportPlayer(player, TeleportLocations.NEX_ARENA);
								player.setCloseInterfacesEvent(new Runnable() {

									@Override
									public void run() {
										player.getControllerManager().startController("GodWars");
									}
								});
							}
							end();
						}

						@Override
						public void finish() {
						}

					});
					return true;
				}
				return false;
			}
		};

		Pages(String... options) {
			this.options = options;
		}

		/**
		 * The options on this page
		 */
		private final String[] options;

		/**
		 * Gets the teleport tiles in order of listing.
		 */
		public WorldTile[] getTeleportTiles() {
			return null;
		}

		/**
		 * The page that
		 */
		public Pages getBackPage() {
			return SELECT_A_TELEPORT;
		}

		/**
		 * Handles a custom option
		 */
		public boolean handleCustomOption(Player player, String option) {
			return false;
		}

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
	 * The array of component ids, with the line id first then the button id
	 * next
	 */
	private static final int[][] COMPONENT_IDS = new int[][] { { 31, 68 }, { 36, 73 }, { 32, 67 }, { 37, 72 }, { 33, 66 }, { 38, 71 }, { 34, 65 }, { 39, 70 }, { 35, 64 }, { 40, 69 } };
	private static final String BACK = "<col=" + ChatColors.YELLOW + ">Back</col>";

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
