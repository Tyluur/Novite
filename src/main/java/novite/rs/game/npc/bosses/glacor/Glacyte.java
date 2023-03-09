package novite.rs.game.npc.bosses.glacor;

import novite.rs.game.Entity;
import novite.rs.game.Hit;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since May 3, 2014
 */
@SuppressWarnings("serial")
public class Glacyte extends NPC {

	public Glacyte(Glacor glacor, int id, GlacyteType type, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		setCapDamage(900);
		setSpawned(true);
		/** Setting final variables */
		this.glacor = glacor;
		this.type = type;
		if (type == null) {
			System.err.println("Glacor type is null!");
		}
		if (glacor.getCombat().getTarget() != null)
			getCombat().setTarget(glacor.getCombat().getTarget());
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		if (type != null) {
			type.handleIncomingHit(this, hit);
		}
		super.handleIngoingHit(hit);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (type != null) {
			type.processGlacyte(this);
		}
		if (getCombat().getTarget() == null && targetName != null) {
			Entity target = World.getPlayer(targetName);
			getCombat().setTarget(target);
		}
	}

	@Override
	public void drop() {
		if (glacor != null) {
			glacor.getGlacytes().remove(this);
			Player player = getMostDamageReceivedSourcePlayer();
			if (player != null) {
				int amount = glacor.getGlacytes().size();
				player.sendMessage("I have " + amount + " more glacyte" + (amount == 1 ? "" : "s") + " to kill...");
			}
		}
	}

	/**
	 * @return the targetName
	 */
	public String getTargetName() {
		return targetName;
	}

	/**
	 * Checking if the target name is correct with the player name
	 * 
	 * @param name
	 *            The name to check for
	 * @return
	 */
	public boolean correctName(String name) {
		if (targetName != null && name.equalsIgnoreCase(targetName))
			return true;
		return false;
	}

	/**
	 * @param targetName
	 *            the targetName to set
	 */
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	private String targetName;

	private final Glacor glacor;
	private final GlacyteType type;
}