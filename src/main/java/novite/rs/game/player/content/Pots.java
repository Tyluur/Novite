package novite.rs.game.player.content;

import novite.rs.game.Animation;
import novite.rs.game.Graphics;
import novite.rs.game.Hit;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.item.Item;
import novite.rs.game.minigames.clanwars.FfaZone;
import novite.rs.game.npc.familiar.Familiar;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.controlers.impl.Wilderness;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public final class Pots {

	public static final int VIAL = 229;

	public static enum Pot {

		ATTACK_POTION(new int[] { 2428, 121, 123, 125 }, Effects.ATTACK_POTION),

		STRENGTH_POTION(new int[] { 113, 115, 117, 119 }, Effects.STRENGTH_POTION),

		DEFENCE_POTION(new int[] { 2432, 133, 135, 137 }, Effects.DEFENCE_POTION),

		RANGE_POTION(new int[] { 2444, 169, 171, 173 }, Effects.RANGE_POTION),

		MAGIC_POTION(new int[] { 3040, 3042, 3044, 3046 }, Effects.MAGIC_POTION),

		MAGIC_FLASK(new int[] { 23423, 23425, 23427, 23429, 23431, 23433 }, Effects.MAGIC_POTION),

		ANTI_POISION(new int[] { 2446, 175, 177, 179 }, Effects.ANTIPOISON), SUPER_ANTI_POISION(new int[] { 2448, 181, 183, 185 }, Effects.SUPER_ANTIPOISON),

		PRAYER_POTION(new int[] { 2434, 139, 141, 143 }, Effects.PRAYER_POTION),

		SUPER_ATT_POTION(new int[] { 2436, 145, 147, 149 }, Effects.SUPER_ATT_POTION),

		SUPER_STR_POTION(new int[] { 2440, 157, 159, 161 }, Effects.SUPER_STR_POTION),

		SUPER_DEF_POTION(new int[] { 2442, 163, 165, 167 }, Effects.SUPER_DEF_POTION),

		ENERGY_POTION(new int[] { 3008, 3010, 3012, 3014 }, Effects.ENERGY_POTION),

		SUPER_ENERGY(new int[] { 3016, 3018, 3020, 3022 }, Effects.SUPER_ENERGY),

		EXTREME_ATT_POTION(new int[] { 15308, 15309, 15310, 15311 }, Effects.EXTREME_ATT_POTION),

		EXTREME_STR_POTION(new int[] { 15312, 15313, 15314, 15315 }, Effects.EXTREME_STR_POTION),

		EXTREME_DEF_POTION(new int[] { 15316, 15317, 15318, 15319 }, Effects.EXTREME_DEF_POTION),

		EXTREME_MAGE_POTION(new int[] { 15320, 15321, 15322, 15323 }, Effects.EXTREME_MAG_POTION),

		EXTREME_RANGE_POTION(new int[] { 15324, 15325, 15326, 15327 }, Effects.EXTREME_RAN_POTION),

		SUPER_RESTORE_POTION(new int[] { 3024, 3026, 3028, 3030 }, Effects.SUPER_RESTORE),

		SARADOMIN_BREW(new int[] { 6685, 6687, 6689, 6691 }, Effects.SARADOMIN_BREW),

		RECOVER_SPECIAL(new int[] { 15300, 15301, 15302, 15303 }, Effects.RECOVER_SPECIAL),

		SUPER_PRAYER(new int[] { 15328, 15329, 15330, 15331 }, Effects.SUPER_PRAYER),

		OVERLOAD(new int[] { 15332, 15333, 15334, 15335 }, Effects.OVERLOAD),

		ANTI_FIRE(new int[] { 2452, 2454, 2456, 2458 }, Effects.ANTI_FIRE),

		SUMMONING_POTION(new int[] { 12140, 12142, 12144, 12146 }, Effects.SUMMONING_POT),

		SUMMONING_FLASK(new int[] { 23621, 23623, 23625, 23627, 23629, 23631 }, Effects.SUMMONING_POT),

		SANFEW_SERUM(new int[] { 10925, 10927, 10929, 10931 }, Effects.SANFEW_SERUM),

		PRAYER_RENEWAL(new int[] { 21630, 21632, 21634, 21636 }, Effects.PRAYER_RENEWAL),
		
		KARAMBWAN(new int[] { 3144 }, Effects.KARAMBWAN) {
			@Override
			public boolean isComboFood() {
				return true;
			}
		},

		BEER(new int[] { 1917, 1919 }, Effects.BEER),

		JUG(new int[] { 1993, 1935 }, Effects.WINE);

		private int[] id;
		private Effects effect;

		private Pot(int[] id, Effects effect) {
			this.id = id;
			this.effect = effect;
		}

		public boolean isFlask() {
			return getMaxDoses() == 6;
		}

		public boolean isPotion() {
			return getMaxDoses() == 4;
		}

		public int getMaxDoses() {
			return id.length;
		}

		public int getIdForDoses(int doses) {
			return id[getMaxDoses() - doses];
		}
		
		public boolean isComboFood() {
			return false;
		}
	}

	private enum Effects {
		ATTACK_POTION(Skills.ATTACK) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.1));
			}
		},
		FISHING_POTION(Skills.FISHING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (level + 3);
			}
		},
		ZAMORAK_BREW(Skills.ATTACK) {
			int toAdd;

			@Override
			public void extra(Player player) {
				toAdd = (player.getSkills().getLevelForXp(Skills.ATTACK));
				player.getSkills().set(Skills.ATTACK, toAdd);
			}

		},
		SANFEW_SERUM(Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.MAGIC, Skills.RANGE, Skills.AGILITY, Skills.COOKING, Skills.CRAFTING, Skills.FARMING, Skills.FIREMAKING, Skills.FISHING, Skills.FLETCHING, Skills.HERBLORE, Skills.MINING, Skills.RUNECRAFTING, Skills.SLAYER, Skills.SMITHING, Skills.THIEVING, Skills.WOODCUTTING, Skills.SUMMONING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int boost = (int) (realLevel * 0.33);
				if (actualLevel > realLevel)
					return actualLevel;
				if (actualLevel + boost > realLevel)
					return realLevel;
				return actualLevel + boost;
			}

			@Override
			public void extra(Player player) {
				player.getPrayer().restorePrayer((int) ((int) (player.getSkills().getLevelForXp(Skills.PRAYER) * 0.33 * 10) * player.getAuraManager().getPrayerPotsRestoreMultiplier()));
				player.addPoisonImmune(180000);
				// TODO DISEASE HEALING
			}

		},
		SUMMONING_POT(Skills.SUMMONING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int restore = (int) (Math.floor(player.getSkills().getLevelForXp(Skills.SUMMONING) * 0.25) + 7);
				if (actualLevel + restore > realLevel)
					return realLevel;
				return actualLevel + restore;
			}

			@Override
			public void extra(Player player) {
				Familiar familiar = player.getFamiliar();
				if (familiar != null)
					familiar.restoreSpecialAttack(15);
			}
		},
		ANTIPOISON() {
			@Override
			public void extra(Player player) {
				player.addPoisonImmune(86000);
				player.getPackets().sendGameMessage("You are now immune to poison.");
			}
		},
		SUPER_ANTIPOISON() {
			@Override
			public void extra(Player player) {
				player.addPoisonImmune(346000);
				player.getPackets().sendGameMessage("You are now immune to poison.");
			}
		},
		ENERGY_POTION() {
			@Override
			public void extra(Player player) {
				int restoredEnergy = player.getRunEnergy() + 20;
				player.setRunEnergy(restoredEnergy > 100 ? 100 : restoredEnergy);
			}
		},
		SUPER_ENERGY() {
			@Override
			public void extra(Player player) {
				int restoredEnergy = player.getRunEnergy() + 40;
				player.setRunEnergy(restoredEnergy > 100 ? 100 : restoredEnergy);
			}
		},
		ANTI_FIRE() {
			@Override
			public void extra(final Player player) {
				player.addFireImmune(360000);
				final long current = player.getFireImmune();
				player.getPackets().sendGameMessage("You are now immune to dragonfire.");
				WorldTasksManager.schedule(new WorldTask() {
					boolean stop = false;

					@Override
					public void run() {
						if (current != player.getFireImmune()) {
							stop();
							return;
						}
						if (!stop) {
							player.getPackets().sendGameMessage("<col=480000>Your antifire potion is about to run out...</col>");
							stop = true;
						} else {
							stop();
							player.getPackets().sendGameMessage("<col=480000>Your antifire potion has ran out...</col>");
						}
					}
				}, 500, 100);
			}
		},
		STRENGTH_POTION(Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.1));
			}
		},
		DEFENCE_POTION(Skills.DEFENCE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.1));
			}
		},
		RANGE_POTION(Skills.RANGE) {

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 5 + (realLevel * 0.1));
			}
		},
		MAGIC_POTION(Skills.MAGIC) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return level + 5;
			}
		},
		PRAYER_POTION() {
			@Override
			public void extra(Player player) {
				player.getPrayer().restorePrayer((int) ((int) (Math.floor(player.getSkills().getLevelForXp(Skills.PRAYER) * 2.5) + 70) * player.getAuraManager().getPrayerPotsRestoreMultiplier()));
			}
		},
		SUPER_STR_POTION(Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 5 + (realLevel * 0.15));
			}
		},
		SUPER_DEF_POTION(Skills.DEFENCE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 5 + (realLevel * 0.15));
			}
		},
		SUPER_ATT_POTION(Skills.ATTACK) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 5 + (realLevel * 0.15));
			}
		},
		EXTREME_STR_POTION(Skills.STRENGTH) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControllerManager().getController() instanceof Wilderness || FfaZone.isOverloadChanged(player)) {
					player.getPackets().sendGameMessage("You cannot drink this potion here.");
					return false;
				}
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 5 + (realLevel * 0.22));
			}
		},
		EXTREME_DEF_POTION(Skills.DEFENCE) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControllerManager().getController() instanceof Wilderness || FfaZone.isOverloadChanged(player)) {
					player.getPackets().sendGameMessage("You cannot drink this potion here.");
					return false;
				}
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 5 + (realLevel * 0.22));
			}
		},
		EXTREME_ATT_POTION(Skills.ATTACK) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControllerManager().getController() instanceof Wilderness || FfaZone.isOverloadChanged(player)) {
					player.getPackets().sendGameMessage("You cannot drink this potion here.");
					return false;
				}
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 5 + (realLevel * 0.22));
			}
		},
		EXTREME_RAN_POTION(Skills.RANGE) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControllerManager().getController() instanceof Wilderness || FfaZone.isOverloadChanged(player)) {
					player.getPackets().sendGameMessage("You cannot drink this potion here.");
					return false;
				}
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 4 + (Math.floor(realLevel / 5.2)));
			}
		},
		EXTREME_MAG_POTION(Skills.MAGIC) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControllerManager().getController() instanceof Wilderness || FfaZone.isOverloadChanged(player)) {
					player.getPackets().sendGameMessage("You cannot drink this potion here.");
					return false;
				}
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return level + 7;
			}
		},
		RECOVER_SPECIAL() {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControllerManager().getController() instanceof Wilderness || FfaZone.isOverloadChanged(player)) {
					player.getPackets().sendGameMessage("You cannot drink this potion here.");
					return false;
				}
				Long time = (Long) player.getTemporaryAttributtes().get("Recover_Special_Pot");
				if (time != null && Utils.currentTimeMillis() - time < 30000) {
					player.getPackets().sendGameMessage("You may only use this pot every 30 seconds.");
					return false;
				}
				return true;
			}

			@Override
			public void extra(Player player) {
				player.getTemporaryAttributtes().put("Recover_Special_Pot", Utils.currentTimeMillis());
				player.getCombatDefinitions().restoreSpecialAttack(25);
			}
		},
		SARADOMIN_BREW("You drink some of the foul liquid.", Skills.ATTACK, Skills.DEFENCE, Skills.STRENGTH, Skills.MAGIC, Skills.RANGE) {

			@Override
			public boolean canDrink(Player player) {
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.DEFENCE) {
					int boost = (int) (realLevel * 0.25);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel * 0.90);
				}
			}

			@Override
			public void extra(Player player) {
				int hitpointsModification = (int) (player.getMaxHitpoints() * 0.15);
				player.heal(hitpointsModification + 20, hitpointsModification);
			}
		},

		OVERLOAD() {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControllerManager().getController() instanceof Wilderness || FfaZone.isOverloadChanged(player)) {
					player.getPackets().sendGameMessage("You cannot drink this potion here.");
					return false;
				}
				/*
				 * if (player.getOverloadDelay() > 0) {
				 * player.getPackets().sendGameMessage(
				 * "You may only use this potion every five minutes."); return
				 * false; }
				 */
				if (player.getHitpoints() <= 500 || player.getOverloadDelay() > 480) {
					player.getPackets().sendGameMessage("You need more than 500 life points to survive the power of overload.");
					return false;
				}
				return true;
			}

			@Override
			public void extra(final Player player) {
				player.setOverloadDelay(501);
				WorldTasksManager.schedule(new WorldTask() {
					int count = 4;

					@Override
					public void run() {
						if (count == 0)
							stop();
						player.setNextAnimation(new Animation(3170));
						player.setNextGraphics(new Graphics(560));
						player.applyHit(new Hit(player, 100, HitLook.REGULAR_DAMAGE, 0));
						count--;
					}
				}, 0, 2);
			}
		},
		SUPER_PRAYER() {
			@Override
			public void extra(Player player) {
				player.getPrayer().setPrayerpoints((int) ((int) (70 + (player.getSkills().getLevelForXp(Skills.PRAYER) * 3.43)) * player.getAuraManager().getPrayerPotsRestoreMultiplier()));
			}
		},
		PRAYER_RENEWAL() {
			@Override
			public void extra(Player player) {
				player.setPrayerRenewalDelay(501);
			}
		},
		KARAMBWAN() {
			@Override
			public void extra(Player player) {
				player.heal(180);
				player.sendMessage("You eat the cooked karambwan.");
			}
		},
		SUPER_RESTORE(Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.MAGIC, Skills.RANGE, Skills.AGILITY, Skills.COOKING, Skills.CRAFTING, Skills.FARMING, Skills.FIREMAKING, Skills.FISHING, Skills.FLETCHING, Skills.HERBLORE, Skills.MINING, Skills.RUNECRAFTING, Skills.SLAYER, Skills.SMITHING, Skills.THIEVING, Skills.WOODCUTTING, Skills.SUMMONING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int boost = (int) (realLevel * 0.33);
				if (actualLevel > realLevel)
					return actualLevel;
				if (actualLevel + boost > realLevel)
					return realLevel;
				return actualLevel + boost;
			}

			@Override
			public void extra(Player player) {
				player.getPrayer().restorePrayer((int) ((int) (player.getSkills().getLevelForXp(Skills.PRAYER) * 0.33 * 10) * player.getAuraManager().getPrayerPotsRestoreMultiplier()));
			}

		},
		RESTORE_POTION(Skills.ATTACK, Skills.STRENGTH, Skills.MAGIC, Skills.RANGE, Skills.PRAYER) {

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int boost = (int) (realLevel * 0.33);
				if (actualLevel > realLevel)
					return actualLevel;
				if (actualLevel + boost > realLevel)
					return realLevel;
				return actualLevel + boost;
			}
		},
		BEER(Skills.ATTACK, Skills.STRENGTH) {

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.STRENGTH) {
					int boost = (int) (realLevel * 0.07);
					if (actualLevel > realLevel)
						return actualLevel;
					if (actualLevel + boost > realLevel)
						return realLevel;
					return actualLevel + boost;
				} else {
					return (int) (actualLevel * 0.90);
				}
			}

			@Override
			public void extra(Player player) {
				player.getPackets().sendGameMessage("You drink the beer. You feel slightly reinvigorated...");
				player.getPackets().sendGameMessage("...and slightly dizzy too.");
			}
		},
		WINE(Skills.ATTACK) {
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				return (int) (actualLevel * 0.90);
			}

			@Override
			public void extra(Player player) {
				player.getPackets().sendGameMessage("You drink the wine. You feel slightly reinvigorated...");
				player.getPackets().sendGameMessage("...and slightly dizzy too.");
				player.heal(70);
			}
		};

		private int[] affectedSkills;
		private String drinkMessage;

		private Effects(int... affectedSkills) {
			this(null, affectedSkills);
		}

		private Effects(String drinkMessage, int... affectedSkills) {
			this.drinkMessage = drinkMessage;
			this.affectedSkills = affectedSkills;
		}

		public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
			return actualLevel;
		}

		public boolean canDrink(Player player) {
			// usualy unused
			return true;
		}

		public void extra(Player player) {
			// usualy unused
		}
	}

	public static Pot getPot(int id) {
		for (Pot pot : Pot.values())
			for (int potionId : pot.id) {
				if (id == potionId)
					return pot;
			}
		return null;
	}

	public static int getDoses(Pot pot, Item item) {
		for (int i = pot.id.length - 1; i >= 0; i--) {
			if (pot.id[i] == item.getId())
				return pot.id.length - i;
		}
		return 0;
	}

	public static boolean mixPot(Player player, Item fromItem, Item toItem, int fromSlot, int toSlot) {
		if (fromItem.getId() == VIAL || toItem.getId() == VIAL) {
			Pot pot = getPot(fromItem.getId() == VIAL ? toItem.getId() : fromItem.getId());
			if (pot == null || pot.isFlask())
				return false;
			int doses = getDoses(pot, fromItem.getId() == VIAL ? toItem : fromItem);
			if (doses == 1) {
				player.getInventory().switchItem(fromSlot, toSlot);
				player.getPackets().sendGameMessage("You pour from one container into the other.", true);
				return true;
			}
			int vialDoses = doses / 2;
			doses -= vialDoses;
			player.getInventory().getItems().set(fromItem.getId() == VIAL ? toSlot : fromSlot, new Item(pot.getIdForDoses(doses), 1));
			player.getInventory().getItems().set(fromItem.getId() == VIAL ? fromSlot : toSlot, new Item(pot.getIdForDoses(vialDoses), 1));
			player.getInventory().refresh(fromSlot);
			player.getInventory().refresh(toSlot);
			player.getPackets().sendGameMessage("You split the potion between the two vials.", true);
			return true;
		}
		Pot pot = getPot(fromItem.getId());
		if (pot == null)
			return false;
		int doses2 = getDoses(pot, toItem);
		if (doses2 == 0 || doses2 == pot.getMaxDoses()) // not same pot type or
			// full already
			return false;
		int doses1 = getDoses(pot, fromItem);
		doses2 += doses1;
		doses1 = doses2 > pot.getMaxDoses() ? doses2 - pot.getMaxDoses() : 0;
		doses2 -= doses1;
		if (doses1 == 0 && pot.isFlask())
			player.getInventory().deleteItem(fromSlot, fromItem);
		else {
			player.getInventory().getItems().set(fromSlot, new Item(doses1 > 0 ? pot.getIdForDoses(doses1) : VIAL, 1));
			player.getInventory().refresh(fromSlot);
		}
		player.getInventory().getItems().set(toSlot, new Item(pot.getIdForDoses(doses2), 1));
		player.getInventory().refresh(toSlot);
		player.getPackets().sendGameMessage("You pour from one container into the other" + (pot.isFlask() && doses1 == 0 ? " and the glass shatters to pieces." : "."));
		return true;
	}

	public static boolean emptyPot(Player player, Item item, int slot) {
		Pot pot = getPot(item.getId());
		if (pot == null || pot.isFlask())
			return false;
		item.setId(VIAL);
		player.getInventory().refresh(slot);
		player.getPackets().sendGameMessage("You empty the vial.", true);
		return true;
	}

	public static boolean pot(Player player, Item item, int slot) {
		Pot pot = getPot(item.getId());
		if (pot == null)
			return false;
		if (player.getPotDelay() > Utils.currentTimeMillis())
			return true;
		if (!player.getControllerManager().canPot(pot))
			return true;
		if (!pot.effect.canDrink(player))
			return true;
		player.addPotDelay(1075);
		pot.effect.extra(player);
		int dosesLeft = getDoses(pot, item) - 1;
		if ((dosesLeft == 0 && pot.isFlask()) || pot.isComboFood())
			player.getInventory().deleteItem(slot, item);
		else {
			player.getInventory().getItems().set(slot, new Item(dosesLeft > 0 ? pot.getIdForDoses(dosesLeft) : pot.isPotion() ? VIAL : getReplacedId(pot), 1));
			player.getInventory().refresh(slot);
		}
		for (int skillId : pot.effect.affectedSkills)
			player.getSkills().set(skillId, pot.effect.getAffectedSkill(player, skillId, player.getSkills().getLevel(skillId), player.getSkills().getLevelForXp(skillId)));
		player.setNextAnimationNoPriority(new Animation(829));
		player.getPackets().sendSound(4580, 0, 1);
		if (pot.isFlask() || pot.isPotion()) {
			player.getPackets().sendGameMessage(pot.effect.drinkMessage != null ? pot.effect.drinkMessage : "You drink some of your " + item.getDefinitions().getName().toLowerCase().replace(" (1)", "").replace(" (2)", "").replace(" (3)", "").replace(" (4)", "").replace(" (5)", "").replace(" (6)", "") + ".", true);
			player.getPackets().sendGameMessage(dosesLeft == 0 ? "You have finished your " + (pot.isFlask() ? "flask and the glass shatters to pieces." : pot.isPotion() ? "potion." : item.getName().toLowerCase() + ".") : "You have " + dosesLeft + " dose of potion left.", true);
		}
		return true;
	}

	@SuppressWarnings("incomplete-switch")
	private static int getReplacedId(Pot pot) {
		switch (pot) {
		case JUG:
			return 1935;
		case BEER:
			return 1919;
		}
		return 0;
	}

	public static void resetOverLoadEffect(Player player) {
		if (!player.isDead()) {
			int actualLevel = player.getSkills().getLevel(Skills.ATTACK);
			int realLevel = player.getSkills().getLevelForXp(Skills.ATTACK);
			if (actualLevel > realLevel)
				player.getSkills().set(Skills.ATTACK, realLevel);
			actualLevel = player.getSkills().getLevel(Skills.STRENGTH);
			realLevel = player.getSkills().getLevelForXp(Skills.STRENGTH);
			if (actualLevel > realLevel)
				player.getSkills().set(Skills.STRENGTH, realLevel);
			actualLevel = player.getSkills().getLevel(Skills.DEFENCE);
			realLevel = player.getSkills().getLevelForXp(Skills.DEFENCE);
			if (actualLevel > realLevel)
				player.getSkills().set(Skills.DEFENCE, realLevel);
			actualLevel = player.getSkills().getLevel(Skills.MAGIC);
			realLevel = player.getSkills().getLevelForXp(Skills.MAGIC);
			if (actualLevel > realLevel)
				player.getSkills().set(Skills.MAGIC, realLevel);
			actualLevel = player.getSkills().getLevel(Skills.RANGE);
			realLevel = player.getSkills().getLevelForXp(Skills.RANGE);
			if (actualLevel > realLevel)
				player.getSkills().set(Skills.RANGE, realLevel);
			player.heal(500);
		}
		player.setOverloadDelay(0);
		player.getPackets().sendGameMessage("<col=480000>The effects of overload have worn off and you feel normal again.");
	}

	public static void applyOverLoadEffect(Player player) {
		if (player.getControllerManager().getController() instanceof Wilderness || FfaZone.isOverloadChanged(player)) {
			int actualLevel = player.getSkills().getLevel(Skills.ATTACK);
			int realLevel = player.getSkills().getLevelForXp(Skills.ATTACK);
			int level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.ATTACK, (int) (level + 5 + (realLevel * 0.15)));

			actualLevel = player.getSkills().getLevel(Skills.STRENGTH);
			realLevel = player.getSkills().getLevelForXp(Skills.STRENGTH);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.STRENGTH, (int) (level + 5 + (realLevel * 0.15)));

			actualLevel = player.getSkills().getLevel(Skills.DEFENCE);
			realLevel = player.getSkills().getLevelForXp(Skills.DEFENCE);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.DEFENCE, (int) (level + 5 + (realLevel * 0.15)));

			actualLevel = player.getSkills().getLevel(Skills.MAGIC);
			realLevel = player.getSkills().getLevelForXp(Skills.MAGIC);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.MAGIC, level + 5);

			actualLevel = player.getSkills().getLevel(Skills.RANGE);
			realLevel = player.getSkills().getLevelForXp(Skills.RANGE);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.RANGE, (int) (level + 5 + (realLevel * 0.1)));
		} else {
			int actualLevel = player.getSkills().getLevel(Skills.ATTACK);
			int realLevel = player.getSkills().getLevelForXp(Skills.ATTACK);
			int level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.ATTACK, (int) (level + 5 + (realLevel * 0.22)));

			actualLevel = player.getSkills().getLevel(Skills.STRENGTH);
			realLevel = player.getSkills().getLevelForXp(Skills.STRENGTH);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.STRENGTH, (int) (level + 5 + (realLevel * 0.22)));

			actualLevel = player.getSkills().getLevel(Skills.DEFENCE);
			realLevel = player.getSkills().getLevelForXp(Skills.DEFENCE);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.DEFENCE, (int) (level + 5 + (realLevel * 0.22)));

			actualLevel = player.getSkills().getLevel(Skills.MAGIC);
			realLevel = player.getSkills().getLevelForXp(Skills.MAGIC);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.MAGIC, level + 7);

			actualLevel = player.getSkills().getLevel(Skills.RANGE);
			realLevel = player.getSkills().getLevelForXp(Skills.RANGE);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.RANGE, (int) (level + 4 + (Math.floor(realLevel / 5.2))));
		}
	}

	private Pots() {

	}
}
