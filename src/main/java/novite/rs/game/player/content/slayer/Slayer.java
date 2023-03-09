package novite.rs.game.player.content.slayer;

import java.util.List;

import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.game.Entity;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.utility.Utils;

/**
 * 
 * @author Tyluur <itstyluur@gmail.com>
 * @since 2012-12-07
 */
public class Slayer {

	public static boolean giveTask(Player player, Type type) {
		List<String> names = Tasks.getTasksNames(type);
		SlayerTask task = null;
		int attempts = 1;
		while (task == null) {
			String name = names.get(Utils.random(names.size()));
			SlayerTask temp = new SlayerTask(type, name, Utils.random(type.getLowestAmount(), type.getHighestAmount()));
			if (canDamage(player, name)) {
				task = temp;
				break;
			}
			if (attempts++ > 30) {
				break;
			}
		}
		if (task != null) {
			player.setSlayerTask(task);
			return true;
		} else
			return false;
	}

	public static void giveTask(Player player, String name, Type type) {
		player.setSlayerTask(new SlayerTask(type, name, Utils.random(type.getLowestAmount(), type.getHighestAmount())));
	}

	public static boolean hasNosepeg(Entity target) {
		if (!(target instanceof Player)) {
			return true;
		}
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 4168 || hasSlayerHelmet(target);
	}

	public static boolean hasEarmuffs(Entity target) {
		if (!(target instanceof Player)) {
			return true;
		}
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 4166 || hat == 13277 || hasSlayerHelmet(target);
	}

	public static boolean hasMask(Entity target) {
		if (!(target instanceof Player)) {
			return true;
		}
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 1506 || hat == 4164 || hat == 13277 || hasSlayerHelmet(target);
	}

	public static boolean hasWitchWoodIcon(Entity target) {
		if (!(target instanceof Player)) {
			return true;
		}
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getAmuletId();
		return hat == 8923;
	}

	public static boolean hasSlayerHelmet(Entity target) {
		if (!(target instanceof Player)) {
			return true;
		}
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 13263 || hat == 14636 || hat == 14637 || hasFullSlayerHelmet(target);
	}

	public static boolean hasFullSlayerHelmet(Entity target) {
		if (!(target instanceof Player)) {
			return true;
		}
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 15492 || hat == 15496 || hat == 15497 || (hat >= 22528 && hat <= 22550);
	}

	public static boolean hasReflectiveEquipment(Entity target) {
		if (!(target instanceof Player)) {
			return true;
		}
		Player targetPlayer = (Player) target;
		int shieldId = targetPlayer.getEquipment().getShieldId();
		return shieldId == 4156;
	}

	public static boolean hasSpinyHelmet(Entity target) {
		if (!(target instanceof Player)) {
			return true;
		}
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 4551 || hasSlayerHelmet(target);
	}

	public static boolean canDamage(Player player, Object param) {
		int id = -1;
		String name = "";
		if (param instanceof String) {
			id = SlayerMonsters.forName((String) param);
			name = (String) param;
		} else {
			id = param instanceof NPC ? ((NPC) param).getId() : (Integer) param;
			name = NPCDefinitions.getNPCDefinitions(id).getName();
		}
		if (id == -1)
			return true;
		SlayerMonsters monster = SlayerMonsters.forId(id);
		if (monster != null) {
			if (player.getSlayerTask() != null && player.getSlayerTask().getName() != null) {
				if (!player.getSlayerTask().getName().equalsIgnoreCase(name)) {
					if (player.getSkills().getLevel(Skills.SLAYER) < monster.getRequirement()) {
						player.sendMessage("You need a Slayer level of " + monster.getRequirement() + " to wound this monster.");
						return false;
					}
				}
			} else {
				if (player.getSkills().getLevel(Skills.SLAYER) < monster.getRequirement()) {
					player.sendMessage("You need a Slayer level of " + monster.getRequirement() + " to wound this monster.");
					return false;
				}
			}
		}
		return true;
	}

}