package novite.rs.api.event.command.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.WorldObject;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;
import novite.rs.utility.game.Rights;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 23, 2014
 */
public class FindNearbyObjects extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "parseobjects" };
	}

	@Override
	public void execute(final Player player) {
		List<WorldObject> closeObjects = new ArrayList<>(player.getRegion().getObjects());
		Collections.sort(closeObjects, new Comparator<WorldObject>() {

			@Override
			public int compare(WorldObject o1, WorldObject o2) {
				return Integer.compare(Utils.getDistance(player, o1), Utils.getDistance(player, o2));
			}
		});
		for (WorldObject object : closeObjects) {
			if (!object.withinDistance(player, 5))
				continue;
			System.out.println("[name=" + object.getDefinitions().name + ", id=" + object.getId() + ", type=" + object.getType() + ", rotation=" + object.getRotation() + ", " + object.getLocation() + "]");
		}
	}

}
