package novite.rs.game.npc.others.quest;

import novite.rs.game.Entity;
import novite.rs.game.ForceTalk;
import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.controlers.impl.quest.DesertTreasure;
import novite.rs.utility.game.TeleportLocations;

public class DesertTreasureNPC extends QuestNPC {

	public DesertTreasureNPC(String target, int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(target, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		this.setSpawned(true);
		this.setForceAgressive(true);
	}

	private static final long serialVersionUID = -2490461318196371202L;

	@Override
	public void sendDeath(Entity source) {
		if (getId() != 1974) {
			super.sendDeath(source);
			if (this.getMostDamageReceivedSourcePlayer() != null) {
				Player player = this.getMostDamageReceivedSourcePlayer();
				if (player.getControllerManager().getController() instanceof DesertTreasure) {
					if (!player.getFacade().getDesertTreasureKills().contains(getId())) {
						player.getFacade().getDesertTreasureKills().add(getId());
					}
					player.getControllerManager().forceStop();
					player.setNextWorldTile(TeleportLocations.QUESTING_DOME);
					int amtToKill = (4) - player.getFacade().getDesertTreasureKills().size();
					player.getDialogueManager().startDialogue("SimpleNPCMessage", 1971, "You have defeated " + getName().toLowerCase() + ".", "You have " + (amtToKill) + " more monster" + (amtToKill == 1 ? "" : "s") + " to kill.");
					this.finish();
					if (amtToKill == 0) {
						player.getQuestManager().finishQuest("Desert Treasure");
					}
				}
			}
		} else {
			transformInto(1975);
			setHitpoints(getMaxHitpoints());
			setNextForceTalk(new ForceTalk("I am Damis, invincible Lord of the Shadows!"));
		}
	}

	@Override
	public void finish() {
		if (hasFinished()) {
			return;
		}
		super.finish();
	}
	
}
