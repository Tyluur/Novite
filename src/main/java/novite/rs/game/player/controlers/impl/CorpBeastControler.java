package novite.rs.game.player.controlers.impl;

import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.player.controlers.Controller;

public class CorpBeastControler extends Controller {

	@Override
	public void start() {

	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 37929) {
			removeControler();
			player.stopAll();
			player.setNextWorldTile(new WorldTile(2970, 4384, 0));
			return false;
		}
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeControler();
	}

	@Override
	public boolean sendDeath() {
		removeControler();
		return true;
	}

	@Override
	public boolean login() {
		return false; // so doesnt remove script
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

}
