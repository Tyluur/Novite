package novite.rs.api.event.listeners.interfaces;

import java.util.ArrayList;
import java.util.List;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.dialogues.BuyItemDialogue;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.game.player.dialogues.impl.SimpleItemMessage;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 27, 2014
 */
public class SkillSelectionInterface extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 1214 };
	}

	public static void display(Player player) {
		int interfaceId = 1214;
		player.getPackets().sendHideIComponent(interfaceId, 20, true);
		player.getPackets().sendHideIComponent(interfaceId, 22, true);
		player.getPackets().sendHideIComponent(interfaceId, 24, true);
		player.getPackets().sendHideIComponent(interfaceId, 27, true);

		player.getPackets().sendHideIComponent(interfaceId, 58, true);
		player.getPackets().sendHideIComponent(interfaceId, 56, true);
		player.getPackets().sendHideIComponent(interfaceId, 61, true);

		player.getPackets().sendIComponentText(interfaceId, 23, "Select a Skill");
		player.getInterfaceManager().sendInterface(interfaceId);
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		if (buttonId == 18) {
			player.closeInterfaces();
			return true;
		}
		if (buttonId == 23)
			return true;
		if (buttonId == 57) { // total
			player.getDialogueManager().startDialogue(new Dialogue() {

				@Override
				public void start() {
					sendOptionsDialogue("Select an Option", "Purchase Max Cape", "Purchase Completionist Cape");
				}

				@Override
				public void run(int interfaceId, int option) {
					final Item[] capes = new Item[2];
					final Object[] data = new Object[2];
					switch (option) {
					case FIRST:
						capes[0] = new Item(20767);
						capes[1] = new Item(20768);
						data[0] = "Max achievement cape";
						data[1] = 250000;
						break;
					case SECOND:
						capes[0] = new Item(20771);
						capes[1] = new Item(20772);
						data[0] = "Completionist cape";
						data[1] = 1000000;
						break;
					}
					if (capes[0] == null) {
						end();
						return;
					}
					player.getDialogueManager().startDialogue(new BuyItemDialogue() {

						@Override
						public void run(int interfaceId, int option) {
							if (option == YES) {
								if (player.takeMoney((int) data[1])) {
									List<String> messageList = new ArrayList<String>();
									messageList.add("You purchase the achievement cape for " + Utils.format((int) data[1]) + " coins!");
									if (!player.isDonator())
										messageList.add("<col=" + ChatColors.MAROON + ">You can receive trimmed capes if you upgrade to donator rank.");
									sendDialogue(messageList.toArray(new String[messageList.size()]));
									for (Item item : capes) {
										player.getInventory().addDroppable(item);
									}
								} else {
									sendDialogue("You do not have " + Utils.format((int) data[1]) + " coins to buy this cape with.");
								}
								stage = -2;
							} else {
								end();
							}
						}
					}, capes[0], "Buying a " + data[0] + " costs " + Utils.format((int) data[1]) + " coins.");
				}

				@Override
				public void finish() {
				}
			});
			return true;
		}
		if (player.getTemporaryAttributtes().get("skill_selection_type") != null) {
			String type = (String) player.getTemporaryAttributtes().get("skill_selection_type");
			final int skill = Skills.XP_COUNTER_STAT_ORDER[buttonId - 31];
			switch (type) {
			case "EXPERIENCE_KIN":
				if (!player.getInventory().contains(18782)) {
					return true;
				}
				int level = player.getSkills().getLevelForXp(skill);
				double exp = (Math.pow(level, 3) - (2 * Math.pow(level, 2)) + (100 * level)) / 20;
				player.getDialogueManager().startDialogue(SimpleItemMessage.class, 18782, "As you focus on your chosen memories, you feel a", "burning malevolence in the back of your mind.", "You have gained new insight into " + Skills.SKILL_NAME[skill] + ".... but at what", "cost?");
				player.getInventory().deleteItem(18782, 1);
				player.getSkills().addExpNoModifier(skill, exp * 3); 
				break;
			case "CAPES":
				final Item[] capes = player.getSkills().getSkillCape(skill);
				if (capes == null)
					return true;
				player.getDialogueManager().startDialogue(new BuyItemDialogue() {

					@Override
					public void run(int interfaceId, int option) {
						if (option == YES) {
							if (player.takeMoney(99000)) {
								List<String> messageList = new ArrayList<String>();
								messageList.add("You purchase the achievement cape for 99K coins!");
								messageList.add("You are truly a master of " + Skills.SKILL_NAME[skill].toLowerCase() + ".");
								if (!player.isDonator())
									messageList.add("<col=" + ChatColors.MAROON + ">You can receive trimmed capes if you upgrade to donator rank.");
								sendDialogue(messageList.toArray(new String[messageList.size()]));
								for (Item item : capes) {
									player.getInventory().addDroppable(item);
								}
							} else {
								sendDialogue("You do not have 99K coins to buy this cape with.");
							}
							stage = -2;
						} else {
							end();
						}
					}
				}, capes[0], "Buying a " + Skills.SKILL_NAME[skill].toLowerCase() + " achievement cape costs 99K coins.");
				break;
			}
		}
		return true;
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
