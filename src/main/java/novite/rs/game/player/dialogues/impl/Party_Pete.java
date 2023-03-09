package novite.rs.game.player.dialogues.impl;

import novite.rs.api.event.listeners.interfaces.DonationShopListener;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.utility.game.ChatColors;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ShopsLoader;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 4, 2014
 */
public class Party_Pete extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select an Option", "Purchase <col=" + ChatColors.BLUE + ">Vote</col> Rewards", "Purchase <col=" + ChatColors.BLUE + ">GoldPoint</col> Rewards", "Purchase <col=" + ChatColors.BLUE + ">Achievement</col> Rewards", "Purchase <col=" + ChatColors.BLUE + ">Loyalty</col> Rewards");
	}

	@Override
	public void run(int interfaceId, int option) {
		end();
		switch (option) {
		case FIRST:
			JsonHandler.<ShopsLoader> getJsonLoader(ShopsLoader.class).openShop(player, "Vote Exchange");
			break;
		case SECOND:
			DonationShopListener.display(player);
			//JsonHandler.<ShopsLoader> getJsonLoader(ShopsLoader.class).openShop(player, "Gold Points Rewards");
			break;
		case THIRD:
			JsonHandler.<ShopsLoader> getJsonLoader(ShopsLoader.class).openShop(player, "Achievement Rewards");
			break;
		case FOURTH:
			player.getLoyaltyManager().displayStore();
			break;
		}
	}

	@Override
	public void finish() {

	}

}
