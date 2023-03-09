package novite.rs.game.player;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;

import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.item.Item;
import novite.rs.game.item.ItemsContainer;
import novite.rs.game.player.content.Runecrafting;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.AchievementManager;
import novite.rs.game.player.dialogues.SimpleMessage;
import novite.rs.game.player.dialogues.impl.SimpleItemMessage;
import novite.rs.utility.ItemExamines;
import novite.rs.utility.game.ChatColors;

public final class Equipment implements Serializable {

	private static final long serialVersionUID = -4147163237095647617L;

	public static final byte SLOT_HAT = 0, SLOT_CAPE = 1, SLOT_AMULET = 2, SLOT_WEAPON = 3, SLOT_CHEST = 4, SLOT_SHIELD = 5, SLOT_LEGS = 7, SLOT_HANDS = 9, SLOT_FEET = 10, SLOT_RING = 12, SLOT_ARROWS = 13, SLOT_AURA = 14;

	private ItemsContainer<Item> items;

	private transient Player player;
	private transient int equipmentHpIncrease;

	static final int[] DISABLED_SLOTS = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0 };

	public Equipment() {
		items = new ItemsContainer<Item>(15, false);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void init() {
		player.getPackets().sendItems(94, items);
		refresh(null);
	}

	public void refresh(int... slots) {
		if (slots != null) {
			player.getPackets().sendUpdateItems(94, items, slots);
			player.getCombatDefinitions().checkAttackStyle();
		}
		player.getCombatDefinitions().refreshBonuses();
		refreshConfigs(slots == null);
	}

	public void reset() {
		items.reset();
		init();
	}

	public Item getItem(int slot) {
		return items.get(slot);
	}

	public void sendExamine(int slotId) {
		Item item = items.get(slotId);
		if (item == null) {
			return;
		}
		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
	}

	public void refreshConfigs(boolean init) {
		double hpIncrease = 0;
		for (int index = 0; index < items.getSize(); index++) {
			Item item = items.get(index);
			if (item == null) {
				continue;
			}
			int id = item.getId();
			if (index == Equipment.SLOT_HAT) {
				if (id == 20135 || id == 20137 // torva
						|| id == 20147 || id == 20149 // pernix
						|| id == 20159 || id == 20161 // virtus
				) {
					hpIncrease += 66;
				} else if (id == Runecrafting.AIR_TIARA) {
					player.getPackets().sendConfig(491, 1);
				} else if (id == Runecrafting.EARTH_TIARA) {
					player.getPackets().sendConfig(491, 8);
				} else if (id == Runecrafting.FIRE_TIARA) {
					player.getPackets().sendConfig(491, 16);
				} else if (id == Runecrafting.WATER_TIARA) {
					player.getPackets().sendConfig(491, 4);
				} else if (id == Runecrafting.BODY_TIARA) {
					player.getPackets().sendConfig(491, 32);
				} else if (id == Runecrafting.MIND_TIARA) {
					player.getPackets().sendConfig(491, 2);
				} else if (id == Runecrafting.OMNI_TIARA) {
					player.getPackets().sendConfig(491, -1);
				}

			} else if (index == Equipment.SLOT_CHEST) {
				if (id == 20139 || id == 20141 // torva
						|| id == 20151 || id == 20153 // pernix
						|| id == 20163 || id == 20165 // virtus
				) {
					hpIncrease += 200;
				}
			} else if (index == Equipment.SLOT_LEGS) {
				if (id == 20143 || id == 20145 // torva
						|| id == 20155 || id == 20157 // pernix
						|| id == 20167 || id == 20169 // virtus
				) {
					hpIncrease += 134;
				}
			}

		}
		if (hpIncrease != equipmentHpIncrease) {
			equipmentHpIncrease = (int) hpIncrease;
			if (!init) {
				player.refreshHitPoints();
			}
		}
	}

	public static boolean hideArms(Item item) {
		String name = item.getName().toLowerCase();
		if (name.contains("d'hide body") || name.contains("dragonhide body") || name.equals("stripy pirate shirt") || (name.contains("chainbody") && (name.contains("iron") || name.contains("bronze") || name.contains("steel") || name.contains("black") || name.contains("mithril") || name.contains("adamant") || name.contains("rune") || name.contains("white"))) || name.equals("leather body") || name.equals("hardleather body") || name.contains("studded body")) {
			return false;
		}
		return item.getDefinitions().getEquipType() == 6;
	}

	public static boolean hideHair(Item item) {
		return item.getDefinitions().getEquipType() == 8;
	}

	public static boolean showBeard(Item item) {
		String name = item.getName().toLowerCase();
		return !hideHair(item) || name.contains("horns") || name.contains("hat") || name.contains("afro") || name.contains("cowl") || name.contains("tattoo") || name.contains("headdress") || name.contains("hood") || (name.contains("mask") && !name.contains("h'ween")) || (name.contains("helm"));
	}

	public static int getItemSlot(int itemId) {
		if (itemId == 6583)
			return Equipment.SLOT_RING;
		return ItemDefinitions.getItemDefinitions(itemId).getEquipSlot();
	}

	public boolean hasTwoHandedWeapon() {
		Item item = items.get(SLOT_WEAPON);
		if (item == null) {
			return false;
		}
		return isTwoHandedWeapon(item);
	}

	public static boolean isTwoHandedWeapon(Item item) {
		return item.getDefinitions().getEquipType() == 5;
	}

	public int getWeaponRenderEmote() {
		Item weapon = items.get(3);
		if (weapon == null) {
			return 1426;
		}
		return weapon.getDefinitions().getRenderAnimId();
	}

	public boolean hasShield() {
		return items.get(5) != null;
	}

	public int getWeaponId() {
		Item item = items.get(SLOT_WEAPON);
		if (item == null) {
			return -1;
		}
		return item.getId();
	}

	public int getChestId() {
		Item item = items.get(SLOT_CHEST);
		if (item == null) {
			return -1;
		}
		return item.getId();
	}

	public int getHatId() {
		Item item = items.get(SLOT_HAT);
		if (item == null) {
			return -1;
		}
		return item.getId();
	}

	public int getShieldId() {
		Item item = items.get(SLOT_SHIELD);
		if (item == null) {
			return -1;
		}
		return item.getId();
	}

	public int getLegsId() {
		Item item = items.get(SLOT_LEGS);
		if (item == null) {
			return -1;
		}
		return item.getId();
	}

	public void removeAmmo(int ammoId, int ammount) {
		if (ammount == -1) {
			items.remove(SLOT_WEAPON, new Item(ammoId, 1));
			refresh(SLOT_WEAPON);
		} else {
			items.remove(SLOT_ARROWS, new Item(ammoId, ammount));
			refresh(SLOT_ARROWS);
		}
	}

	public int getAuraId() {
		Item item = items.get(SLOT_AURA);
		if (item == null) {
			return -1;
		}
		return item.getId();
	}

	public int getCapeId() {
		Item item = items.get(SLOT_CAPE);
		if (item == null) {
			return -1;
		}
		return item.getId();
	}

	public int getRingId() {
		Item item = items.get(SLOT_RING);
		if (item == null) {
			return -1;
		}
		return item.getId();
	}

	public int getAmmoId() {
		Item item = items.get(SLOT_ARROWS);
		if (item == null) {
			return -1;
		}
		return item.getId();
	}

	public void deleteItem(int itemId, int amount) {
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(new Item(itemId, amount));
		refreshItems(itemsBefore);
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			if (itemsBefore[index] != items.getItems()[index]) {
				changedSlots[count++] = index;
			}
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
	}

	public int getBootsId() {
		Item item = items.get(SLOT_FEET);
		if (item == null) {
			return -1;
		}
		return item.getId();
	}

	public int getGlovesId() {
		Item item = items.get(SLOT_HANDS);
		if (item == null) {
			return -1;
		}
		return item.getId();
	}

	public ItemsContainer<Item> getItems() {
		return items;
	}

	public int getEquipmentHpIncrease() {
		return equipmentHpIncrease;
	}

	public void setEquipmentHpIncrease(int hp) {
		this.equipmentHpIncrease = hp;
	}

	public boolean wearingArmour() {
		for (int i = 0; i < items.getSize(); i++) {
			if (getItem(i) != null) {
				return true;
			}
		}
		return false;
	}

	public int getAmuletId() {
		Item item = items.get(SLOT_AMULET);
		if (item == null) {
			return -1;
		}
		return item.getId();
	}

	private static boolean isMaxed(Player player, Item item) {
		int skillId = -1;
		for (int i = 0; i < Skills.SKILL_NAME.length; i++) {
			if (i == Skills.CONSTRUCTION || i == Skills.DUNGEONEERING)
				continue;
			if (player.getSkills().getLevelForXp(i) < 99) {
				skillId = i;
				break;
			}
		}
		if (skillId != -1) {
			player.getDialogueManager().startDialogue(SimpleItemMessage.class, item.getId(), "You must level your " + Skills.SKILL_NAME[skillId] + " to 99 first.");
			return false;
		}
		return true;
	}

	public static boolean canWear(Item item, Player player) {
		if (item.getId() == 7462 && player.getSkills().getLevelForXp(Skills.DEFENCE) < 40) {
			player.sendMessage("You are not high enough level to use this item");
			player.sendMessage("You need to have a defence level of 40.");
			return false;
		}
		if (item.getId() == 7461 && player.getSkills().getLevelForXp(Skills.DEFENCE) < 40) {
			player.sendMessage("You are not high enough level to use this item");
			player.sendMessage("You need to have a defence level of 40.");
			return false;
		}
		if (item.getId() == 7460 && player.getSkills().getLevelForXp(Skills.DEFENCE) < 8) {
			player.sendMessage("You are not high enough level to use this item");
			player.sendMessage("You need to have a defence level of 8.");
			return false;
		}
		if (item.getName().toLowerCase().contains("void")) {
			int[] skills = new int[] { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.HITPOINTS, Skills.RANGE, Skills.MAGIC };
			boolean valid = true;
			for (int skill : skills) {
				if (player.getSkills().getLevelForXp(skill) < 42) {
					player.sendMessage("You are not high enough level to use this item");
					player.sendMessage("You need to have a " + Skills.SKILL_NAME[skill].toLowerCase() + " level of 42.");
					valid = false;
					break;
				}
			}
			if (valid && player.getSkills().getLevelForXp(Skills.PRAYER) < 22) {
				player.sendMessage("You are not high enough level to use this item");
				player.sendMessage("You need to have a prayer level of 22.");
				valid = false;
			}
			return valid;
		}
		if (item.getName().toLowerCase().contains("max ")) {
			return isMaxed(player, item);
		}
		if (item.getName().toLowerCase().contains("completionist")) {
			if (!isMaxed(player, item))
				return false;
			Iterator<Entry<String, Achievement>> it = AchievementManager.getAchievements().entrySet().iterator();
			Achievement unfinished = null;
			while (it.hasNext()) {
				Entry<String, Achievement> entry = it.next();
				if (!entry.getValue().isComplete(player)) {
					unfinished = entry.getValue();
					break;
				}
			}
			if (unfinished != null) {
				player.getDialogueManager().startDialogue(SimpleMessage.class, "You must finish this achievement first!", "<col=" + ChatColors.BLUE + ">" + unfinished.getTitle());
				return false;
			}
		}
		return true;
	}

	public void forceRemove(int itemId, int amount) {
		items.remove(new Item(itemId, amount));
	}

	public boolean containsOneItem(int... itemIds) {
		for (int itemId : itemIds) {
			if (items.containsOne(new Item(itemId, 1))) {
				return true;
			}
		}
		return false;
	}

}