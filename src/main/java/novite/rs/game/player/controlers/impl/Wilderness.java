package novite.rs.game.player.controlers.impl;

import novite.rs.Constants;
import novite.rs.cache.loaders.ObjectDefinitions;
import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.ForceMovement;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.others.DeathNPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.content.achievements.impl.VarrockEasyDitchAchievement;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class Wilderness extends Controller {

	private boolean showingSkull;

	@Override
	public void start() {
		checkBoosts(player);
	}

	public static void checkBoosts(Player player) {
		boolean changed = false;
		int level = player.getSkills().getLevelForXp(Skills.ATTACK);
		int maxLevel = (int) (level + 5 + (level * 0.15));
		if (maxLevel < player.getSkills().getLevel(Skills.ATTACK)) {
			player.getSkills().set(Skills.ATTACK, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Skills.STRENGTH);
		maxLevel = (int) (level + 5 + (level * 0.15));
		if (maxLevel < player.getSkills().getLevel(Skills.STRENGTH)) {
			player.getSkills().set(Skills.STRENGTH, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Skills.DEFENCE);
		maxLevel = (int) (level + 5 + (level * 0.15));
		if (maxLevel < player.getSkills().getLevel(Skills.DEFENCE)) {
			player.getSkills().set(Skills.DEFENCE, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Skills.RANGE);
		maxLevel = (int) (level + 5 + (level * 0.1));
		if (maxLevel < player.getSkills().getLevel(Skills.RANGE)) {
			player.getSkills().set(Skills.RANGE, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Skills.MAGIC);
		maxLevel = level + 5;
		if (maxLevel < player.getSkills().getLevel(Skills.MAGIC)) {
			player.getSkills().set(Skills.MAGIC, maxLevel);
			changed = true;
		}
		if (changed) {
			player.getPackets().sendGameMessage("Your extreme potion bonus has been reduced.");
		}
	}

	@Override
	public boolean login() {
		moved();
		return false;
	}

	@Override
	public boolean keepCombating(Entity target) {
		if (target instanceof NPC) {
			return true;
		}
		if (!canAttack(target)) {
			return false;
		}
		if (target.getAttackedBy() != player && player.getAttackedBy() != target) {
			player.setWildernessSkull();
		}
		return true;
	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof Player) {
			Player p2 = (Player) target;
			if (player.isCanPvp() && !p2.isCanPvp()) {
				player.getPackets().sendGameMessage("That player is not in the wilderness.");
				return false;
			}
			if (canHit(target)) {
				return true;
			}
			// warning message here
			return false;
		}
		return true;
	}

	@Override
	public boolean canHit(Entity target) {
		if (target instanceof NPC) {
			return true;
		}
		Player p2 = (Player) target;
		if (Math.abs(player.getSkills().getCombatLevel() - p2.getSkills().getCombatLevel()) > getWildLevel(player)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		if (getWildLevel(player) > 20) {
			player.getPackets().sendGameMessage("You must travel " + (getWildLevel(player) - 20) + " levels down the wilderness to teleport.");
			return false;
		}
		if (player.getTeleBlockDelay() > Utils.currentTimeMillis()) {
			player.getPackets().sendGameMessage("You are currently teleblocked!");
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		if (player.getTeleBlockDelay() > Utils.currentTimeMillis()) {
			player.getPackets().sendGameMessage("You are currently teleblocked!");
			return false;
		}
		return true;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		if (getWildLevel(player) > 20) {
			player.getPackets().sendGameMessage("You must travel " + (getWildLevel(player) - 20) + " levels down the wilderness to teleport.");
			return false;
		}
		if (player.getTeleBlockDelay() > Utils.currentTimeMillis()) {
			player.getPackets().sendGameMessage("You are currently teleblocked!");
			return false;
		}
		return true;
	}

	public void showSkull() {
		player.getInterfaceManager().sendTab(player.getInterfaceManager().hasResizableScreen() ? 10 : 19, 381);
	}

	public void removeIcon() {
		if (showingSkull) {
			showingSkull = false;
			player.setCanPvp(false);
			player.getPackets().closeInterface(player.getInterfaceManager().hasResizableScreen() ? 10 : 19);
			player.getAppearence().generateAppearenceData();
			player.getEquipment().refresh(null);
		}
	}

	public static boolean isDitch(int id) {
		return id >= 1440 && id <= 1444 || id >= 65076 && id <= 65087;
	}

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (isDitch(object.getId())) {
			player.lock();
			player.setNextAnimation(new Animation(6132));
			final WorldTile toTile = new WorldTile(player.getX(), object.getY() - 1, object.getPlane());
			player.setNextForceMovement(new ForceMovement(new WorldTile(player), 1, toTile, 2, 2));
			final ObjectDefinitions objectDef = object.getDefinitions();
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (player.getRegionId() == 12854 || player.getRegionId() == 12855) {
						player.getAchievementManager().notifyUpdate(VarrockEasyDitchAchievement.class);
					}
					player.setNextWorldTile(toTile);
					player.setNextFaceWorldTile(new WorldTile(object.getCoordFaceX(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation()), object.getCoordFaceY(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation()), object.getPlane()));
					removeIcon();
					removeControler();
					player.unlock();
				}
			}, 2);
			return false;
		}
		return true;
	}

	@Override
	public void sendInterfaces() {
		if (isAtWild(player)) {
			showSkull();
		}
	}

	@Override
	public boolean sendDeath() {
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					new DeathNPC(player, DeathNPC.getClosestFreeTile(player), -1, true);
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					Player killer = player.getMostDamageReceivedSourcePlayer();
					if (killer != null && player != null) {
						if (killer.getControllerManager().getController() instanceof Wilderness) {
							killer.removeDamage(player);
							killer.increaseKillCount(player);
							killer.handleKill(player);
						}
					}
					player.sendItemsOnDeath(killer);
					player.getEquipment().init();
					player.getInventory().init();
					player.reset();
					player.setNextWorldTile(Constants.DEATH_TILE);
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					removeIcon();
					removeControler();
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void moved() {
		boolean isAtWild = isAtWild(player);
		boolean isAtWildSafe = isAtWildSafe(player);
		if (!showingSkull && isAtWild && !isAtWildSafe) {
			showingSkull = true;
			player.setCanPvp(true);
			showSkull();
			player.getAppearence().generateAppearenceData();
		} else if (showingSkull && (isAtWildSafe || !isAtWild)) {
			removeIcon();
		} else if (!isAtWildSafe && !isAtWild) {
			player.setCanPvp(false);
			removeIcon();
			removeControler();
		}
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public void forceClose() {
		removeIcon();
	}

	/**
	 * If we are in the wilderness
	 * 
	 * @param tile
	 *            The tile to check for
	 * @return
	 */
	public static boolean isAtWild(WorldTile tile) {
		return isAtWildSafe(tile) || getWildLevel(tile) > 0;
	}

	/**
	 * If the tile is at the safe area of the wilderness
	 * 
	 * @param tile
	 *            The tile to check for
	 * @return
	 */
	public static boolean isAtWildSafe(WorldTile tile) {
		return (tile.getX() >= 2940 && tile.getX() <= 3395 && tile.getY() <= 3524 && tile.getY() >= 3523);
	}

	/**
	 * Gets the wilderness level the tile is at
	 * 
	 * @param tile
	 *            The tile to check for
	 * @return
	 */
	public static int getWildLevel(WorldTile tile) {
		int x = tile.getX(), y = tile.getY();
		if (y >= 10302 && y <= 10357)
			return (byte) ((y - 9912) / 8 + 1);
		if (x > 2935 && x < 3400 && y > 3524 && y < 4000)
			return (byte) ((Math.ceil((y) - 3520D) / 8D) + 1);
		if (y > 10050 && y < 10179 && x > 3008 && x < 3144)
			return (byte) ((Math.ceil((y) - 10048D) / 8D) + 17);
		return 0;
	}

	@Override
	public boolean handleItemOption1(Player playerr, int slotId, int itemId, Item item) {
		if (itemId != item.getId()) {
			return false;
		}
		switch (itemId) {
		case -1: // Noobs
			return false;
		}
		return true;
	}
}