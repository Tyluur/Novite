package novite.rs.game.player.dialogues.impl;

import java.util.ArrayList;
import java.util.List;

import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.utility.Utils;
import novite.rs.utility.Utils.CombatRates;
import novite.rs.utility.game.ChatColors;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 10, 2014
 */
public class ExperienceRateSelector extends Dialogue {

	@Override
	public void start() {
		sendNPCDialogue(2244, ChatAnimation.NORMAL, "To start your adventure, please select an experience rate.");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			List<String> rateList = new ArrayList<String>();
			rateList.add("Select an Option");
			boolean ignoreLoot = false;
			for (CombatRates rate : CombatRates.values()) {
				StringBuilder bldr = new StringBuilder();
				bldr.append(Utils.formatPlayerNameForDisplay(rate.name()));
				bldr.append("[x" + rate.getCombat() + " <col=" + ChatColors.BLUE + ">combat</col> ");
				bldr.append("x" + rate.getSkill() + " <col=" + ChatColors.BLUE + ">skill</col>");
				if (ignoreLoot) {
					bldr.append("]");
				} else {
					bldr.append(" " + rate.getLoot() + "% <col=" + ChatColors.BLUE + ">drop rates</col>]");
				}
				rateList.add(bldr.toString());
			}
			sendOptionsDialogue(rateList.toArray(new String[rateList.size()]));
			stage = 0;
			break;
		case 0:
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
			case FIFTH:
				rates = CombatRates.ELITE;
				break;
			}
			if (rates != null) {
				player.getFacade().setModifiers(rates);
				sendDialogue("You are now playing on <col=" + ChatColors.BLUE + ">" + rates.name().toLowerCase() + "</col> rates.", "Visit your <col=" + ChatColors.BLUE + ">information tab</col> if you forget your rates.");
				stage = -2;
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
