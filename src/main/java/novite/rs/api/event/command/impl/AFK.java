package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since May 4, 2014
 */
public class AFK extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.MODERATOR;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "afk" };
	}

	@Override
	public void execute(final Player player) {
		final int renderEmote = player.getAppearence().getRenderEmote();
		player.getAttributes().put("afking", true);
		player.getAppearence().setRenderEmote(500);
		player.setCloseInterfacesEvent(new Runnable() {
			
			@Override
			public void run() {
				player.getAppearence().setRenderEmote(renderEmote);
				player.getAttributes().remove("afking");
			}
		});
	}

}
