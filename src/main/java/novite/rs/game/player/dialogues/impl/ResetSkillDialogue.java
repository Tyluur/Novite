package novite.rs.game.player.dialogues.impl;

import novite.rs.game.player.Skills;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.utility.Utils;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class ResetSkillDialogue extends Dialogue {

	@Override
	public void start() {
		skill = (Integer) parameters[0];
		sendDialogue("Are you sure you want to reset " + Skills.SKILL_NAME[skill] + "?", "", "It will cost you " + Utils.format(cost) + " coins and this change is irreversible.");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Select an Option", "Reset my " + Skills.SKILL_NAME[skill] + " level to " + (skill == Skills.HITPOINTS ? "10" : "1") + ".", "Exit");
			stage = 0;
			break;
		case 0:
			switch (option) {
			case FIRST:
				if (player.getEquipment().wearingArmour()) {
					sendDialogue("Take off your armour before you do this.");
					setStage(-2);
					return;
				}
				if (player.takeMoney(cost)) {
					player.getSkills().set(skill, skill == Skills.HITPOINTS ? 10 : 1);
					player.getSkills().setXp(skill, skill == Skills.HITPOINTS ? Skills.getXPForLevel(10) : Skills.getXPForLevel(1));
					sendDialogue("Your " + Skills.SKILL_NAME[skill] + " level as been reset to " + player.getSkills().getLevel(skill) + ".");
				} else {
					sendDialogue("You do not have 500,000 coins on you", "that can be exchanged to reset a skill.");
				}
				stage = -2;
				break;
			case SECOND:
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {

	}

	int skill;
	private static final int cost = 500000;

}
