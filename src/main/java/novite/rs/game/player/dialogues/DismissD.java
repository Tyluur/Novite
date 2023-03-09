package novite.rs.game.player.dialogues;

public class DismissD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue(player.getPet() != null ? "Free pet?" : "Dismiss Familiar?", "Yes.", "No.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (getStage() == -1 && componentId == FIRST) {
			if (player.getFamiliar() != null) {
				player.getFamiliar().sendDeath(player);
			} else if (player.getPet() != null) {
				setStage(0);
				sendPlayerDialogue(ChatAnimation.NORMAL, "Run along; I'm setting you free.");
				return;
			}
		} else if (getStage() == 0 && player.getPet() != null) {
			player.getPetManager().setNpcId(-1);
			player.getPetManager().setItemId(-1);
			player.getPetManager().removeDetails(player.getPet().getItemId());
			player.getPet().switchOrb(false);
			player.getPackets().closeInterface(player.getInterfaceManager().hasResizableScreen() ? 98 : 212);
			player.getPackets().sendIComponentSettings(747, 17, 0, 0, 0);
			player.getPet().finish();
			player.setPet(null);
			player.getPackets().sendGameMessage("Your pet runs off until it's out of sight.");
		}
		end();
	}

	@Override
	public void finish() {

	}

}
