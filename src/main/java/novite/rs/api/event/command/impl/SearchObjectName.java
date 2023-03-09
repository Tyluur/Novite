package novite.rs.api.event.command.impl;

import java.util.Arrays;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.cache.loaders.ObjectDefinitions;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 21, 2014
 */
public class SearchObjectName extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "objn" };
	}

	@Override
	public void execute(Player player) {
		boolean searchingOptions = cmd.length >= 3;
		for (int i = 0; i < Utils.getObjectDefinitionsSize(); i++) {
			ObjectDefinitions definitions = ObjectDefinitions.getObjectDefinitions(i);
			if (definitions.name.toLowerCase().contains(cmd[1].replaceAll("_", " "))) {
				StringBuilder sbl = new StringBuilder();
				for (int j = 0; j < definitions.options.length; j++) {
					String option = definitions.options[j];
					if (option == null)
						continue;
					sbl.append(option + ",");
				}
				if (searchingOptions) {
					String[] options = sbl.toString().split(",");
					for (String option : options) {
						if (option.equalsIgnoreCase(cmd[2])) {
							player.getPackets().sendMessage(99, "[OBJECT] " + ObjectDefinitions.getObjectDefinitions(i).name + " - ID: " + i + " " + Arrays.toString(sbl.toString().split(",")), player);
						}
					}
				} else
					player.getPackets().sendMessage(99, "[OBJECT] " + ObjectDefinitions.getObjectDefinitions(i).name + " - ID: " + i + " " + Arrays.toString(sbl.toString().split(",")), player);
			}
		}
	}

}
