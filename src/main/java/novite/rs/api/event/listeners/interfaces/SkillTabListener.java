package novite.rs.api.event.listeners.interfaces;

import static novite.rs.game.player.Skills.AGILITY;
import static novite.rs.game.player.Skills.ATTACK;
import static novite.rs.game.player.Skills.CONSTRUCTION;
import static novite.rs.game.player.Skills.COOKING;
import static novite.rs.game.player.Skills.CRAFTING;
import static novite.rs.game.player.Skills.DEFENCE;
import static novite.rs.game.player.Skills.DUNGEONEERING;
import static novite.rs.game.player.Skills.FARMING;
import static novite.rs.game.player.Skills.FIREMAKING;
import static novite.rs.game.player.Skills.FISHING;
import static novite.rs.game.player.Skills.FLETCHING;
import static novite.rs.game.player.Skills.HERBLORE;
import static novite.rs.game.player.Skills.HITPOINTS;
import static novite.rs.game.player.Skills.HUNTER;
import static novite.rs.game.player.Skills.MAGIC;
import static novite.rs.game.player.Skills.MINING;
import static novite.rs.game.player.Skills.PRAYER;
import static novite.rs.game.player.Skills.RANGE;
import static novite.rs.game.player.Skills.RUNECRAFTING;
import static novite.rs.game.player.Skills.SLAYER;
import static novite.rs.game.player.Skills.SMITHING;
import static novite.rs.game.player.Skills.STRENGTH;
import static novite.rs.game.player.Skills.SUMMONING;
import static novite.rs.game.player.Skills.THIEVING;
import static novite.rs.game.player.Skills.WOODCUTTING;
import novite.rs.api.event.EventListener;
import novite.rs.game.ForceTalk;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.dialogues.LevelUp;
import novite.rs.game.player.dialogues.SimpleMessage;
import novite.rs.game.player.dialogues.impl.ResetSkillDialogue;
import novite.rs.networking.protocol.game.DefaultGameDecoder;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class SkillTabListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 320 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		int skillId = getSkillId(buttonId);
		switch (packetId) {
		case DefaultGameDecoder.ACTION_BUTTON1_PACKET:
			player.stopAll();
			int lvlupSkill = -1;
			int skillMenu = -1;
			switch (buttonId) {
			case 200: // Attack
				skillMenu = 1;
				if (player.getTemporaryAttributtes().remove("leveledUp[0]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 1);
				} else {
					lvlupSkill = 0;
					player.getPackets().sendConfig(1230, 10);
				}
				break;
			case 11: // Strength
				skillMenu = 2;
				if (player.getTemporaryAttributtes().remove("leveledUp[2]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 2);
				} else {
					lvlupSkill = 2;
					player.getPackets().sendConfig(1230, 20);
				}
				break;
			case 28: // Defence
				skillMenu = 5;
				if (player.getTemporaryAttributtes().remove("leveledUp[1]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 5);
				} else {
					lvlupSkill = 1;
					player.getPackets().sendConfig(1230, 40);
				}
				break;
			case 52: // Ranged
				skillMenu = 3;
				if (player.getTemporaryAttributtes().remove("leveledUp[4]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 3);
				} else {
					lvlupSkill = 4;
					player.getPackets().sendConfig(1230, 30);
				}
				break;
			case 76: // Prayer
				if (player.getTemporaryAttributtes().remove("leveledUp[5]") != Boolean.TRUE) {
					skillMenu = 7;
					player.getPackets().sendConfig(965, 7);
				} else {
					lvlupSkill = 5;
					player.getPackets().sendConfig(1230, 60);
				}
				break;
			case 93: // Magic
				if (player.getTemporaryAttributtes().remove("leveledUp[6]") != Boolean.TRUE) {
					skillMenu = 4;
					player.getPackets().sendConfig(965, 4);
				} else {
					lvlupSkill = 6;
					player.getPackets().sendConfig(1230, 33);
				}
				break;
			case 110: // Runecrafting
				if (player.getTemporaryAttributtes().remove("leveledUp[20]") != Boolean.TRUE) {
					skillMenu = 12;
					player.getPackets().sendConfig(965, 12);
				} else {
					lvlupSkill = 20;
					player.getPackets().sendConfig(1230, 100);
				}
				break;
			case 134: // Construction
				skillMenu = 22;
				if (player.getTemporaryAttributtes().remove("leveledUp[21]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 22);
				} else {
					lvlupSkill = 21;
					player.getPackets().sendConfig(1230, 698);
				}
				break;
			case 193: // Hitpoints
				skillMenu = 6;
				if (player.getTemporaryAttributtes().remove("leveledUp[3]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 6);
				} else {
					lvlupSkill = 3;
					player.getPackets().sendConfig(1230, 50);
				}
				break;
			case 19: // Agility
				skillMenu = 8;
				if (player.getTemporaryAttributtes().remove("leveledUp[16]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 8);
				} else {
					lvlupSkill = 16;
					player.getPackets().sendConfig(1230, 65);
				}
				break;
			case 36: // Herblore
				skillMenu = 9;
				if (player.getTemporaryAttributtes().remove("leveledUp[15]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 9);
				} else {
					lvlupSkill = 15;
					player.getPackets().sendConfig(1230, 75);
				}
				break;
			case 60: // Thieving
				skillMenu = 10;
				if (player.getTemporaryAttributtes().remove("leveledUp[17]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 10);
				} else {
					lvlupSkill = 17;
					player.getPackets().sendConfig(1230, 80);
				}
				break;
			case 84: // Crafting
				skillMenu = 11;
				if (player.getTemporaryAttributtes().remove("leveledUp[12]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 11);
				} else {
					lvlupSkill = 12;
					player.getPackets().sendConfig(1230, 90);
				}
				break;
			case 101: // Fletching
				skillMenu = 19;
				if (player.getTemporaryAttributtes().remove("leveledUp[9]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 19);
				} else {
					lvlupSkill = 9;
					player.getPackets().sendConfig(1230, 665);
				}
				break;
			case 118: // Slayer
				skillMenu = 20;
				if (player.getTemporaryAttributtes().remove("leveledUp[18]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 20);
				} else {
					lvlupSkill = 18;
					player.getPackets().sendConfig(1230, 673);
				}
				break;
			case 142: // Hunter
				skillMenu = 23;
				if (player.getTemporaryAttributtes().remove("leveledUp[22]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 23);
				} else {
					lvlupSkill = 22;
					player.getPackets().sendConfig(1230, 689);
				}
				break;
			case 186: // Mining
				skillMenu = 13;
				if (player.getTemporaryAttributtes().remove("leveledUp[14]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 13);
				} else {
					lvlupSkill = 14;
					player.getPackets().sendConfig(1230, 110);
				}
				break;
			case 179: // Smithing
				skillMenu = 14;
				if (player.getTemporaryAttributtes().remove("leveledUp[13]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 14);
				} else {
					lvlupSkill = 13;
					player.getPackets().sendConfig(1230, 115);
				}
				break;
			case 44: // Fishing
				skillMenu = 15;
				if (player.getTemporaryAttributtes().remove("leveledUp[10]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 15);
				} else {
					lvlupSkill = 10;
					player.getPackets().sendConfig(1230, 120);
				}
				break;
			case 68: // Cooking
				skillMenu = 16;
				if (player.getTemporaryAttributtes().remove("leveledUp[7]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 16);
				} else {
					lvlupSkill = 7;
					player.getPackets().sendConfig(1230, 641);
				}
				break;
			case 172: // Firemaking
				skillMenu = 17;
				if (player.getTemporaryAttributtes().remove("leveledUp[11]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 17);
				} else {
					lvlupSkill = 11;
					player.getPackets().sendConfig(1230, 649);
				}
				break;
			case 165: // Woodcutting
				skillMenu = 18;
				if (player.getTemporaryAttributtes().remove("leveledUp[8]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 18);
				} else {
					lvlupSkill = 8;
					player.getPackets().sendConfig(1230, 660);
				}
				break;
			case 126: // Farming
				skillMenu = 21;
				if (player.getTemporaryAttributtes().remove("leveledUp[19]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 21);
				} else {
					lvlupSkill = 19;
					player.getPackets().sendConfig(1230, 681);
				}
				break;
			case 150: // Summoning
				skillMenu = 24;
				if (player.getTemporaryAttributtes().remove("leveledUp[23]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 24);
				} else {
					lvlupSkill = 23;
					player.getPackets().sendConfig(1230, 705);
				}
				break;
			case 158: // Dung
				skillMenu = 25;
				if (player.getTemporaryAttributtes().remove("leveledUp[24]") != Boolean.TRUE) {
					player.getPackets().sendConfig(965, 25);
				} else {
					lvlupSkill = 24;
					player.getPackets().sendConfig(1230, 705);
				}
				break;
			}
			player.getInterfaceManager().sendInterface(lvlupSkill != -1 ? 741 : 499);
			if (lvlupSkill != -1) {
				LevelUp.switchFlash(player, lvlupSkill, false);
			}
			if (skillMenu != -1) {
				player.getTemporaryAttributtes().put("skillMenu", skillMenu);
			}
			break;
		case DefaultGameDecoder.ACTION_BUTTON2_PACKET:// say lvl
			player.setNextForceTalk(new ForceTalk("<col=0066CC>My " + Skills.SKILL_NAME[skillId] + " level is: " + player.getSkills().getLevelForXp(skillId) + ".</col>"));
			break;
		case DefaultGameDecoder.ACTION_BUTTON4_PACKET:// reset
			player.getDialogueManager().startDialogue(ResetSkillDialogue.class, skillId);
			return true;
		}
		return false;
	}

	private int getSkillId(int buttonId) {
		switch (buttonId) {
		case 200:
			return ATTACK;
		case 11:
			return STRENGTH;
		case 28:
			return DEFENCE;
		case 52:
			return RANGE;
		case 76:
			return PRAYER;
		case 93:
			return MAGIC;
		case 110:
			return RUNECRAFTING;
		case 134:
			return CONSTRUCTION;
		case 158:
			return DUNGEONEERING;
		case 193:
			return HITPOINTS;
		case 19:
			return AGILITY;
		case 36:
			return HERBLORE;
		case 60:
			return THIEVING;
		case 84:
			return CRAFTING;
		case 101:
			return FLETCHING;
		case 118:
			return SLAYER;
		case 142:
			return HUNTER;
		case 186:
			return MINING;
		case 179:
			return SMITHING;
		case 44:
			return FISHING;
		case 68:
			return COOKING;
		case 172:
			return FIREMAKING;
		case 165:
			return WOODCUTTING;
		case 126:
			return FARMING;
		case 150:
			return SUMMONING;
		default:
			System.out.println(buttonId);
			return 0;
		}
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
