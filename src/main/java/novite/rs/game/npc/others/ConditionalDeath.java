package novite.rs.game.npc.others;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;

public class ConditionalDeath extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = -4848598636987815532L;
	private int requiredItem;
	private String deathMessage;
	private boolean remove;

	public ConditionalDeath(int requiredItem, String deathMessage, boolean remove, int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		this.requiredItem = requiredItem;
		this.deathMessage = deathMessage;
		this.remove = remove;
	}

	public int getRequiredItem() {
		return requiredItem;
	}

	private boolean removeItem(Player player) {
		if (this.getHitpoints() < (getMaxHitpoints() * 0.1) && (player.getEquipment().getWeaponId() == requiredItem || player.getInventory().containsItem(requiredItem, 1))) {
			if (remove) {
				player.getInventory().deleteItem(requiredItem, 1);
			}
			return true;
		}
		return false;
	}

	public boolean useHammer(Player player) {
		if (removeItem(player)) {
			if (deathMessage != null) {
				player.getPackets().sendGameMessage(deathMessage);
			}
			//missing emote
			if (getId() == 14849) {
				player.setNextAnimation(new Animation(15845));
			}
			setHitpoints(0);
			super.sendDeath(player);
			return true;
		}
		return false;
	}

	@Override
	public void sendDeath(Entity source) {
		if (source instanceof Player) {
			Player player = (Player) source;
			if ((player.getEquipment().getWeaponId() == requiredItem || player.getEquipment().getGlovesId() == requiredItem) && useHammer(player)) {
				return;
			}
			player.getPackets().sendGameMessage("The " + getName() + " is on its last legs! Finish it quickly!");
		}
		setHitpoints(1);
	}
}
