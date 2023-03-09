package novite.rs.game.player.content.agility;

import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;

public class Agility {

	public static boolean hasLevel(Player player, int level) {
		if (player.getSkills().getLevel(Skills.AGILITY) < level) {
			player.getPackets().sendGameMessage("You need an agility level of " + level + " to use this obstacle.", true);
			return false;
		}
		return true;
	}

}
