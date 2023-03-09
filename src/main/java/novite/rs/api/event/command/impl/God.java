package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Aug 6, 2013
 */
public class God extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "god" };
	}

	@Override
	public void execute(Player player) {
		player.setHitpoints(Short.MAX_VALUE);
		player.getEquipment().setEquipmentHpIncrease(Short.MAX_VALUE - 990);
		for (int i = 0; i < 10; i++) {
			player.getCombatDefinitions().getBonuses()[i] = 5000;
		}
		for (int i = 14; i < player.getCombatDefinitions().getBonuses().length; i++) {
			player.getCombatDefinitions().getBonuses()[i] = 5000;
		}
	}

}
