package novite.rs.game.player.dialogues.impl;

import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 29, 2014
 */
public class Book_Switcher extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select an Option", "Switch Prayer Book", "Switch Magic SpellBook");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			switch (option) {
			case FIRST:
				player.getPrayer().setPrayerBook(!player.getPrayer().isAncientCurses());
				end();
				break;
			case SECOND:
				sendOptionsDialogue("Select an Option", "Normal Spellbook", "Ancient Spellbook", "Lunar Spellbook", "Cancel");
				stage = 1;
				break;
			}
			break;
		case 1:
			switch (option) {
			case FIRST:
				player.getCombatDefinitions().setSpellBook(0);
				break;
			case SECOND:
				player.getCombatDefinitions().setSpellBook(1);
				break;
			case THIRD:
				player.getCombatDefinitions().setSpellBook(2);
				break;
			}
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}

	int npcId;

}
