package novite.rs.game.npc.others.quest;

import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 29, 2014
 */
public class QuestNPC extends NPC {

	public QuestNPC(String target, int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		this.target = target;
	}
	
	@Override
	public void processNPC() {
		Player player = World.getPlayer(target);
		if (player == null || !withinDistance(player, 30)) {
			finish();
			return;
		}
		super.processNPC();
 	}
 	
	@Override
	public boolean canBeAttacked(Player player) {
		if (!player.getUsername().equals(target)) {
			player.sendMessage("You cannot attack this npc.");
			return false;
		}
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3900198370982938815L;

	private final String target;
}
