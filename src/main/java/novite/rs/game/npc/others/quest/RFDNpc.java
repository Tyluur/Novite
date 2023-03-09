package novite.rs.game.npc.others.quest;

import novite.rs.game.Animation;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.quests.QuestManager;
import novite.rs.game.player.quests.impl.Recipe_For_Disaster;

public class RFDNpc extends NPC {

	public RFDNpc(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		setSpawned(true);
		setNextAnimation(new Animation(-1));
	}

	@Override
	public void drop() {
		Player killer = getMostDamageReceivedSourcePlayer();
		if (killer != null) {
			killer.getFacade().setLastRFDWave(killer.getFacade().getLastRFDWave() + 1);
			int index = getArrayIndex();
			if (index != -1)
				((Recipe_For_Disaster) killer.getQuestManager().getProgressedQuest(QuestManager.getQuest(Recipe_For_Disaster.class).getName())).getKilledBosses()[index] = true;
		}
	}

	/**
	 * Get the index in the array for the npc id
	 * @return
	 */
	private int getArrayIndex() {
		switch (getId()) {
		case 3493:
			return Recipe_For_Disaster.AGRITH_NA_NA_INDEX;
		case 3494:
			return Recipe_For_Disaster.FLAMBEED_INDEX;
		case 3495:
			return Recipe_For_Disaster.KARAMEL_INDEX;
		case 3496:
			return Recipe_For_Disaster.DESSOURT_INDEX;
		}
		return -1;
	}
	
	private static final long serialVersionUID = -7664850315199508535L;

}
