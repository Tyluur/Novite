package novite.rs.game.player.dialogues.impl;

import static novite.rs.game.player.Skills.ATTACK;
import static novite.rs.game.player.Skills.DEFENCE;
import static novite.rs.game.player.Skills.HITPOINTS;
import static novite.rs.game.player.Skills.MAGIC;
import static novite.rs.game.player.Skills.RANGE;
import static novite.rs.game.player.Skills.SKILL_NAME;
import static novite.rs.game.player.Skills.STRENGTH;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.utility.Utils;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 22, 2014
 */
public class CombatLampItem extends Dialogue {

	int skill;

	@Override
	public void start() {
		sendOptionsDialogue("Choose a Combat Skill", "Attack", "Strength", "Defence", "Hitpoints", "Next Page");
	}

	@Override
	public void run(int interfaceId, int option) {
		int skill = -1;
		switch (stage) {
			case -1:
				switch (option) {
					case FIRST:
						skill = ATTACK;
						break;
					case SECOND:
						skill = STRENGTH;
						break;
					case THIRD:
						skill = DEFENCE;
						break;
					case FOURTH:
						skill = HITPOINTS;
						break;
					case FIFTH:
						sendOptionsDialogue("Choose a Combat Skill", "Magic", "Ranged", "Back");
						stage = 0;
						break;
				}
				if (skill != -1) {
					addExperience(skill);
				}
				break;
			case 0:
				switch (option) {
					case FIRST:
						skill = MAGIC;
						break;
					case SECOND:
						skill = RANGE;
						break;
					case THIRD:
						player.getDialogueManager().startDialogue(CombatLampItem.class);
						break;
				}
				if (skill != -1) {
					addExperience(skill);
				}
		}
	}

	/**
	 * Adds experience to the skill
	 *
	 * @param skill
	 *            The skill to add experience to
	 */
	public void addExperience(int skill) {
		if (player.getInventory().contains(15390)) {
			player.getInventory().deleteItem(15390, 1);
			player.getSkills().addExpNoModifier(skill, EXP_AMOUNT);
			if (skill != HITPOINTS) {
				player.getSkills().addExpNoModifier(HITPOINTS, EXP_AMOUNT / 3);
			}
			sendDialogue("You receive " + Utils.format(EXP_AMOUNT) + " experience in " + SKILL_NAME[skill] + ".");
			stage = -2;
		}
	}

	@Override
	public void finish() {

	}

	/**
	 * The amount of experience to give
	 */
	private static final int EXP_AMOUNT = 300000;

}
