package novite.rs.game.player.dialogues;

public class DTClaimRewards extends Dialogue {

	@Override
	public void start() {
		sendDialogue(SEND_1_TEXT_INFO, "You have a Dominion Factor of " + player.getDominionTower().getDominionFactor() + ".");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (getStage() == -1) {
			setStage(0);
			sendDialogue(SEND_2_OPTIONS, "If you claim your rewards your progress will be reset.", "Claim Rewards", "Cancel");
		} else if (getStage() == 0) {
			if (componentId == 1) {
				player.getDominionTower().openRewardsChest();
			}
			end();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
