package novite.rs.game.player.dialogues.impl;

import novite.rs.game.player.Player;
import novite.rs.game.player.clans.ClansManager;
import novite.rs.game.player.dialogues.Dialogue;

public class ClanInvite extends Dialogue {

	Player inviter;

	@Override
	public void start() {
		inviter = (Player) parameters[0];
		ClansManager manager = inviter.getClanManager();
		if (manager == null || player.getClanManager() != null) {
			end();
			return;
		}
		if (player.getInterfaceManager().containsScreenInterface() || player.getControllerManager().getController() != null) {
			end();
			return;
		}
		player.getPackets().sendClanSettings(manager, false);
		player.getInterfaceManager().sendInterface(1095);
		player.getPackets().sendIComponentText(1095, 2, "You have been invited to join " + manager.getClan().getClanName() + " by " + inviter.getDisplayName() + ".");
		if (manager.getClan().getMottifTop() != 0) {
			player.getPackets().sendIComponentModel(1095, 44, ClansManager.getMottifSprite(manager.getClan().getMottifTop()));
		}
		if (manager.getClan().getMottifBottom() != 0) {
			player.getPackets().sendIComponentModel(1095, 54, ClansManager.getMottifSprite(manager.getClan().getMottifBottom()));
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == 36) {
			ClansManager.joinClan(player, inviter);
		}
		end();

	}

	@Override
	public void finish() {

	}

}
