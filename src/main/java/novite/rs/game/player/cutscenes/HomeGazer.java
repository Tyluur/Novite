package novite.rs.game.player.cutscenes;

import java.util.ArrayList;

import novite.rs.game.player.Player;
import novite.rs.game.player.cutscenes.actions.CutsceneAction;
import novite.rs.game.player.cutscenes.actions.LookCameraAction;
import novite.rs.game.player.cutscenes.actions.PosCameraAction;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 29, 2014
 */
public class HomeGazer extends Cutscene {

	@Override
	public boolean hiddenMinimap() {
		return false;
	}

	@Override
	public CutsceneAction[] getActions(Player player) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();

		actionsList.add(new PosCameraAction(30, 75, 5000, 0, 3, -1));
		actionsList.add(new LookCameraAction(30, -75, 1000, 0, 3, 6));

		/*	actionsList.add(new PosCameraAction(80, 75, 5000, 6, 6, -1));
			actionsList.add(new LookCameraAction(30, 75, 1000, 6, 6, 10));
			actionsList.add(new PosCameraAction(30, 75, 5000, 3, 3, 10));*/

		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}

}
