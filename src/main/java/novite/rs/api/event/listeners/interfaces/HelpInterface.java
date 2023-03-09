package novite.rs.api.event.listeners.interfaces;

import novite.rs.Constants;
import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 20, 2014
 */
public class HelpInterface extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 761 };
	}

	private enum HelpOption {

		SHOPS("Where can I buy items?") {
			@Override
			public void onClick(Player player) {
				Scrollable.sendQuestScroll(player, "Where can I buy items?", "Every item in the game can be bought from the <col=" + ChatColors.RED + ">grand ex<col=" + ChatColors.RED + ">change</col> directly north of Edgeville bank.", "However, some items do not have a system quantity, meaning another player has to be selling the item in the grand exchange for you to buy it.", "<br>", "You can also talk to the Shopkeeper in Edgeville to buy miscellaneous items");
			}
		},
		TELEPORTS("How do I teleport around the world?") {
			@Override
			public void onClick(Player player) {
				Scrollable.sendQuestScroll(player, "How do I teleport around the world?", "West of the Edgeville bank, there is a spirit tree. Interact with the tree to be teleported around the game world free of cost");
			}
		},
		MINIGAMES("Where can I engage in minigames?") {
			@Override
			public void onClick(Player player) {
				Scrollable.sendQuestScroll(player, "Where can I engage in minigames?", "At the southern entrance to the Edgeville bank, there is an 'Event Portal'.", "Enter the portal and select an event or minigame to join.");
				
			}
		},
		SLAYER("How do I get a slayer task?") {
			@Override
			public void onClick(Player player) {
				Scrollable.sendQuestScroll(player, "How do I get a slayer task?", "Converse with Lapalok who is directly east of the edgeville bank.", "He will give you a slayer task, can cancel your task, or buy slayer items from him.");
			}
		},
		TRAINING("Where do I go to train?") {
			@Override
			public void onClick(Player player) {
				Scrollable.sendQuestScroll(player, "Where do I go to train?", "Interact with the spirit tree west of the edgeville bank and select teleport.", "Choose 'Training Dungeon' and travel around in the dungeon to find the best monster suitable for you to train on.");
			}
		},
		QUESTING("How do I get quest items such as ava's accumulators?") {
			@Override
			public void onClick(Player player) {
				Scrollable.sendQuestScroll(player, "How do I get quest items?", "You must complete quests to get quest items.", "There are several quests that are available to you.", "<br>", "Check out your quest diary to see your progress on all quests.", "<br>", "Enter the Event Portal at and select Questing Dome to start", "and finish quests.");
			}
		},
		MONEY_MAKING("What is the best way to make money?") {
			@Override
			public void onClick(Player player) {
				Scrollable.sendQuestScroll(player, "What is the best way to make money?", "There are several great ways to make money in the game.", "<br>", "1. Kill monsters, they will drop caskets on occasion. These caskets can be opened to give you money. If you have the monsters as your slayer task, there is a higher chance of getting a casket.", "2. Complete quests. These will give you cash sums of rewards and access to other cool things.", "3. Skill and barter the resources made to other people. This can be in the grand exchange or via trade. The grand exchange, however can be used to sell your items while you are offline.");
			}
		},
		SUMMONING_TRAINING("Where do I train summoning?") {
			@Override
			public void onClick(Player player) {
				Scrollable.sendQuestScroll(player, "Where do I train summoning?", "Directly east of the edgeville bank, there is an obelisk set up.", "Select 'Infuse-Pouch' on the obelisk and select the type of scrolls or pouches you wish to create.", "<br>", "All monsters in the realm of <col=" + ChatColors.RED + ">" + Constants.SERVER_NAME + "</col> drop charms. The higher the combat level of the monster, the higher the chance to drop charms.", "The amount of charms dropped also increase when the monster is a bigger size.");
			}
		};

		HelpOption(String line) {
			this.line = line;
		}

		private final String line;

		public abstract void onClick(Player player);
	}

	/**
	 * Displays the help interface to the player
	 * 
	 * @param player
	 *            The player to display the interface to
	 */
	public static void display(Player player) {
		player.closeInterfaces();
		int interfaceId = 761;
		int startLine = 10;
		int lineCount = Utils.getInterfaceDefinitionsComponentsSize(interfaceId);
		for (int k = 0; k < lineCount; k++) {
			player.getPackets().sendIComponentText(interfaceId, k, "");
		}
		for (HelpOption option : HelpOption.values()) {
			player.getPackets().sendIComponentText(interfaceId, startLine, "<col=" + ChatColors.LIGHT_BLUE + ">" + option.line);
			startLine++;
		}

		player.getPackets().sendIComponentText(interfaceId, 6, "What do you need help with?");
		player.getPackets().sendIComponentText(interfaceId, 23, "If you can't find what you need help with here, request staff assistance.");
		player.getInterfaceManager().sendInterface(interfaceId);
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		if (buttonId >= 10 && buttonId <= 21) {
			int index = (buttonId - 10);
			for (HelpOption option : HelpOption.values()) {
				if (option.ordinal() == index) {
					option.onClick(player);
					break;
				}
			}
		}
		return true;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleNPCClick(Player player, NPC npc, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
	}

}
