package novite.rs.game.player.content.cannon;

import java.util.ArrayList;
import java.util.List;

import novite.rs.game.Animation;
import novite.rs.game.World;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.OwnedObjectManager;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.content.slayer.SlayerMonsters;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Nov 30, 2013
 */
public class CannonAlgorithms {

	/**
	 * Handles the setup of the player cannon
	 *
	 * @param player
	 *            The player setting up the cannon
	 */
	public static void createCannon(Player player) {
		if (!World.canMoveNPC(player.getPlane(), player.getX(), player.getY(), 3)) {
			player.sendMessage("You need more space to set up your cannon.");
			return;
		}
		if (player.getInventory().containsItems(DwarfCannon.CANNON_ITEMS, new int[] { 1, 1, 1, 1 })) {
			player.lock(2);
			if (player.getDwarfCannon() != null) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You already had a dwarf cannon set up so the previous one has been banked.");
				player.getDwarfCannon().finish(true);
				return;
			}
			for (int part : DwarfCannon.CANNON_ITEMS) {
				player.getInventory().deleteItem(part, 1);
			}
			player.setNextAnimation(new Animation(827));
			player.setDwarfCannon(new DwarfCannon(player.getDisplayName(), new WorldObject(6, 10, 0, player)));
		} else {
			player.sendMessage("You do not have all of the pieces of the cannon to set it up!");
		}
	}

	/**
	 * Toggles the firing status of the cannon
	 *
	 * @param player
	 *            The player who clicked the cannon
	 * @param object
	 *            The cannon object
	 */
	public static void toggleFiring(Player player, WorldObject object) {
		Player owner = OwnedObjectManager.getOwner(object);
		if (owner != null && owner.equals(player)) {
			DwarfCannon cannon = player.getDwarfCannon();
			if (cannon.getBalls() > 0) {
				cannon.setFiring(!cannon.isFiring());
				player.sendMessage(cannon.isFiring() ? "Your cannon sets off to fire!" : "You stop your cannon from firing.");
			} else {
				player.sendMessage("Your cannon does not have enough cannon balls to start firing.");
			}
		} else {
			player.getDialogueManager().startDialogue("SimpleMessage", "That is not your cannon!");
		}
	}

	/**
	 * Adds cannon balls to the player's cannon
	 *
	 * @param player
	 *            The player who used cannon balls on the cannon
	 * @param object
	 *            The cannon world object
	 * @param itemId
	 *            The item id of the cannonballs
	 */
	public static void addCannonBalls(Player player, WorldObject object, int itemId) {
		Player owner = OwnedObjectManager.getOwner(object);
		if (owner != null && owner.equals(player)) {
			int cannonBalls = player.getDwarfCannon().getBalls();
			if (cannonBalls >= 30) {
				player.getDialogueManager().startDialogue("SimpleMessage", "Your cannon is already full.");
				return;
			}
			int newCannonBalls = player.getInventory().getNumberOf(itemId);
			if (newCannonBalls > 30) {
				newCannonBalls = 30;
			}
			if (newCannonBalls + cannonBalls > 30) {
				newCannonBalls = 30 - cannonBalls;
			}
			if (newCannonBalls < 1) {
				return;
			}
			player.getInventory().deleteItem(2, newCannonBalls);
			player.sendMessage("You load " + newCannonBalls + " cannonball" + (newCannonBalls > 1 ? "s" : "") + " into your cannon.");
			player.getDwarfCannon().setBalls(cannonBalls + newCannonBalls);
		} else {
			player.getDialogueManager().startDialogue("SimpleMessage", "That is not your cannon!");
		}
	}

	/**
	 * Creates a list of all of the npcs that the dwarf cannon CAN fire at in
	 * the current direction it's facing. The algorithm to get the tiles is not
	 * included in this.
	 *
	 * @param tile
	 *            The current tile of the dwarf cannon
	 * @param direction
	 *            The direction being faced
	 * @return A {@code List} {@code Object} of NPCs
	 */
	public static List<NPC> getAffectedNPCS(Player player, WorldTile tile, CannonDirection direction) {
		List<NPC> npcs = new ArrayList<NPC>();
		List<Integer> npcIndexes = World.getRegion(player.getRegionId()).getNPCsIndexes();
		if (npcIndexes == null)
			return npcs;
		for (int npcIndex : npcIndexes) {
			NPC n = World.getNPCs().get(npcIndex);
			if (n == null) {
				continue;
			}
			for (WorldTile t : getTilesByDirection(tile, direction)) {
				//World.addGroundItem(new Item(11694), t, player, false, 0, 0, 1);
				SlayerMonsters monster = SlayerMonsters.forId(n.getId());
				if (monster != null) {
					if (monster.getRequirement() > player.getSkills().getLevelForXp(Skills.SLAYER)) {
						continue;
					}
				}
				if (t.matches(n.getLocation())) {
					npcs.add(n);
				}
			}
		}
		return npcs;
	}

	/**
	 * Creates a list of the tiles that the cannon will be attacking in the
	 * current direction it's facing
	 *
	 * @param cannon
	 *            The location of the cannon
	 * @param direction
	 *            The direction the cannon is facing
	 * @return A {@code List} {@code Object} of {@code WorldTile}s
	 */
	public static List<WorldTile> getTilesByDirection(WorldTile cannon, CannonDirection direction) {
		int range = 10;
		List<WorldTile> tiles = new ArrayList<WorldTile>(range);
		try {
			switch (direction) {
			case TOP:
			case NORTH:
				for (int i = 1; i <= range; i++) {
					tiles.add(new WorldTile(cannon.getX(), cannon.getY() + i, cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() + 1, cannon.getY() + i, cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() + 2, cannon.getY() + i, cannon.getPlane()));
				}
				break;
			case NORTH_EAST:
				for (int i = 1; i <= range; i++) {
					tiles.add(new WorldTile(cannon.getX() + i, cannon.getY() + i, cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() + (i + 1), cannon.getY() + i, cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() + (i + 2), cannon.getY() + i, cannon.getPlane()));
				}
				break;
			case EAST:
				for (int i = 1; i <= range; i++) {
					tiles.add(new WorldTile(cannon.getX() + i, cannon.getY(), cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() + i, cannon.getY() + 1, cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() + i, cannon.getY() + 2, cannon.getPlane()));
				}
				break;
			case SOUTH_EAST:
				for (int i = 1; i <= range; i++) {
					tiles.add(new WorldTile(cannon.getX() + i, cannon.getY() - i, cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() + i, cannon.getY() - i + 1, cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() + i, cannon.getY() - i + 2, cannon.getPlane()));
				}
				break;
			case SOUTH:
				for (int i = 1; i <= range; i++) {
					tiles.add(new WorldTile(cannon.getX(), cannon.getY() - i, cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() + 1, cannon.getY() - i, cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() + 2, cannon.getY() - i, cannon.getPlane()));
				}
				break;
			case SOUTH_WEST:
				for (int i = 1; i <= range; i++) {
					tiles.add(new WorldTile(cannon.getX() - i, cannon.getY() - i, cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() - i + 1, cannon.getY() - i, cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() - i + 2, cannon.getY() - i, cannon.getPlane()));
				}
				break;
			case WEST:
				for (int i = 1; i <= range; i++) {
					tiles.add(new WorldTile(cannon.getX() - i, cannon.getY(), cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() - i, cannon.getY() + 1, cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() - i, cannon.getY() + 2, cannon.getPlane()));
				}
				break;
			case NORTH_WEST:
				for (int i = 1; i <= range; i++) {
					tiles.add(new WorldTile(cannon.getX() - i, cannon.getY() + i, cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() - i + 1, cannon.getY() + i, cannon.getPlane()));
					tiles.add(new WorldTile(cannon.getX() - i + 2, cannon.getY() + i, cannon.getPlane()));
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tiles;
	}

}
