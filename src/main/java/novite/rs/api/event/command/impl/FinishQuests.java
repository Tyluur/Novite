package novite.rs.api.event.command.impl;

import java.util.Map.Entry;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.game.player.quests.Quest;
import novite.rs.game.player.quests.QuestManager;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 29, 2014
 */
public class FinishQuests extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "fq" };
	}

	@Override
	public void execute(Player player) {
		player.getQuestManager().getProgressed().clear();
		for (Entry<String, Quest<?>> quest : QuestManager.getQuests().entrySet()) {
			player.getQuestManager().startQuest(quest.getValue().getClass());
			quest.getValue().completeQuest(player);
		}
		player.getFacade().setLastRFDWave(4);
	}

}
