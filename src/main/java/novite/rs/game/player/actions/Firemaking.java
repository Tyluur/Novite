package novite.rs.game.player.actions;

import novite.rs.game.Animation;
import novite.rs.game.Region;
import novite.rs.game.World;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.FloorItem;
import novite.rs.game.item.Item;
import novite.rs.game.minigames.games.GamesHandler;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.content.achievements.impl.InfernoAdzeAchievement;
import novite.rs.game.player.controlers.impl.DuelArena;
import novite.rs.game.player.controlers.impl.DuelControler;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.networking.codec.handlers.InventoryOptionsHandler;
import novite.rs.utility.Utils;

public class Firemaking extends Action {

	public static enum Fire {
		NORMAL(1511, 1, 30, 2732, 40, 20), ACHEY(2862, 1, 30, 2732, 40, 1), OAK(1521, 15, 45, 2732, 60, 1), WILLOW(1519, 30, 45, 2732, 90, 1), TEAK(6333, 35, 45, 2732, 105, 1), ARCTIC_PINE(10810, 42, 50, 2732, 125, 1), MAPLE(1517, 45, 50, 2732, 135, 1), MAHOGANY(6332, 50, 70, 2732, 157.5, 1), EUCALYPTUS(12581, 58, 70, 2732, 193.5, 1), YEW(1515, 60, 80, 2732, 202.5, 1), MAGIC(1513, 75, 90, 2732, 303.8, 1), CURSED_MAGIC(13567, 82, 100, 2732, 303.8, 1);

		private int logId;
		private int level;
		private int life;
		private int fireId;
		private int time;
		private double xp;

		Fire(int logId, int level, int life, int fireId, double xp, int time) {
			this.logId = logId;
			this.level = level;
			this.life = life;
			this.fireId = fireId;
			this.xp = xp;
			this.time = time;
		}

		public int getLogId() {
			return logId;
		}

		public int getLevel() {
			return level;
		}

		public int getLife() {
			return (life * 600);
		}

		public int getFireId() {
			return fireId;
		}

		public double getExperience() {
			return xp;
		}

		public int getTime() {
			return time;
		}
		
		public static Fire getFireByLog(int logId) {
			for (Fire fire : Fire.values()) {
				if (fire.getLogId() == logId) {
					return fire;
				}
			}
			return null;
		}
	}

	private Fire fire;

	public Firemaking(Fire fire) {
		this.fire = fire;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player, fire, false)) {
			return false;
		}
		player.getPackets().sendGameMessage("You attempt to light the logs.", true);
		player.getInventory().deleteItem(fire.getLogId(), 1);
		World.addGroundItem(new Item(fire.getLogId(), 1), new WorldTile(player), player, true, 180);
		Long time = (Long) player.getTemporaryAttributtes().remove("Fire");
		boolean quickFire = time != null && time > Utils.currentTimeMillis();
		setActionDelay(player, quickFire ? 1 : 2);
		if (!quickFire) {
			player.setNextAnimation(new Animation(733));
		}
		return true;
	}

	public static boolean isFiremaking(Player player, Item item1, Item item2) {
		Item log = InventoryOptionsHandler.contains(590, item1, item2);
		if (log == null) {
			return false;
		}
		return isFiremaking(player, log.getId());
	}

	public static boolean isFiremaking(Player player, int logId) {
		for (Fire fire : Fire.values()) {
			if (fire.getLogId() == logId) {
				player.getActionManager().setAction(new Firemaking(fire));
				return true;
			}
		}
		return false;
	}

	public static boolean checkAll(Player player, Fire fire, boolean usingPyre) {
		if (!usingPyre) {
			if (!player.getInventory().contains(590)) {
				player.getPackets().sendGameMessage("You do not have the required items to light this.");
				return false;
			}
		}
		if (player.getSkills().getLevel(Skills.FIREMAKING) < fire.getLevel()) {
			player.getPackets().sendGameMessage("You do not have the required level to light this.");
			return false;
		}
		if (!World.isTileFree(player.getPlane(), player.getX(), player.getY(), 1) || World.getObjectWithSlot(usingPyre ? player.getFamiliar() : player, Region.OBJECT_SLOT_FLOOR) != null || player.getControllerManager().getController() instanceof DuelArena || player.getControllerManager().getController() instanceof DuelControler) {
			player.getPackets().sendGameMessage("You can't light a fire here.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player, fire, false);
	}

	public static double increasedExperience(Player player, double totalXp) {
		if (player.getEquipment().getGlovesId() == 13660) {
			totalXp *= 1.025;
		}
		if (player.getEquipment().getRingId() == 13659) {
			totalXp *= 1.025;
		}
		return totalXp;
	}

	@Override
	public int processWithDelay(final Player player) {
		final WorldTile tile = new WorldTile(player);
		if (!player.addWalkSteps(player.getX() - 1, player.getY(), 1)) {
			if (!player.addWalkSteps(player.getX() + 1, player.getY(), 1)) {
				if (!player.addWalkSteps(player.getX(), player.getY() + 1, 1)) {
					player.addWalkSteps(player.getX(), player.getY() - 1, 1);
				}
			}
		}
		player.getPackets().sendGameMessage("The fire catches and the logs begin to burn.", true);
		if (player.getControllerManager().getController() instanceof GamesHandler)
			((GamesHandler) player.getControllerManager().getController()).addSkillPoints(3);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				final FloorItem item = World.getRegion(tile.getRegionId()).getFloorItem(fire.getLogId(), tile, player);
				if (item == null) {
					return;
				}
				if (!World.removeGroundItem(player, item, false)) {
					return;
				}
				World.spawnTempGroundObject(new WorldObject(fire.getFireId(), 10, 0, tile.getX(), tile.getY(), tile.getPlane()), 592, fire.getLife());
				player.getSkills().addXp(Skills.FIREMAKING, increasedExperience(player, fire.getExperience()));
				player.getAchievementManager().notifyUpdate(InfernoAdzeAchievement.class);
				player.setNextFaceWorldTile(tile);
			}
		}, 1);
		player.getTemporaryAttributtes().put("Fire", Utils.currentTimeMillis() + 1800);
		return -1;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}

	public static Fire getFire(int logId) {
		for (Fire fire : Fire.values()) {
			if (fire.getLogId() == logId) {
				return fire;
			}
		}
		return null;
	}
}
