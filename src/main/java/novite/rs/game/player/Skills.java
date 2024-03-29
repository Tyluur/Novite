package novite.rs.game.player;

import java.io.Serializable;

import novite.rs.Constants;
import novite.rs.game.item.Item;
import novite.rs.game.player.dialogues.LevelUp;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;

public final class Skills implements Serializable {

	private static final long serialVersionUID = -7086829989489745985L;

	public static final double MAXIMUM_EXP = 200000000;
	public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2, HITPOINTS = 3, RANGE = 4, PRAYER = 5, MAGIC = 6, COOKING = 7, WOODCUTTING = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13, MINING = 14, HERBLORE = 15, AGILITY = 16, THIEVING = 17, SLAYER = 18, FARMING = 19, RUNECRAFTING = 20, HUNTER = 21, CONSTRUCTION = 22, SUMMONING = 23, DUNGEONEERING = 24;

	public static final String[] SKILL_NAME = { "Attack", "Defence", "Strength", "Constitution", "Ranging", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecraft", "Hunter", "Construction", "Summoning", "Dungeoneering" };
	public static final byte[] XP_COUNTER_STAT_ORDER = { ATTACK, STRENGTH, DEFENCE, RANGE, MAGIC, HITPOINTS, PRAYER, AGILITY, HERBLORE, THIEVING, CRAFTING, FLETCHING, MINING, SMITHING, FISHING, COOKING, FIREMAKING, WOODCUTTING, RUNECRAFTING, SLAYER, FARMING, CONSTRUCTION, HUNTER, SUMMONING, DUNGEONEERING };

	public short level[];
	private double xp[];
	private double xpCounter;

	private transient Player player;

	public void passLevels(Player p) {
		this.level = p.getSkills().level;
		this.xp = p.getSkills().xp;
	}

	public Skills() {
		level = new short[25];
		xp = new double[25];
		for (int i = 0; i < level.length; i++) {
			level[i] = 1;
			xp[i] = 0;
		}
		level[3] = 10;
		xp[3] = 1184;
		level[HERBLORE] = 3;
		xp[HERBLORE] = 250;
	}

	public void restoreSkills() {
		for (int skill = 0; skill < level.length; skill++) {
			level[skill] = (short) getLevelForXp(skill);
			refresh(skill);
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getLevel(int skill) {
		return level[skill];
	}

	public double getXp(int skill) {
		return xp[skill];
	}

	public int getCombatLevel() {
		int attack = getLevelForXp(0);
		int defence = getLevelForXp(1);
		int strength = getLevelForXp(2);
		int hp = getLevelForXp(3);
		int prayer = getLevelForXp(5);
		int ranged = getLevelForXp(4);
		int magic = getLevelForXp(6);
		int combatLevel = 3;
		combatLevel = (int) ((defence + hp + Math.floor(prayer / 2)) * 0.25) + 1;
		double melee = (attack + strength) * 0.325;
		double ranger = Math.floor(ranged * 1.5) * 0.325;
		double mage = Math.floor(magic * 1.5) * 0.325;
		if (melee >= ranger && melee >= mage) {
			combatLevel += melee;
		} else if (ranger >= melee && ranger >= mage) {
			combatLevel += ranger;
		} else if (mage >= melee && mage >= ranger) {
			combatLevel += mage;
		}
		return combatLevel;
	}

	public void set(int skill, int newLevel) {
		level[skill] = (short) newLevel;
		refresh(skill);
	}
	
	public int drainLevel(int skill, int drain) {
		int drainLeft = drain - level[skill];
		if (drainLeft < 0) {
			drainLeft = 0;
		}
		level[skill] -= drain;
		if (level[skill] < 0) {
			level[skill] = 0;
		}
		refresh(skill);
		return drainLeft;
	}

	public int getCombatLevelWithSummoning() {
		return getCombatLevel() + getSummoningCombatLevel();
	}

	public int getSummoningCombatLevel() {
		return getLevelForXp(Skills.SUMMONING) / 8;
	}

	public void drainSummoning(int amt) {
		int level = getLevel(Skills.SUMMONING);
		if (level == 0) {
			return;
		}
		set(Skills.SUMMONING, amt > level ? 0 : level - amt);
	}

	public static int getXPForLevel(int level) {
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if (lvl >= level) {
				return output;
			}
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	public int getLevelForXp(int skill) {
		double exp = xp[skill];
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= (skill == DUNGEONEERING ? 120 : 99); lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if ((output - 1) >= exp) {
				return lvl;
			}
		}
		return skill == DUNGEONEERING ? 120 : 99;
	}

	public void init() {
		for (int skill = 0; skill < level.length; skill++) {
			refresh(skill);
		}
		refreshXpCounter();
	}

	private void refreshXpCounter() {
		player.getPackets().sendConfig(1801, (int) (xpCounter * 10));
	}

	public void resetXpCounter() {
		xpCounter = 0;
		refreshXpCounter();
	}

	public void refresh(int skill) {
		player.getPackets().sendSkillLevel(skill);
	}

	/**
	 * Adds experience to the given skill and does not take into calculation any
	 * modifiers, and does not update the trackers
	 *
	 * @param skill
	 *            The skill to add experience to
	 * @param exp
	 *            The experience to add to the skill
	 */
	public void addExpNoModifier(int skill, double exp) {
		int oldLevel = getLevelForXp(skill);
		this.xp[skill] += exp;
		level[skill] = (short) getLevelForXp(skill);
		int newLevel = getLevelForXp(skill);
		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			player.getDialogueManager().startDialogue(LevelUp.class, skill);
			if (skill == SUMMONING || (skill >= ATTACK && skill <= MAGIC)) {
				player.getAppearence().generateAppearenceData();
				if (skill == HITPOINTS) {
					player.heal(levelDiff * 10);
				} else if (skill == PRAYER) {
					player.getPrayer().restorePrayer(levelDiff * 10);
				}
			}
		}
		refresh(skill);
	}

	public void addXp(int skill, double exp, boolean... ignoreParam) {
		if (player.getFacade().isExpLocked()) {
			if (player.getAttributes().get("gave_lock_notif") == null) {
				player.sendMessage("<col=" + ChatColors.MAROON + ">Your experience is locked so you will not receive any experience.");
				player.getAttributes().put("gave_lock_notif", true);
			}
			return;
		}
		player.getControllerManager().trackXP(skill, (int) exp);
		boolean ignore = false;
		if (ignoreParam.length > 0 && ignoreParam[0] == true) {
			ignore = true;
		}
		if (!ignore && player.getFacade().getModifiers() != null) {
			switch (skill) {
			case ATTACK:
			case STRENGTH:
			case DEFENCE:
			case MAGIC:
			case RANGE:
			case HITPOINTS:
				exp *= player.getFacade().getModifiers()[Facade.COMBAT_MODIFIER_INDEX];
				break;
			default:
				exp *= player.getFacade().getModifiers()[Facade.SKILL_MODIFIER_INDEX];
				break;
			}
			if (Constants.isDoubleExp) {
				exp *= 2;
			}
		}
		if (player.getAuraManager().usingWisdom())
		    exp *= 1.125;
		int oldLevel = getLevelForXp(skill);
		xp[skill] += exp;
		xpCounter += exp;
		refreshXpCounter();
		if (xp[skill] > MAXIMUM_EXP) {
			xp[skill] = MAXIMUM_EXP;
		}
		int newLevel = getLevelForXp(skill);
		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDiff;
			player.getDialogueManager().startDialogue(LevelUp.class, skill);
			if (skill == HITPOINTS) {
				player.heal(levelDiff * 10);
			}
			if (skill == PRAYER) {
				player.getPrayer().restorePrayer(levelDiff * 10);
			}
			if (skill == SUMMONING || (skill >= ATTACK && skill <= MAGIC)) {
				player.getAppearence().generateAppearenceData();
			}
		}
		refresh(skill);
	}

	public boolean isMaxed() {
		int maxlevels = 0;
		for (int ji = 0; ji < level.length; ji++) {
			if (this.getLevel(ji) != 99) {
				continue;
			}
			maxlevels++;
		}
		if (maxlevels >= 23) {
			return true;
		}
		return false;
	}

	public void addSkillXpRefresh(int skill, double xp) {
		this.xp[skill] += xp;
		level[skill] = (short) getLevelForXp(skill);
	}

	public void resetSkillNoRefresh(int skill) {
		xp[skill] = 0;
		level[skill] = 1;
	}

	public void setXp(int skill, double exp) {
		xp[skill] = exp;
		refresh(skill);
	}
	
	public void setStat(int skill, int newLevel) {
		level[skill] = (short) newLevel;
		xp[skill] = getXPForLevel(newLevel);
	}

	public int getTotalLevel() {
		int level = 0;
		for (int i = 0; i < 25; i++) {
			level += getLevel(i);
		}
		return level;
	}

	public int getTotalExp() {
		int exp = 0;
		for (double element : xp) {
			exp += element;
		}
		return exp;
	}

	/**
	 * Gets the skillcape to give the player based on the skill
	 * 
	 * @param skill
	 *            The skill
	 * @return
	 */
	public Item[] getSkillCape(int skill) {
		boolean shouldReceiveTrimmed = player.isDonator();
		String capeName = SKILL_NAME[skill];
		if (skill == CONSTRUCTION)
			capeName = "Construct.";
		if (shouldReceiveTrimmed) {
			switch (skill) {
			case WOODCUTTING:
				capeName = "Woodcut.";
				break;
			}
		}
		Item[] rewards = new Item[2];
		if (shouldReceiveTrimmed) {
			for (Item[] element : Utils.TRIMMED_CAPES) {
				String name = element[0].getDefinitions().getName().toLowerCase();
				if (name.split(" ")[0].contains(capeName.toLowerCase())) {
					for (int j = 0; j < rewards.length; j++) {
						rewards[j] = element[j];
					}
				}
			}
		} else {
			for (Item[] element : Utils.UNTRIMMED_CAPES) {
				String name = element[0].getDefinitions().getName().toLowerCase();
				if (name.split(" ")[0].contains(capeName.toLowerCase())) {
					for (int j = 0; j < rewards.length; j++) {
						rewards[j] = element[j];
					}
				}
			}
		}
		return rewards;
	}
}
