package novite.rs.game.player.content;

import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.WorldObject;
import novite.rs.game.item.Item;
import novite.rs.game.item.KeyItemDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.actions.crafting.Jewellery;
import novite.rs.game.player.actions.custom.DFSCreateAction;
import novite.rs.game.player.content.cannon.CannonAlgorithms;
import novite.rs.game.player.content.slayer.Abilities;
import novite.rs.game.player.dialogues.SimpleMessage;
import novite.rs.game.player.dialogues.impl.SimpleItemMessage;

/**
 * Handles items being used on objects and other items
 *
 * @author Tyluur<itstyluur@gmail.com>
 * @since March 23rd, 2014
 */
public class ItemOnTypeHandler implements KeyItemDefinitions {

	/**
	 * Handles the item on the other item
	 *
	 * @param player
	 *            The player
	 * @param used
	 *            The item used
	 * @param with
	 *            The item used with
	 * @return {@code Boolean.TRUE} if successful
	 */
	public static boolean handleItemOnItem(Player player, Item used, Item with) {
		for (ItemTypes type : ItemTypes.values()) {
			for (int typeUse : type.used) {
				for (int typeWith : type.with) {
					if (typeUse == used.getId() && with.getId() == typeWith || (typeUse == with.getId() && typeWith == used.getId())) {
						type.useTogether(player, used.getId(), with.getId());
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Handles what to do when used together
	 *
	 * @param player
	 *            The player
	 * @param object
	 *            The object
	 * @param item
	 *            The item
	 */
	public static boolean handleItemOnObject(Player player, WorldObject object, Item item) {
		for (ObjectTypes type : ObjectTypes.values()) {
			String objName = object.getDefinitions().name;
			if (objName.toLowerCase().equals(type.name.toLowerCase()) && type.itemId == item.getId()) {
				type.useTogether(player, object, item);
				return true;
			}
		}
		return false;
	}

	private enum ObjectTypes {

		CANNON("Dwarf Multicannon", 2) {
			@Override
			public void useTogether(Player player, WorldObject object, Item item) {
				if (object.getId() == 6) {
					CannonAlgorithms.addCannonBalls(player, object, item.getId());
					return;
				}
			}
		},

		FURNACE("Furnace", 2357) {
			@Override
			public void useTogether(Player player, WorldObject object, Item item) {
				Jewellery.displayJewelleryInterface(player);
				return;
			}
		},

		DFS_MAKING("Anvil", 11286) {
			@Override
			public void useTogether(Player player, WorldObject object, Item item) {
				player.getActionManager().setAction(new DFSCreateAction());
			}
		};

		ObjectTypes(String name, int itemId) {
			this.name = name;
			this.itemId = itemId;
		}

		private final String name;
		private final int itemId;

		/**
		 * Handles what to do when used together
		 *
		 * @param player
		 *            The player
		 * @param object
		 *            The object
		 * @param item
		 *            The item
		 */
		public abstract void useTogether(Player player, WorldObject object, Item item);
	}

	private enum ItemTypes {

		LOOP_KEYS(985, 987) {
			@Override
			public void useTogether(Player player, int used, int with) {
				player.getInventory().deleteItem(used, 1);
				player.getInventory().deleteItem(with, 1);
				player.getInventory().addItem(989, 1);
			}
		},
		
		SLAYER_HELM(new int[] { 4164, 4551, 4166, 4168, 8901 }, new int[] { 4164, 4551, 4166, 4168, 8901 }) {
			@Override
			public void useTogether(Player player, int used, int with) {
				if (!player.getSlayerManager().hasUnlockedAbility(Abilities.CRAFT_SLAYER_HELMETS)) {
					player.getDialogueManager().startDialogue(SimpleItemMessage.class, 15492, "You must unlock the correct ability to make this helmet.");
					return;
				}
				int[] items = new int[] { 4164, 4551, 4166, 4168, 8901 };
				if (player.getInventory().containsOneItem(items)) {
					for (int item : items) {
						player.getInventory().deleteItem(item, 1);
					}
					player.getInventory().addItem(15492, 1);
				} else {
					player.getDialogueManager().startDialogue(SimpleItemMessage.class, 15492, "You don't have all the items required to make a slayer helmet");
				}
			}
		},
		
		DRAGON_SQ(2366, 2368) {

			@Override
			public void useTogether(Player player, int used, int with) {
				player.getInventory().deleteItem(used, 1);
				player.getInventory().deleteItem(with, 1);
				player.getInventory().addItem(1187, 1);
			}
		},

		VINE(21369, 4151) {
			@Override
			public void useTogether(Player player, int used, int with) {
				player.getInventory().deleteItem(used, 1);
				player.getInventory().deleteItem(with, 1);
				player.getInventory().addItem(21371, 1);
			}
		},
		GODSWORD_MAKING(new int[] { ARMADYL_HILT, BANDOS_HILT, SARADOMIN_HILT, ZAMORAK_HILT }, new int[] { SHARD_1, SHARD_2, SHARD_3 }) {
			@Override
			public void useTogether(Player player, int used, int with) {
				int hiltId = (with > 11708 ? used : with);
				if (!player.getInventory().containsItems(new int[] { SHARD_1, SHARD_2, SHARD_3 }, new int[] { 1, 1, 1 })) {
					player.sendMessage("You don't have all the shards!");
					return;
				}
				if (!player.getInventory().contains(hiltId)) {
					return;
				}
				player.getInventory().deleteItem(hiltId, 1);
				player.getInventory().deleteItem(SHARD_1, 1);
				player.getInventory().deleteItem(SHARD_2, 1);
				player.getInventory().deleteItem(SHARD_3, 1);
				player.getInventory().addItem(getSwordByHilt(hiltId));
			}
		},

		BLESSED_MAKING(13754, 13734) {
			@Override
			public void useTogether(Player player, int used, int with) {
				player.getInventory().deleteItem(used, 1);
				player.getInventory().deleteItem(with, 1);
				player.getInventory().addItem(BLESSED_SPIRIT_SHIELD, 1);
			}
		},
		SPIRIT_SHIELD_MAKING(new int[] { ARCANE_SIGIL, DIVINE_SIGIL, ELYSIAN_SIGIL, SPECTRAL_SIGIL }, new int[] { BLESSED_SPIRIT_SHIELD }) {
			@Override
			public void useTogether(Player player, int used, int with) {
				int sigilId = (with == BLESSED_SPIRIT_SHIELD ? used : with);
				int shieldId = getShieldBySigil(sigilId);
				if (!player.getInventory().contains(sigilId)) {
					return;
				}
				if (player.getSkills().getLevelForXp(Skills.PRAYER) < 90) {
					player.sendMessage("You need a prayer level of 90 to create this spirit shield.");
					return;
				}
				if (player.getSkills().getLevelForXp(Skills.SMITHING) < 85) {
					player.sendMessage("You need a smithing level of 85 to create this sprit shield.");
					return;
				}
				player.getInventory().deleteItem(sigilId, 1);
				player.getInventory().deleteItem(BLESSED_SPIRIT_SHIELD, 1);
				player.getInventory().addItem(new Item(shieldId, 1));
				player.getDialogueManager().startDialogue(SimpleMessage.class, "You successfully create a " + ItemDefinitions.getItemDefinitions(shieldId).getName() + ".");
			}
		};

		ItemTypes(int[] used, int[] with) {
			this.used = used;
			this.with = with;
		}

		ItemTypes(int used, int with) {
			this.used = new int[] { used };
			this.with = new int[] { with };
		}

		private final int[] used;
		private final int[] with;

		public abstract void useTogether(Player player, int used, int with);
	}

	private static Item getSwordByHilt(int used) {
		switch (used) {
		case ARMADYL_HILT:
			return new Item(11694);
		case BANDOS_HILT:
			return new Item(11696);
		case SARADOMIN_HILT:
			return new Item(11698);
		case ZAMORAK_HILT:
			return new Item(11700);
		}
		return null;
	}

	/**
	 * Gets the id of the spirit shield by the sigil
	 *
	 * @param sigil
	 *            The id of the sigil
	 * @return
	 */
	private static int getShieldBySigil(int sigil) {
		switch (sigil) {
		case ARCANE_SIGIL:
			return ARCANE_SPIRIT_SHIELD;
		case DIVINE_SIGIL:
			return DIVINE_SPIRIT_SHIELD;
		case ELYSIAN_SIGIL:
			return ELYSIAN_SPIRIT_SHIELD;
		case SPECTRAL_SIGIL:
			return SPECTRAL_SPIRIT_SHIELD;
		}
		return -1;
	}
}