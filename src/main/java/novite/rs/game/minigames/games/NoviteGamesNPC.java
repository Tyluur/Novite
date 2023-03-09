package novite.rs.game.minigames.games;

import novite.rs.game.Entity;
import novite.rs.game.Hit;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;

/**
 * @author Tyluur<itstyluur@gmail.com>
 * @since Aug 19, 2014
 */
public class NoviteGamesNPC extends NPC {

	public NoviteGamesNPC(int id, WorldTile tile) {
		super(id, tile, -1, true);
		setForceAgressive(true);
		setSpawned(true);
		setForceMultiArea(true);
		setStats();
	}

	private void setStats() {
		setBonuses(new int[13]);
		
		getCombatDefinitions().setMaxHit(200);
		for (int i = 0; i <= 5; i++) {
			getBonuses()[i] = 200;
		}
		for (int i = 5; i <= 10; i++) {
			getBonuses()[i] = 50;
		}
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		super.handleIngoingHit(hit);
		Entity source = hit.getSource();
		if (source.isPlayer()) {
			Player p = source.player();
			if (p.getControllerManager().getController() instanceof GamesHandler) {
				GamesHandler handler = (GamesHandler) p.getControllerManager().getController();
				handler.setDamageDealt(handler.getDamageDealt() + hit.getDamage());
			}
		}
	}
	
	@Override
	public void drop() {
		MainGameHandler.get().removeMonster(this);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4691087739451782456L;

}
