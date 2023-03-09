package novite.rs.game.npc.others.quest;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.quests.QuestManager;
import novite.rs.game.player.quests.impl.Lunar_Diplomacy;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 29, 2014
 */
public class LunarDiplomacyNPC extends QuestNPC {

	public LunarDiplomacyNPC(String target, int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(target, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		setSpawned(true);
	}
	
	@Override
	public void drop() {
		Player killer = getMostDamageReceivedSourcePlayer();
		if (killer != null) {
			killer.getQuestManager().finishQuest(QuestManager.getQuest(Lunar_Diplomacy.class).getName());
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8860295790986915718L;

}
