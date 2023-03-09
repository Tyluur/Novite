package novite.rs.networking.codec.handlers;

import novite.rs.Constants;
import novite.rs.api.event.EventListener.ClickOption;
import novite.rs.api.event.EventManager;
import novite.rs.api.event.listeners.interfaces.Scrollable;
import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.engine.process.impl.SwitchingProcessor;
import novite.rs.engine.process.impl.SwitchingProcessor.ItemSwitch;
import novite.rs.game.Animation;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.item.ArmourSetOpening;
import novite.rs.game.item.Decanting;
import novite.rs.game.item.Item;
import novite.rs.game.item.ItemConstants;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.familiar.Familiar.SpecialAttack;
import novite.rs.game.npc.others.pet.Pet;
import novite.rs.game.player.Equipment;
import novite.rs.game.player.Inventory;
import novite.rs.game.player.Player;
import novite.rs.game.player.actions.BoxAction;
import novite.rs.game.player.actions.BoxAction.HunterEquipment;
import novite.rs.game.player.actions.Firemaking;
import novite.rs.game.player.actions.Fletching;
import novite.rs.game.player.actions.Fletching.Fletch;
import novite.rs.game.player.actions.GemCutting;
import novite.rs.game.player.actions.GemCutting.Gem;
import novite.rs.game.player.actions.HerbCleaning;
import novite.rs.game.player.actions.Herblore;
import novite.rs.game.player.actions.Hunter;
import novite.rs.game.player.actions.Hunter.FlyingEntities;
import novite.rs.game.player.actions.LeatherCrafting;
import novite.rs.game.player.actions.Woodcutting.Nest;
import novite.rs.game.player.actions.prayer.Bone;
import novite.rs.game.player.actions.prayer.Burying;
import novite.rs.game.player.actions.summoning.Pouches;
import novite.rs.game.player.actions.summoning.Summoning;
import novite.rs.game.player.content.AncientEffigies;
import novite.rs.game.player.content.ArmourSets;
import novite.rs.game.player.content.Foods;
import novite.rs.game.player.content.ItemOnTypeHandler;
import novite.rs.game.player.content.Magic;
import novite.rs.game.player.content.Pots;
import novite.rs.game.player.content.Runecrafting;
import novite.rs.game.player.content.SkillCapeCustomizer;
import novite.rs.game.player.content.scrolls.ClueScrollManager;
import novite.rs.game.player.dialogues.Transportation;
import novite.rs.game.player.dialogues.impl.Bob;
import novite.rs.game.player.dialogues.impl.NameChangeRequest;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.utility.Caskets;
import novite.rs.utility.ItemExamines;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;
import novite.rs.utility.game.TeleportLocations;

public class InventoryOptionsHandler {

	public static void handleItemOption2(final Player player, final int slotId, final int itemId, Item item) {
		if (EventManager.get().handleItemClick(player, item, ClickOption.SECOND)) {
			return;
		}
		if (itemId == 15262) {
			ArmourSets.openSkillPack(player, itemId, 12183, 5000, player.getInventory().getNumberOf(itemId));
		} else if (itemId == 15362) {
			ArmourSets.openSkillPack(player, itemId, 230, 50, player.getInventory().getNumberOf(itemId));
		} else if (itemId == 15363) {
			ArmourSets.openSkillPack(player, itemId, 228, 50, player.getInventory().getNumberOf(itemId));
		} else if (itemId == 15364) {
			ArmourSets.openSkillPack(player, itemId, 222, 50, player.getInventory().getNumberOf(itemId));
		} else if (itemId == 15365) {
			ArmourSets.openSkillPack(player, itemId, 9979, 50, player.getInventory().getNumberOf(itemId));
		} else if (itemId >= 5509 && itemId <= 5514) {
			int pouch = -1;
			if (itemId == 5509) {
				pouch = 0;
			}
			if (itemId == 5510) {
				pouch = 1;
			}
			if (itemId == 5512) {
				pouch = 2;
			}
			if (itemId == 5514) {
				pouch = 3;
			}
			Runecrafting.emptyPouch(player, pouch);
			player.stopAll(false);
		} else {
			if (player.isEquipDisabled()) {
				return;
			}
			if (!Equipment.canWear(item, player)) {
				return;
			}
			if (!player.getQuestManager().handleItem(player, item))
				return;
			SwitchingProcessor.addToQueue(new ItemSwitch(player, slotId, itemId));
		}
	}

	public static void handleItemOption1(Player player, final int slotId, final int itemId, Item item) {
		if (!player.getInventory().containsItem(item.getId(), item.getAmount())) {
			return;
		}
		long time = Utils.currentTimeMillis();
		if (player.getLockDelay() >= time || player.getEmotesManager().getNextEmoteEnd() >= time) {
			return;
		}
		player.stopAll(false);
		if (Foods.eat(player, item, slotId)) {
			return;
		}
		if (ClueScrollManager.isScroll(item)) {
			player.getClueScrollManager().read(item);
			return;
		}
		if (ArmourSetOpening.handleSetOpening(player, item)) {
			return;
		}
		if (Nest.isNest(itemId)) {
		    Nest.searchNest(player, slotId);
		    return;
		}
		if (EventManager.get().handleItemClick(player, item, ClickOption.FIRST)) {
			return;
		}
		if (!player.getControllerManager().handleItemOption1(player, slotId, itemId, item)) {
			return;
		}
		if (Pots.pot(player, item, slotId)) {
			return;
		}
		if (itemId >= 5509 && itemId <= 5514) {
			int pouch = -1;
			if (itemId == 5509) {
				pouch = 0;
			}
			if (itemId == 5510) {
				pouch = 1;
			}
			if (itemId == 5512) {
				pouch = 2;
			}
			if (itemId == 5514) {
				pouch = 3;
			}
			Runecrafting.fillPouch(player, pouch);
			return;
		}
		if (itemId == 10944) {
			StringBuilder bldr = new StringBuilder();
			bldr.append("<col=" + ChatColors.BLUE + ">What is a vote token?<br><br>");
			bldr.append("A vote token is an item you receive for voting. You can exchange these for rewards in the vote shop via <col=" + ChatColors.MAROON + ">Party Pete</col>.<br><br>");
			bldr.append("<col=" + ChatColors.BLUE + ">How many vote tokens do I get when I vote?<br><br>");
			bldr.append("You will receive 5 vote tokens when you vote.");
			Scrollable.sendScroll(player, "", bldr.toString());
			return;
		}
		if (itemId == 10942) { // name change token
			player.getDialogueManager().startDialogue(NameChangeRequest.class);
			return;
		}
		if (itemId == 952) {// spade
			player.resetWalkSteps();
			player.setNextAnimation(new Animation(830));
			if (player.getClueScrollManager().successfulDig()) {
				return;
			}
			player.getPackets().sendGameMessage("Nothing interesting happens.");
			return;
		}
		if (HerbCleaning.clean(player, item, slotId)) {
			return;
		}
		Bone bone = Bone.forId(itemId);
		if (bone != null) {
			Burying.bury(player, slotId);
			return;
		}
		if (Magic.useTabTeleport(player, itemId)) {
			return;
		}
		if (itemId == AncientEffigies.SATED_ANCIENT_EFFIGY || itemId == AncientEffigies.GORGED_ANCIENT_EFFIGY || itemId == AncientEffigies.NOURISHED_ANCIENT_EFFIGY || itemId == AncientEffigies.STARVED_ANCIENT_EFFIGY) {
			player.getDialogueManager().startDialogue("AncientEffigiesD", itemId);
		} else if (itemId == 4155) {
			player.getDialogueManager().startDialogue("EnchantedGemDialouge");
		} else if (itemId == HunterEquipment.BOX.getId()) {
			player.getActionManager().setAction(new BoxAction(HunterEquipment.BOX));
		} else if (itemId == HunterEquipment.BRID_SNARE.getId()) {
			player.getActionManager().setAction(new BoxAction(HunterEquipment.BRID_SNARE));
		} else if (itemId == 15262) {
			ArmourSets.openSkillPack(player, itemId, 12183, 5000, 1);
		} else if (itemId == 15362) {
			ArmourSets.openSkillPack(player, itemId, 230, 50, 1);
		} else if (itemId == 15363) {
			ArmourSets.openSkillPack(player, itemId, 228, 50, 1);
		} else if (itemId == 15364) {
			ArmourSets.openSkillPack(player, itemId, 222, 50, 1);
		} else if (itemId == 15365) {
			ArmourSets.openSkillPack(player, itemId, 9979, 50, 1);
		} else if (Caskets.PUZZLE_CASKET.getItemId() == itemId) {
			player.getClueScrollManager().giveRewards();
			return;
		} else if (itemId == Caskets.REGULAR.getItemId()) {
			Caskets casket = Caskets.REGULAR;
			player.getInventory().deleteItem(slotId, item);

			/** Giving the users key halfs */
			if (Utils.percentageChance(30)) {
				int keyId = Utils.percentageChance(50) ? 985 : 987;
				player.getInventory().addItem(keyId, 1);
				player.sendMessage("You loot the casket and receive a " + ItemDefinitions.getItemDefinitions(keyId).getName() + "!");
			}

			int coinAmount = casket.getBaseCoinAmount();
			int extra = Utils.random(1000, casket.getExtraCoinAmount());
			player.getInventory().addDroppable(new Item(995, coinAmount + extra));
			return;
		}
		if (Constants.DEBUG) {
			System.out.println("Item Select:" + itemId + ", Slot Id:" + slotId);
		}
	}

	/*
	 * returns the other
	 */
	public static Item contains(int id1, Item item1, Item item2) {
		if (item1.getId() == id1) {
			return item2;
		}
		if (item2.getId() == id1) {
			return item1;
		}
		return null;
	}

	public static boolean contains(int id1, int id2, Item... items) {
		boolean containsId1 = false;
		boolean containsId2 = false;
		for (Item item : items) {
			if (item.getId() == id1) {
				containsId1 = true;
			} else if (item.getId() == id2) {
				containsId2 = true;
			}
		}
		return containsId1 && containsId2;
	}

	public static void handleItemOnItem(final Player player, InputStream stream) {
		int hash1 = stream.readIntV1();
		int interfaceId = hash1 >> 16;
		int itemUsedId = stream.readShort128();
		int fromSlot = stream.readShortLE128();
		int hash2 = stream.readIntV2();
		int interfaceId2 = hash2 >> 16;
		int itemUsedWithId = stream.readShort128();
		int toSlot = stream.readShortLE();
		int component = hash2 & 0xFFFF;

		if ((interfaceId2 == 747 || interfaceId2 == 662) && interfaceId == Inventory.INVENTORY_INTERFACE) {
			if (player.getFamiliar() != null) {
				player.getFamiliar().setSpecial(true);
				if (player.getFamiliar().getSpecialAttack() == SpecialAttack.ITEM) {
					if (player.getFamiliar().hasSpecialOn()) {
						player.getFamiliar().submitSpecial(toSlot);
					}
				}
			}
			return;
		}

		if (interfaceId2 == 192 && interfaceId == Inventory.INVENTORY_INTERFACE) {
			if (toSlot >= 28) {
				return;
			}
			long time = Utils.currentTimeMillis();
			Item item = player.getInventory().getItem(toSlot);
			if (item == null || item.getId() != itemUsedWithId) {
				return;
			}
			if (player.getLockDelay() >= time || player.getEmotesManager().getNextEmoteEnd() >= time) {
				return;
			}
			Magic.processNormalSpell(player, component, (byte) toSlot);
		}

		if (interfaceId == Inventory.INVENTORY_INTERFACE && interfaceId == interfaceId2 && !player.getInterfaceManager().containsInventoryInter()) {
			if (toSlot >= 28 || fromSlot >= 28) {
				return;
			}
			Item usedWith = player.getInventory().getItem(toSlot);
			Item itemUsed = player.getInventory().getItem(fromSlot);
			if (itemUsed == null || usedWith == null || itemUsed.getId() != itemUsedId || usedWith.getId() != itemUsedWithId) {
				return;
			}
			if (toSlot == fromSlot) {
				return;
			}
			player.stopAll();
			if (!player.getControllerManager().canUseItemOnItem(itemUsed, usedWith)) {
				return;
			}
			if (Decanting.handleDecanting(player, fromSlot, toSlot)) {
				return;
			}
			Fletch fletch = Fletching.isFletching(usedWith, itemUsed);
			if (fletch != null) {
				player.getDialogueManager().startDialogue("FletchingD", fletch);
				return;
			}
			int herblore = Herblore.isHerbloreSkill(itemUsed, usedWith);
			if (herblore > -1) {
				player.getDialogueManager().startDialogue("HerbloreD", herblore, itemUsed, usedWith);
				return;
			}
			if (itemUsed.getId() == LeatherCrafting.NEEDLE.getId() || usedWith.getId() == LeatherCrafting.NEEDLE.getId()) {
				if (LeatherCrafting.handleItemOnItem(player, itemUsed, usedWith)) {
					return;
				}
			}
			if (ItemOnTypeHandler.handleItemOnItem(player, itemUsed, usedWith)) {
				return;
			} else if (Firemaking.isFiremaking(player, itemUsed, usedWith)) {
				return;
			} else if (contains(1755, Gem.OPAL.getUncut(), itemUsed, usedWith)) {
				GemCutting.cut(player, Gem.OPAL);
			} else if (contains(1755, Gem.JADE.getUncut(), itemUsed, usedWith)) {
				GemCutting.cut(player, Gem.JADE);
			} else if (contains(1755, Gem.RED_TOPAZ.getUncut(), itemUsed, usedWith)) {
				GemCutting.cut(player, Gem.RED_TOPAZ);
			} else if (contains(1755, Gem.SAPPHIRE.getUncut(), itemUsed, usedWith)) {
				GemCutting.cut(player, Gem.SAPPHIRE);
			} else if (contains(1755, Gem.EMERALD.getUncut(), itemUsed, usedWith)) {
				GemCutting.cut(player, Gem.EMERALD);
			} else if (contains(1755, Gem.RUBY.getUncut(), itemUsed, usedWith)) {
				GemCutting.cut(player, Gem.RUBY);
			} else if (contains(1755, Gem.DIAMOND.getUncut(), itemUsed, usedWith)) {
				GemCutting.cut(player, Gem.DIAMOND);
			} else if (contains(1755, Gem.DRAGONSTONE.getUncut(), itemUsed, usedWith)) {
				GemCutting.cut(player, Gem.DRAGONSTONE);
			} else if (contains(1755, Gem.ONYX.getUncut(), itemUsed, usedWith)) {
				GemCutting.cut(player, Gem.ONYX);
			} else {
				player.getPackets().sendGameMessage("Nothing interesting happens.");
			}
			if (Constants.DEBUG) {
				System.out.println("Used:" + itemUsed.getId() + ", With:" + usedWith.getId());
			}
		}
	}

	public static void handleItemOption3(Player player, int slotId, int itemId, Item item) {
		long time = Utils.currentTimeMillis();
		if (player.getLockDelay() >= time || player.getEmotesManager().getNextEmoteEnd() >= time) {
			return;
		}
		if (EventManager.get().handleItemClick(player, item, ClickOption.THIRD)) {
			return;
		}
		player.stopAll(false);
		FlyingEntities impJar = FlyingEntities.forId(itemId);
		if (impJar != null) {
			Hunter.openJar(player, impJar, slotId);
		} else if (itemId == 20767 || itemId == 20769 || itemId == 20771) {
			SkillCapeCustomizer.startCustomizing(player, itemId);
		} else if (Equipment.getItemSlot(itemId) == Equipment.SLOT_AURA) {
			player.getAuraManager().sendTimeRemaining(itemId);
		}
	}

	public static void handleItemOption4(Player player, int slotId, int itemId, Item item) {
		System.out.println("Option 4");
	}

	public static void handleItemOption5(Player player, int slotId, int itemId, Item item) {
		System.out.println("Option 5");
	}

	public static void handleItemOption6(Player player, int slotId, int itemId, Item item) {
		long time = Utils.currentTimeMillis();
		if (player.getLockDelay() >= time || player.getEmotesManager().getNextEmoteEnd() >= time) {
			return;
		}
		player.stopAll(false);
		Pouches pouches = Pouches.forId(itemId);
		if (pouches != null) {
			Summoning.spawnFamiliar(player, pouches);
		} else if (itemId == 1438) {
			Runecrafting.locate(player, 3127, 3405);
		} else if (itemId == 1440) {
			Runecrafting.locate(player, 3306, 3474);
		} else if (itemId == 1442) {
			Runecrafting.locate(player, 3313, 3255);
		} else if (itemId == 1444) {
			Runecrafting.locate(player, 3185, 3165);
		} else if (itemId == 1446) {
			Runecrafting.locate(player, 3053, 3445);
		} else if (itemId == 1448) {
			Runecrafting.locate(player, 2982, 3514);
		} else if (itemId <= 1712 && itemId >= 1706 || itemId >= 10354 && itemId <= 10362) {
			player.getDialogueManager().startDialogue(Transportation.class, "Edgeville", new WorldTile(3087, 3496, 0), "Karamja", new WorldTile(2918, 3176, 0), "Draynor Village", new WorldTile(3105, 3251, 0), "Al Kharid", new WorldTile(3293, 3163, 0), itemId);
		} else if (itemId >= 2552 && itemId <= 2567) {
			player.getDialogueManager().startDialogue(Transportation.class, "Duel Arena", TeleportLocations.DUEL_ARENA, "Castle Wars", TeleportLocations.CASTLE_WARS, "Mobilising Armies", new WorldTile(2411, 2841, 0), "Novite Games", TeleportLocations.NOVITE_GAMES, itemId);
		} else if (itemId == 1704 || itemId == 10352) {
			player.getPackets().sendGameMessage("The amulet has ran out of charges. You need to recharge it if you wish it use it once more.");
		} else if (itemId >= 3853 && itemId <= 3867) {
			player.getDialogueManager().startDialogue("Transportation", "Burthrope Games Room", new WorldTile(2880, 3559, 0), "Barbarian Outpost", new WorldTile(2519, 3571, 0), "Gamers' Grotto", new WorldTile(2970, 9679, 0), "Corporeal Beast", new WorldTile(2886, 4377, 0), itemId);
		}
	}

	public static void handleItemOption7(Player player, int slotId, int itemId, Item item) {
		long time = System.currentTimeMillis();
		if (player.getLockDelay() >= time || player.getEmotesManager().getNextEmoteEnd() >= time) {
			return;
		}
		if (!player.getControllerManager().canDropItem(item)) {
			return;
		}
		player.stopAll();
		if (!ItemConstants.isTradeable(item.getId())) {
			player.getDialogueManager().startDialogue("DestroyItemOption", new Object[] { Integer.valueOf(slotId), item });
			return;
		}
		if (player.getPetManager().spawnPet(itemId, true)) {
			return;
		}
		if (!Bob.isBarrows(item.getId()) && player.getCharges().degradeCompletly(item)) {
			return;
		}
		player.setNextAnimation(new Animation(-1));
		player.getInventory().deleteItem(slotId, item);
		player.getPackets().sendSound(2739, 0, 1);
		World.addGroundItem(item, new WorldTile(player), player, true, 60);
	}

	public static void handleItemOption8(Player player, int slotId, int itemId, Item item) {
		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
	}

	public static void handleItemOnNPC(Player player, NPC npc, Item item) {
		if (npc instanceof Pet) {
			player.turnTo(npc);
			player.getPetManager().eat(item.getId(), (Pet) npc);
			return;
		}

	}

}