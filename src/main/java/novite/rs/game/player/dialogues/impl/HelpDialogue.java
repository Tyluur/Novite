package novite.rs.game.player.dialogues.impl;

import novite.rs.api.event.listeners.interfaces.HelpInterface;
import novite.rs.api.event.listeners.interfaces.SkillSelectionInterface;
import novite.rs.game.player.content.PlayerLook;
import novite.rs.game.player.content.TicketSystem;
import novite.rs.game.player.controlers.impl.StartTutorial;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.game.player.dialogues.SimpleMessage;
import novite.rs.utility.Utils.CombatRates;
import novite.rs.utility.game.ChatColors;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 20, 2014
 */
public class HelpDialogue extends Dialogue {

	int npcId = 2244;

	@Override
	public void start() {
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Hello, adventurer " + player.getDisplayName() + ". How may I help you?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Select an Option", "View F.A.Q", "Modify Appearance", "Request Staff Assistance", "Purchase a Skillcape");
			stage = 0;
			break;
		case 0:
			if (player.getControllerManager().getController() instanceof StartTutorial) {
				StartTutorial controler = (StartTutorial) player.getControllerManager().getController();
				if ((controler.getStage() == 6 && option != SECOND)) {
					sendDialogue("Please select the correct option.");
					stage = -1;
					return;
				}
			}
			switch (option) {
			case FIRST:
				HelpInterface.display(player);
				break;
			case SECOND:
				PlayerLook.openCharacterCustomizing(player);
				break;
			case THIRD:
				TicketSystem.requestTicket(player);
				break;
			case FOURTH:
				SkillSelectionInterface.display(player);
				player.getDialogueManager().startDialogue(SimpleMessage.class, "Select the skill in which you wish to buy a cape!");
				player.getTemporaryAttributtes().put("skill_selection_type", "CAPES");
				break;
			}
			end();
			break;
		case 1:
			CombatRates rates = null;
			switch (option) {
			case FIRST:
				rates = CombatRates.EASY;
				break;
			case SECOND:
				rates = CombatRates.NORMAL;
				break;
			case THIRD:
				rates = CombatRates.HARD;
				break;
			case FOURTH:
				rates = CombatRates.LEGEND;
				break;
			}
			if (rates != null) {
				player.getFacade().setModifiers(rates);
				if (player.getControllerManager().getController() instanceof StartTutorial) {
					end();
					StartTutorial controler = (StartTutorial) player.getControllerManager().getController();
					controler.setStage(controler.getStage() + 1);
					controler.sendTutorialDialogue(false);
				} else {
					sendDialogue("You are now playing on <col=" + ChatColors.BLUE + ">" + rates.name().toLowerCase() + "</col> rates.", "Visit your <col=" + ChatColors.BLUE + ">information tab</col> if you forget your rates.");
					stage = -2;
				}
			} else {
				end();
			}
			break;
		}
	}

	@Override
	public void finish() {

	}

}
