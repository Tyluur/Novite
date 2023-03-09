package novite.rs.game;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import novite.Main;
import novite.rs.Constants;
import novite.rs.api.database.ConnectionPool;
import novite.rs.api.database.DatabaseConnection;
import novite.rs.api.database.mysql.MySQLDatabaseConfiguration;
import novite.rs.api.database.mysql.MySQLDatabaseConnection;
import novite.rs.engine.CoresManager;
import novite.rs.engine.game.GameWorker;
import novite.rs.engine.game.LoginWorker;
import novite.rs.game.item.FloorItem;
import novite.rs.game.item.Item;
import novite.rs.game.item.ItemConstants;
import novite.rs.game.minigames.GodWarsBosses;
import novite.rs.game.minigames.ZarosGodwars;
import novite.rs.game.minigames.clanwars.FfaZone;
import novite.rs.game.minigames.clanwars.RequestController;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.bosses.glacor.Glacor;
import novite.rs.game.npc.corp.CorporealBeast;
import novite.rs.game.npc.dragons.KingBlackDragon;
import novite.rs.game.npc.godwars.GodWarMinion;
import novite.rs.game.npc.godwars.armadyl.GodwarsArmadylFaction;
import novite.rs.game.npc.godwars.armadyl.KreeArra;
import novite.rs.game.npc.godwars.bandos.GeneralGraardor;
import novite.rs.game.npc.godwars.bandos.GodwarsBandosFaction;
import novite.rs.game.npc.godwars.saradomin.CommanderZilyana;
import novite.rs.game.npc.godwars.saradomin.GodwarsSaradominFaction;
import novite.rs.game.npc.godwars.zammorak.GodwarsZammorakFaction;
import novite.rs.game.npc.godwars.zammorak.KrilTstsaroth;
import novite.rs.game.npc.godwars.zaros.Nex;
import novite.rs.game.npc.godwars.zaros.NexMinion;
import novite.rs.game.npc.godwars.zaros.ZarosMinion;
import novite.rs.game.npc.kalph.KalphiteQueen;
import novite.rs.game.npc.nomad.FlameVortex;
import novite.rs.game.npc.nomad.Nomad;
import novite.rs.game.npc.normal.Jadinko;
import novite.rs.game.npc.others.AbyssalDemon;
import novite.rs.game.npc.others.BanditCampBandits;
import novite.rs.game.npc.others.Bork;
import novite.rs.game.npc.others.ConditionalDeath;
import novite.rs.game.npc.others.Cyclopse;
import novite.rs.game.npc.others.Elemental;
import novite.rs.game.npc.others.HarpieBug;
import novite.rs.game.npc.others.HoleInTheWall;
import novite.rs.game.npc.others.ItemHunterNPC;
import novite.rs.game.npc.others.Kurask;
import novite.rs.game.npc.others.LivingRock;
import novite.rs.game.npc.others.Lucien;
import novite.rs.game.npc.others.MasterOfFear;
import novite.rs.game.npc.others.MercenaryMage;
import novite.rs.game.npc.others.MiladeDeath;
import novite.rs.game.npc.others.MutatedZygomites;
import novite.rs.game.npc.others.Revenant;
import novite.rs.game.npc.others.RockCrabs;
import novite.rs.game.npc.others.Sheep;
import novite.rs.game.npc.others.TimeSpeakingNPC;
import novite.rs.game.npc.others.TormentedDemon;
import novite.rs.game.npc.others.Werewolf;
import novite.rs.game.npc.slayer.Strykewyrm;
import novite.rs.game.player.OwnedObjectManager;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.actions.BoxAction.HunterNPC;
import novite.rs.game.player.actions.mining.LivingRockCavern;
import novite.rs.game.player.content.scrolls.ClueScrollManager;
import novite.rs.game.player.controlers.impl.DuelControler;
import novite.rs.game.player.controlers.impl.Wilderness;
import novite.rs.game.player.controlers.impl.dice.DiceControler;
import novite.rs.game.route.Flags;
import novite.rs.networking.ConnectionFilteration;
import novite.rs.utility.Utils;
import novite.rs.utility.configuration.ConfigurationNode;
import novite.rs.utility.configuration.ConfigurationParser;
import novite.rs.utility.game.ChatColors;
import novite.rs.utility.game.npc.NPCSpeaking;

import com.runetoplist.RuneTopList;

public final class World {

	public static int exiting_delay;
	public static long exiting_start;

	private static final EntityList<Player> players = new EntityList<Player>(Constants.PLAYERS_LIMIT);
	private static final EntityList<NPC> npcs = new EntityList<NPC>(Constants.NPCS_LIMIT);
	private static final Map<Integer, Region> regions = Collections.synchronizedMap(new HashMap<Integer, Region>());

	public static void init() {
		try {
			addRestoreRunEnergyTask();
			addRestoreHitPointsTask();
			addRestoreSkillsTask();
			addRestoreSpecialAttackTask();
			addSummoningEffectTask();
			addOwnedObjectsTask();

			loadConfiguration();
			LivingRockCavern.init();
			RuneTopList.init("lazarus", "guuo21ak4uuqsemi");

			EXECUTOR_SERVICE.submit(getGameWorker());
			EXECUTOR_SERVICE.submit(getLoginWorker());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public List<Player> getPlayersInWilderness() {
		List<Player> players = new ArrayList<Player>();
		for (Player player : World.getPlayers()) {
			if (player.getControllerManager().getController() instanceof Wilderness) {
				players.add(player);
			}
		}
		return players;
	}

	/**
	 * Loads server configuration.
	 *
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws ClassNotFoundException
	 *             if a class loaded through reflection was not found.
	 * @throws IllegalAccessException
	 *             if a class could not be accessed.
	 * @throws InstantiationException
	 *             if a class could not be created.
	 */
	private static void loadConfiguration() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		FileInputStream fis = new FileInputStream(Constants.SQL_FILE_PATH);
		try {
			ConfigurationParser parser = new ConfigurationParser(fis);
			ConfigurationNode mainNode = parser.parse();
			if (mainNode.has("database")) {
				ConfigurationNode databaseNode = mainNode.nodeFor("database");
				MySQLDatabaseConfiguration config = new MySQLDatabaseConfiguration();
				config.setHost(databaseNode.getString("host"));
				config.setPort(databaseNode.getInteger("port"));
				config.setDatabase(databaseNode.getString("database"));
				config.setUsername(databaseNode.getString("username"));
				config.setPassword(databaseNode.getString("password"));
				setConnectionPool(new ConnectionPool<MySQLDatabaseConnection>(config));
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			fis.close();
		}
	}

	private static void addOwnedObjectsTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					OwnedObjectManager.processAll();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	private static final void addSummoningEffectTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player.getFamiliar() == null || player.isDead() || !player.hasFinished()) {
							continue;
						}
						if (player.getFamiliar().getOriginalId() == 6814) {
							player.heal(20);
							player.setNextGraphics(new Graphics(1507));
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, 0, 15, TimeUnit.SECONDS);
	}

	private static final void addRestoreSpecialAttackTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.isDead() || !player.isRunning()) {
							continue;
						}
						player.getCombatDefinitions().restoreSpecialAttack();
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, 0, 30000);
	}

	private static boolean checkAgility;

	private static final void addRestoreRunEnergyTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.isDead() || !player.isRunning() || (checkAgility && player.getSkills().getLevel(Skills.AGILITY) < 70)) {
							continue;
						}
						player.restoreRunEnergy();
					}
					checkAgility = !checkAgility;
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, 0, 1000);
	}

	private static final void addRestoreHitPointsTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.isDead() || !player.isRunning()) {
							continue;
						}
						player.restoreHitPoints();
					}
					for (NPC npc : npcs) {
						if (npc == null || npc.isDead() || npc.hasFinished()) {
							continue;
						}
						npc.restoreHitPoints();
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, 0, 6000);
	}

	private static final void addRestoreSkillsTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || !player.isRunning()) {
							continue;
						}
						int ammountTimes = player.getPrayer().usingPrayer(0, 8) ? 2 : 1;
						if (player.isResting()) {
							ammountTimes += 1;
						}
						boolean berserker = player.getPrayer().usingPrayer(1, 5);
						for (int skill = 0; skill < 25; skill++) {
							if (skill == Skills.SUMMONING) {
								continue;
							}
							for (int time = 0; time < ammountTimes; time++) {
								int currentLevel = player.getSkills().getLevel(skill);
								int normalLevel = player.getSkills().getLevelForXp(skill);
								if (currentLevel > normalLevel) {
									if (skill == Skills.ATTACK || skill == Skills.STRENGTH || skill == Skills.DEFENCE || skill == Skills.RANGE || skill == Skills.MAGIC) {
										if (berserker && Utils.getRandom(100) <= 15) {
											continue;
										}
									}
									player.getSkills().set(skill, currentLevel - 1);
								} else if (currentLevel < normalLevel) {
									player.getSkills().set(skill, currentLevel + 1);
								} else {
									break;
								}
							}
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, 0, 30000);

	}

	public static final Map<Integer, Region> getRegions() {
		return regions;
	}

	public static final Region getRegion(int id) {
		try {
			return getRegion(id, false);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isTileFree(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++) {
			for (int tileY = y; tileY < y + size; tileY++) {
				if (!isFloorFree(plane, tileX, tileY) || !isWallsFree(plane, tileX, tileY)) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isFloorFree(int plane, int x, int y) {
		return (getMask(plane, x, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ)) == 0;
	}

	public static boolean isWallsFree(int plane, int x, int y) {
		return (getMask(plane, x, y) & (Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST | Flags.WALLOBJ_EAST | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST)) == 0;
	}

	public static final void spawnTempGroundObject(final WorldObject object, final int replaceId, long time) {
		spawnObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					removeObject(object);
					addGroundItem(new Item(replaceId), object, null, false, 180);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, time, TimeUnit.MILLISECONDS);
	}

	public static final Region getRegion(int id, boolean load) {
		Region region = regions.get(id);
		if (region == null) {
			region = new Region(id);
			regions.put(id, region);
		}
		if (load) {
			region.checkLoadMap();
		}
		return region;
	}

	public static final void addPlayer(Player player) {
		players.add(player);
		ConnectionFilteration.add(player.getSession().getIP());
	}

	public static void removePlayer(Player player) {
		players.remove(player);
		ConnectionFilteration.remove(player.getSession().getIP());
	}

	public static final void addNPC(NPC npc) {
		npcs.add(npc);
	}

	public static final void removeNPC(NPC npc) {
		npcs.remove(npc);
	}

	public static final NPC spawnNPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		NPC n = null;
		String message = NPCSpeaking.getMessage(id);
		HunterNPC hunterNPCs = HunterNPC.forId(id);
		if (hunterNPCs != null && id == hunterNPCs.getNpcId()) {
			n = new ItemHunterNPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id >= 5533 && id <= 5558) {
			n = new Elemental(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 1926 || id == 1931) {
			n = new BanditCampBandits(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 14872) {
			n = new MiladeDeath(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 6078 || id == 6079 || id == 4292 || id == 4291 || id == 6080 || id == 6081) {
			n = new Cyclopse(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 7134) {
			n = new Bork(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 9441) {
			n = new FlameVortex(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id >= 8832 && id <= 8834) {
			n = new LivingRock(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id >= 13465 && id <= 13481) {
			n = new Revenant(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 1158 || id == 1160) {
			n = new KalphiteQueen(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id >= 8528 && id <= 8532) {
			n = new Nomad(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 13456 || id == 13457 || id == 13458 || id == 13459) {
			n = new ZarosMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 6261 || id == 6263 || id == 6265) {
			n = GodWarsBosses.graardorMinions[(id - 6261) / 2] = new GodWarMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 6260) {
			n = new GeneralGraardor(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 6222) {
			n = new KreeArra(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 6223 || id == 6225 || id == 6227 || id == 6081) {
			n = GodWarsBosses.armadylMinions[(id - 6223) / 2] = new GodWarMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 6203) {
			n = new KrilTstsaroth(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 6204 || id == 6206 || id == 6208) {
			n = GodWarsBosses.zamorakMinions[(id - 6204) / 2] = new GodWarMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 6248 || id == 6250 || id == 6252) {
			n = GodWarsBosses.commanderMinions[(id - 6248) / 2] = new GodWarMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 6247) {
			n = new CommanderZilyana(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id >= 6210 && id <= 6221) {
			n = new GodwarsZammorakFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id >= 6254 && id <= 6259) {
			n = new GodwarsSaradominFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id >= 6268 && id <= 6283) {
			n = new GodwarsBandosFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id >= 6228 && id <= 6246) {
			n = new GodwarsArmadylFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 1615) {
			n = new AbyssalDemon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 2058) {
			n = new HoleInTheWall(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 50 || id == 2642) {
			n = new KingBlackDragon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id >= 9462 && id <= 9467) {
			n = new Strykewyrm(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id >= 6026 && id <= 6045) {
			n = new Werewolf(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 1266 || id == 1268 || id == 2453 || id == 2886) {
			n = new RockCrabs(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 8133) {
			n = new CorporealBeast(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 13447) {
			n = ZarosGodwars.nex = new Nex(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 13451) {
			n = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 13452) {
			n = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 13453) {
			n = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 13454) {
			n = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 14256) {
			n = new Lucien(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 1282) {
			n = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
			n.setRandomWalk(true);
		} else if (id == 43 || (id >= 5156 && id <= 5164) || id == 5156 || id == 1765) {
			n = new Sheep(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 8335) {
			n = new MercenaryMage(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 8349 || id == 8350 || id == 8351) {
			n = new TormentedDemon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 15149) {
			n = new MasterOfFear(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 1631 || id == 1632) {
			n = new ConditionalDeath(4161, "The rockslug shrivels and dies.", true, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 14301) {
			n = new Glacor(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 1610) {
			n = new ConditionalDeath(4162, "The gargoyle breaks into peices as you slam the hammer onto its head.", false, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 14849) {
			n = new ConditionalDeath(23035, null, false, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 1627 || id == 1628 || id == 1629 || id == 1630) {
			n = new ConditionalDeath(4158, null, false, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id >= 2803 && id <= 2808) {
			n = new ConditionalDeath(6696, null, true, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 1609 || id == 1610) {
			n = new Kurask(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 3153) {
			n = new HarpieBug(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 3344 || id == 3345 || id == 3346 || id == 3347) {
			n = new MutatedZygomites(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 13820 || id == 13821 || id == 13822) {
			n = new Jadinko(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 1131 || id == 1132 || id == 1133 || id == 1134) {
			n = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
			n.setForceAgressive(true);
		} else if (message != null) {
			n = new TimeSpeakingNPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, message);
		} else {
			n = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		}
		//if (!Constants.isVPS)
		//	n.setForceAgressive(true);
		return n;
	}

	public static final void spawnNPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		spawnNPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	}

	/*
	 * check if the entity region changed because moved or teled then we update
	 * it
	 */
	public static final void updateEntityRegion(Entity entity) {
		if (entity.hasFinished()) {
			if (entity instanceof Player) {
				getRegion(entity.getLastRegionId()).removePlayerIndex(entity.getIndex());
			} else {
				getRegion(entity.getLastRegionId()).removeNPCIndex(entity.getIndex());
			}
			return;
		}
		int regionId = entity.getRegionId();
		if (entity.getLastRegionId() != regionId) { // map region entity at
			// changed
			if (entity instanceof Player) {
				if (entity.getLastRegionId() > 0) {
					getRegion(entity.getLastRegionId()).removePlayerIndex(entity.getIndex());
				}
				Region region = getRegion(regionId);
				region.addPlayerIndex(entity.getIndex());
				Player player = (Player) entity;
				player.getControllerManager().moved();
				int musicId = region.getMusicId();
				if (musicId != -1) {
					player.getMusicsManager().checkMusic(region.getMusicId());
				}
				if (player.isRunning() && player.getControllerManager().getController() == null) {
					checkControllersAtMove(player);
				}
			} else {
				if (entity.getLastRegionId() > 0) {
					getRegion(entity.getLastRegionId()).removeNPCIndex(entity.getIndex());
				}
				getRegion(regionId).addNPCIndex(entity.getIndex());
			}
			entity.checkMultiArea();
			entity.setLastRegionId(regionId);
		} else {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				player.getControllerManager().moved();
				if (player.isRunning() && player.getControllerManager().getController() == null) {
					checkControllersAtMove(player);
				}
			}
			entity.checkMultiArea();
		}
	}

	private static void checkControllersAtMove(Player player) {
		if (player.getControllerManager().getController() == null) {
			String control = null;
			if (!(player.getControllerManager().getController() instanceof RequestController) && RequestController.inWarRequest(player))
				control = "clan_wars_request";
			else if (Wilderness.isAtWild(player))
				control = "Wilderness";
			else if (DuelControler.isAtDuelArena(player))
				control = "DuelControler";
			else if (DiceControler.isAtDiceArea(player))
				control = "DiceControler";
			else if (FfaZone.inArea(player))
				control = "clan_wars_ffa";
			if (control != null)
				player.getControllerManager().startController(control);
		}
	}

	/*
	 * checks clip
	 */
	public static boolean canMoveNPC(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++) {
			for (int tileY = y; tileY < y + size; tileY++) {
				if (getMask(plane, tileX, tileY) != 0) {
					return false;
				}
			}
		}
		return true;
	}

	public static int getMask(int plane, int x, int y) {
		WorldTile tile = new WorldTile(x, y, plane);
		Region region = getRegion(tile.getRegionId());
		if (region == null) {
			return -1;
		}
		return region.getMask(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
	}

	private static int getClipedOnlyMask(int plane, int x, int y) {
		WorldTile tile = new WorldTile(x, y, plane);
		Region region = getRegion(tile.getRegionId());
		if (region == null) {
			return -1;
		}
		return region.getMaskClipedOnly(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
	}

	public static final boolean checkProjectileStep(int plane, int x, int y, int dir, int size) {
		int xOffset = Utils.DIRECTION_DELTA_X[dir];
		int yOffset = Utils.DIRECTION_DELTA_Y[dir];

		if (size == 1) {
			int mask = getClipedOnlyMask(plane, x + Utils.DIRECTION_DELTA_X[dir], y + Utils.DIRECTION_DELTA_Y[dir]);
			if (xOffset == -1 && yOffset == 0) {
				return (mask & 0x42240000) == 0;
			}
			if (xOffset == 1 && yOffset == 0) {
				return (mask & 0x60240000) == 0;
			}
			if (xOffset == 0 && yOffset == -1) {
				return (mask & 0x40a40000) == 0;
			}
			if (xOffset == 0 && yOffset == 1) {
				return (mask & 0x48240000) == 0;
			}
			if (xOffset == -1 && yOffset == -1) {
				return (mask & 0x43a40000) == 0 && (getClipedOnlyMask(plane, x - 1, y) & 0x42240000) == 0 && (getClipedOnlyMask(plane, x, y - 1) & 0x40a40000) == 0;
			}
			if (xOffset == 1 && yOffset == -1) {
				return (mask & 0x60e40000) == 0 && (getClipedOnlyMask(plane, x + 1, y) & 0x60240000) == 0 && (getClipedOnlyMask(plane, x, y - 1) & 0x40a40000) == 0;
			}
			if (xOffset == -1 && yOffset == 1) {
				return (mask & 0x4e240000) == 0 && (getClipedOnlyMask(plane, x - 1, y) & 0x42240000) == 0 && (getClipedOnlyMask(plane, x, y + 1) & 0x48240000) == 0;
			}
			if (xOffset == 1 && yOffset == 1) {
				return (mask & 0x78240000) == 0 && (getClipedOnlyMask(plane, x + 1, y) & 0x60240000) == 0 && (getClipedOnlyMask(plane, x, y + 1) & 0x48240000) == 0;
			}
		} else if (size == 2) {
			if (xOffset == -1 && yOffset == 0) {
				return (getClipedOnlyMask(plane, x - 1, y) & 0x43a40000) == 0 && (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4e240000) == 0;
			}
			if (xOffset == 1 && yOffset == 0) {
				return (getClipedOnlyMask(plane, x + 2, y) & 0x60e40000) == 0 && (getClipedOnlyMask(plane, x + 2, y + 1) & 0x78240000) == 0;
			}
			if (xOffset == 0 && yOffset == -1) {
				return (getClipedOnlyMask(plane, x, y - 1) & 0x43a40000) == 0 && (getClipedOnlyMask(plane, x + 1, y - 1) & 0x60e40000) == 0;
			}
			if (xOffset == 0 && yOffset == 1) {
				return (getClipedOnlyMask(plane, x, y + 2) & 0x4e240000) == 0 && (getClipedOnlyMask(plane, x + 1, y + 2) & 0x78240000) == 0;
			}
			if (xOffset == -1 && yOffset == -1) {
				return (getClipedOnlyMask(plane, x - 1, y) & 0x4fa40000) == 0 && (getClipedOnlyMask(plane, x - 1, y - 1) & 0x43a40000) == 0 && (getClipedOnlyMask(plane, x, y - 1) & 0x63e40000) == 0;
			}
			if (xOffset == 1 && yOffset == -1) {
				return (getClipedOnlyMask(plane, x + 1, y - 1) & 0x63e40000) == 0 && (getClipedOnlyMask(plane, x + 2, y - 1) & 0x60e40000) == 0 && (getClipedOnlyMask(plane, x + 2, y) & 0x78e40000) == 0;
			}
			if (xOffset == -1 && yOffset == 1) {
				return (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4fa40000) == 0 && (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4e240000) == 0 && (getClipedOnlyMask(plane, x, y + 2) & 0x7e240000) == 0;
			}
			if (xOffset == 1 && yOffset == 1) {
				return (getClipedOnlyMask(plane, x + 1, y + 2) & 0x7e240000) == 0 && (getClipedOnlyMask(plane, x + 2, y + 2) & 0x78240000) == 0 && (getClipedOnlyMask(plane, x + 1, y + 1) & 0x78e40000) == 0;
			}
		} else {
			if (xOffset == -1 && yOffset == 0) {
				if ((getClipedOnlyMask(plane, x - 1, y) & 0x43a40000) != 0 || (getClipedOnlyMask(plane, x - 1, -1 + (y + size)) & 0x4e240000) != 0) {
					return false;
				}
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++) {
					if ((getClipedOnlyMask(plane, x - 1, y + sizeOffset) & 0x4fa40000) != 0) {
						return false;
					}
				}
			} else if (xOffset == 1 && yOffset == 0) {
				if ((getClipedOnlyMask(plane, x + size, y) & 0x60e40000) != 0 || (getClipedOnlyMask(plane, x + size, y - (-size + 1)) & 0x78240000) != 0) {
					return false;
				}
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++) {
					if ((getClipedOnlyMask(plane, x + size, y + sizeOffset) & 0x78e40000) != 0) {
						return false;
					}
				}
			} else if (xOffset == 0 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x, y - 1) & 0x43a40000) != 0 || (getClipedOnlyMask(plane, x + size - 1, y - 1) & 0x60e40000) != 0) {
					return false;
				}
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++) {
					if ((getClipedOnlyMask(plane, x + sizeOffset, y - 1) & 0x63e40000) != 0) {
						return false;
					}
				}
			} else if (xOffset == 0 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x, y + size) & 0x4e240000) != 0 || (getClipedOnlyMask(plane, x + (size - 1), y + size) & 0x78240000) != 0) {
					return false;
				}
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++) {
					if ((getClipedOnlyMask(plane, x + sizeOffset, y + size) & 0x7e240000) != 0) {
						return false;
					}
				}
			} else if (xOffset == -1 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x - 1, y - 1) & 0x43a40000) != 0) {
					return false;
				}
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++) {
					if ((getClipedOnlyMask(plane, x - 1, y + (-1 + sizeOffset)) & 0x4fa40000) != 0 || (getClipedOnlyMask(plane, sizeOffset - 1 + x, y - 1) & 0x63e40000) != 0) {
						return false;
					}
				}
			} else if (xOffset == 1 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x + size, y - 1) & 0x60e40000) != 0) {
					return false;
				}
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++) {
					if ((getClipedOnlyMask(plane, x + size, sizeOffset + (-1 + y)) & 0x78e40000) != 0 || (getClipedOnlyMask(plane, x + sizeOffset, y - 1) & 0x63e40000) != 0) {
						return false;
					}
				}
			} else if (xOffset == -1 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x - 1, y + size) & 0x4e240000) != 0) {
					return false;
				}
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++) {
					if ((getClipedOnlyMask(plane, x - 1, y + sizeOffset) & 0x4fa40000) != 0 || (getClipedOnlyMask(plane, -1 + (x + sizeOffset), y + size) & 0x7e240000) != 0) {
						return false;
					}
				}
			} else if (xOffset == 1 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x + size, y + size) & 0x78240000) != 0) {
					return false;
				}
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++) {
					if ((getClipedOnlyMask(plane, x + sizeOffset, y + size) & 0x7e240000) != 0 || (getClipedOnlyMask(plane, x + size, y + sizeOffset) & 0x78e40000) != 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public static final boolean checkWalkStep(int plane, int x, int y, int dir, int size) {
		return checkWalkStep(plane, x, y, Utils.DIRECTION_DELTA_X[dir], Utils.DIRECTION_DELTA_Y[dir], size);
	}

	public static final boolean checkWalkStep(int plane, int x, int y, int xOffset, int yOffset, int size) {
		if (size == 1) {
			int mask = getMask(plane, x + xOffset, y + yOffset);
			if (xOffset == -1 && yOffset == 0)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0 && (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0 && (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0 && (getMask(plane, x + 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0 && (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0 && (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0 && (getMask(plane, x, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0 && (getMask(plane, x + 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0 && (getMask(plane, x, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
		} else if (size == 2) {
			if (xOffset == -1 && yOffset == 0)
				return (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0 && (getMask(plane, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (getMask(plane, x + 2, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0 && (getMask(plane, x + 2, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0 && (getMask(plane, x + 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (getMask(plane, x, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0 && (getMask(plane, x + 1, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) == 0 && (getMask(plane, x - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0 && (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (getMask(plane, x + 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) == 0 && (getMask(plane, x + 2, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0 && (getMask(plane, x + 2, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (getMask(plane, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) == 0 && (getMask(plane, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0 && (getMask(plane, x, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (getMask(plane, x + 1, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) == 0 && (getMask(plane, x + 2, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0 && (getMask(plane, x + 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
		} else {
			if (xOffset == -1 && yOffset == 0) {
				if ((getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0 || (getMask(plane, x - 1, -1 + (y + size)) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x - 1, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 0) {
				if ((getMask(plane, x + size, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0 || (getMask(plane, x + size, y - (-size + 1)) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + size, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == -1) {
				if ((getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0 || (getMask(plane, x + size - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == 1) {
				if ((getMask(plane, x, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0 || (getMask(plane, x + (size - 1), y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == -1) {
				if ((getMask(plane, x - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x - 1, y + (-1 + sizeOffset)) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0 || (getMask(plane, sizeOffset - 1 + x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == -1) {
				if ((getMask(plane, x + size, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x + size, sizeOffset + (-1 + y)) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0 || (getMask(plane, x + sizeOffset, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == 1) {
				if ((getMask(plane, x - 1, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x - 1, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0 || (getMask(plane, -1 + (x + sizeOffset), y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 1) {
				if ((getMask(plane, x + size, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0 || (getMask(plane, x + size, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			}
		}
		return true;
	}

	public static final boolean containsPlayer(String username) {
		for (Player p2 : players) {
			if (p2 == null) {
				continue;
			}
			if (p2.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	public static Player getPlayer(String username) {
		for (Player player : getPlayers()) {
			if (player == null) {
				continue;
			}
			if (player.getUsername().equals(username)) {
				return player;
			}
		}
		return null;
	}

	public static Player getPlayerByUID(long uid) {
		for (Player player : getPlayers()) {
			if (player == null) {
				continue;
			}
			if (player.getSession().getUid() == uid) {
				return player;
			}
		}
		return null;
	}

	public static final Player getPlayerByDisplayName(String username) {
		String formatedUsername = Utils.formatPlayerNameForProtocol(username);
		for (Player player : getPlayers()) {
			if (player == null) {
				continue;
			}
			if (player.getUsername().equals(formatedUsername) || player.getDisplayName().equals(username)) {
				return player;
			}
		}
		return null;
	}

	public static final EntityList<Player> getPlayers() {
		return players;
	}

	public static final EntityList<NPC> getNPCs() {
		return npcs;
	}

	private World() {

	}

	public static final void safeShutdown(final boolean restart, final int delay) {
		if (Constants.isVPS && exiting_start != 0) {
			return;
		}
		exiting_start = Utils.currentTimeMillis();
		exiting_delay = delay;
		final long updateTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delay);
		CoresManager.slowExecutor.scheduleAtFixedRate(new Runnable() {
			int seconds = 0;

			@Override
			public void run() {
				if (seconds++ == delay) {
					try {
						for (Player player : World.getPlayers()) {
							if (player == null || !player.hasStarted()) {
								continue;
							}
							player.realFinish();
						}
						if (restart) {
							Main.restart();
						} else {
							Main.shutdown();
							System.exit(-1);
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
				} else {
					for (Player player : World.getPlayers()) {
						if (player == null || !player.hasStarted() || player.hasFinished()) {
							continue;
						}
						player.sendNotification("Update In: " + (TimeUnit.MILLISECONDS.toSeconds(updateTime - System.currentTimeMillis())), -1);
					}
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	public static final void spawnTemporaryObject(final WorldObject object, long time) {
		spawnObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!World.isSpawnedObject(object)) {
						return;
					}
					removeObject(object);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	public static final boolean removeObjectTemporary(final WorldObject object, long time) {
		removeObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawnObject(object);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}, time, TimeUnit.MILLISECONDS);
		return true;
	}

	public static final boolean isSpawnedObject(WorldObject object) {
		return getRegion(object.getRegionId()).getSpawnedObjects().contains(object);
	}

	public static final boolean removeTemporaryObject(final WorldObject object, long time) {
		removeObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawnObject(object);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}, time, TimeUnit.MILLISECONDS);
		return true;
	}

	public static final void spawnObject(WorldObject object) {
		getRegion(object.getRegionId()).spawnObject(object, object.getPlane(), object.getXInRegion(), object.getYInRegion(), false);
	}

	public static final void unclipTile(WorldTile tile) {
		getRegion(tile.getRegionId()).unclip(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
	}

	public static final void removeObject(WorldObject object) {
		getRegion(object.getRegionId()).removeObject(object, object.getPlane(), object.getXInRegion(), object.getYInRegion());
	}

	public static final boolean containsObjectWithId(WorldTile tile, int id) {
		return getRegion(tile.getRegionId()).containsObjectWithId(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(), id);
	}

	public static final boolean containsObjectWithId(int id, WorldTile tile) {
		return getRegion(tile.getRegionId()).containsObjectWithId(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(), id);
	}

	public static final WorldObject getObjectWithId(int id, WorldTile tile) {
		return getRegion(tile.getRegionId()).getObjectWithId(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(), id);
	}

	public static final WorldObject getObjectWithId(WorldTile tile, int id) {
		return getRegion(tile.getRegionId()).getObjectWithId(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(), id);
	}

	public static final WorldObject getStandardObject(WorldTile tile) {
		return getRegion(tile.getRegionId()).getStandardObject(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
	}

	public static final WorldObject getObjectWithType(WorldTile tile, int type) {
		return getRegion(tile.getRegionId()).getObjectWithType(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(), type);
	}

	public static final WorldObject getObjectWithSlot(WorldTile tile, int slot) {
		return getRegion(tile.getRegionId()).getObjectWithSlot(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(), slot);
	}

	public static final void addGroundItem(final Item item, final WorldTile tile) {
		addGroundItem(item, tile, null, false, -1, 2, -1);
	}

	public static final void addGroundItem(final Item item, final WorldTile tile, final Player owner, boolean invisible, long hiddenTime) {
		addGroundItem(item, tile, owner, invisible, hiddenTime, 2, 150);
	}

	public static final FloorItem addGroundItem(final Item item, final WorldTile tile, final Player owner, boolean invisible, long hiddenTime, int type) {
		return addGroundItem(item, tile, owner, invisible, hiddenTime, type, 150);
	}

	/**
	 * Adds a ground item
	 * 
	 * @param item
	 * @param tile
	 * @param owner
	 * @param invisible
	 * @param hiddenTime
	 * @param type
	 *            The type of item. 0 : if the item isn't tradeable it will be
	 *            turned to gold.<br>
	 *            If the item is a barrows item it will degrade completely.<br>
	 *            1: if the item has a destroy option it will turn into gold.
	 * @param publicTime
	 * @return
	 */
	public static final FloorItem addGroundItem(final Item item, final WorldTile tile, final Player owner, boolean invisible, long hiddenTime, int type, final int publicTime) {
		if (type != 2) {
			if ((type == 0 && !ItemConstants.isTradeable(item)) || type == 1 && ItemConstants.isDestroy(item)) {
				int price = item.getDefinitions().getValue();
				if (price <= 0) {
					return null;
				}
				item.setId(995);
				item.setAmount(price);
			}
		}
		final FloorItem floorItem = new FloorItem(item, tile, owner, owner != null, invisible);
		final Region region = getRegion(tile.getRegionId());
		region.forceGetFloorItems().add(floorItem);
		if (invisible) {
			if (owner != null) {
				owner.getPackets().sendGroundItem(floorItem);
			}
			// becomes visible after x time
			if (hiddenTime != -1) {
				CoresManager.slowExecutor.schedule(new Runnable() {
					@Override
					public void run() {
						try {
							turnPublic(floorItem, publicTime);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}, hiddenTime, TimeUnit.SECONDS);
			}
		} else {
			// visible
			int regionId = tile.getRegionId();
			for (Player player : players) {
				if (player == null || !player.hasStarted() || player.hasFinished() || player.getPlane() != tile.getPlane() || !player.getMapRegionsIds().contains(regionId)) {
					continue;
				}
				player.getPackets().sendGroundItem(floorItem);
			}
			// disapears after this time
			if (publicTime != -1) {
				removeGroundItem(floorItem, publicTime);
			}
		}
		return floorItem;
	}

	public static final void spawnObjectTemporary(final WorldObject object, long time) {
		spawnObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!World.isSpawnedObject(object)) {
						return;
					}
					removeObject(object);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	public static final void turnPublic(FloorItem floorItem, int publicTime) {
		if (!floorItem.isInvisible()) {
			return;
		}
		int regionId = floorItem.getTile().getRegionId();
		final Region region = getRegion(regionId);
		if (!region.forceGetFloorItems().contains(floorItem)) {
			return;
		}
		Player realOwner = floorItem.hasOwner() ? World.getPlayer(floorItem.getOwner().getUsername()) : null;
		if (!ItemConstants.isTradeable(floorItem)) {
			region.forceGetFloorItems().remove(floorItem);
			if (realOwner != null) {
				if (realOwner.getMapRegionsIds().contains(regionId) && realOwner.getPlane() == floorItem.getTile().getPlane()) {
					realOwner.getPackets().sendRemoveGroundItem(floorItem);
				}
			}
			return;
		}
		floorItem.setInvisible(false);
		for (Player player : players) {
			if (player == null || player == realOwner || !player.hasStarted() || player.hasFinished() || player.getPlane() != floorItem.getTile().getPlane() || !player.getMapRegionsIds().contains(regionId)) {
				continue;
			}
			player.getPackets().sendGroundItem(floorItem);
		}
		// disapears after this time
		if (publicTime != -1) {
			removeGroundItem(floorItem, publicTime);
		}
	}

	public static final void updateGroundItem(Item item, final WorldTile tile, final Player owner) {
		final FloorItem floorItem = World.getRegion(tile.getRegionId()).getFloorItem(item.getId(), tile, owner);
		if (floorItem == null) {
			addGroundItem(item, tile, owner, true, 360);
			return;
		}
		floorItem.setAmount(floorItem.getAmount() + item.getAmount());
		owner.getPackets().sendRemoveGroundItem(floorItem);
		owner.getPackets().sendGroundItem(floorItem);

	}

	private static final void removeGroundItem(final FloorItem floorItem, long publicTime) {
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					int regionId = floorItem.getTile().getRegionId();
					Region region = getRegion(regionId);
					if (!region.forceGetFloorItems().contains(floorItem)) {
						return;
					}
					region.forceGetFloorItems().remove(floorItem);
					for (Player player : World.getPlayers()) {
						if (player == null || !player.hasStarted() || player.hasFinished() || player.getPlane() != floorItem.getTile().getPlane() || !player.getMapRegionsIds().contains(regionId)) {
							continue;
						}
						player.getPackets().sendRemoveGroundItem(floorItem);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, publicTime, TimeUnit.SECONDS);
	}

	public static final boolean removeGroundItem(Player player, FloorItem floorItem) {
		return removeGroundItem(player, floorItem, true);
	}

	public static final void addGroundItemForever(Item item, final WorldTile tile) {
		int regionId = tile.getRegionId();
		final FloorItem floorItem = new FloorItem(item, tile, true);
		final Region region = getRegion(tile.getRegionId());
		region.forceGetFloorItems().add(floorItem);
		for (Player player : players) {
			if (player == null || !player.hasStarted() || player.hasFinished() || player.getPlane() != floorItem.getTile().getPlane() || !player.getMapRegionsIds().contains(regionId)) {
				continue;
			}
			player.getPackets().sendGroundItem(floorItem);
		}
	}

	public static final boolean removeGroundItem(Player player, final FloorItem floorItem, boolean addToInventory) {
		int regionId = floorItem.getTile().getRegionId();
		Region region = getRegion(regionId);
		if (!region.forceGetFloorItems().contains(floorItem)) {
			return false;
		}
		if (floorItem.getId() == ClueScrollManager.EASY_SCROLL || floorItem.getId() == ClueScrollManager.MEDIUM_SCROLL || floorItem.getId() == ClueScrollManager.HARD_SCROLL || floorItem.getId() == ClueScrollManager.ELITE_SCROLL) {
			if (player.containsOneItem(Boolean.TRUE, floorItem.getId())) {
				player.getPackets().sendGameMessage("You can only have 1 scroll at a time.");
				return false;
			}
		}
		if (player.getInventory().getFreeSlots() == 0 && (!floorItem.getDefinitions().isStackable() || !player.getInventory().containsItem(floorItem.getId(), 1))) {
			player.getPackets().sendGameMessage("Not enough space in your inventory.");
			return false;
		}
		region.forceGetFloorItems().remove(floorItem);
		if (addToInventory) {
			player.getInventory().addItem(floorItem);
		}
		if (floorItem.isInvisible()) {
			player.getPackets().sendRemoveGroundItem(floorItem);
			return true;
		} else {
			for (Player p2 : World.getPlayers()) {
				if (p2 == null || !p2.hasStarted() || p2.hasFinished() || p2.getPlane() != floorItem.getTile().getPlane() || !p2.getMapRegionsIds().contains(regionId)) {
					continue;
				}
				p2.getPackets().sendRemoveGroundItem(floorItem);
			}
			if (floorItem.isForever()) {
				CoresManager.slowExecutor.schedule(new Runnable() {
					@Override
					public void run() {
						try {
							addGroundItemForever(floorItem, floorItem.getTile());
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}, 60, TimeUnit.SECONDS);
			}
			return true;
		}
	}

	public static final void sendGraphics(Entity creator, Graphics graphics, WorldTile tile) {
		if (creator == null) {
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted() || player.hasFinished() || !player.withinDistance(tile)) {
					continue;
				}
				player.getPackets().sendGraphics(graphics, tile);
			}
		} else {
			for (int regionId : creator.getMapRegionsIds()) {
				List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
				if (playersIndexes == null) {
					continue;
				}
				for (Integer playerIndex : playersIndexes) {
					Player player = players.get(playerIndex);
					if (player == null || !player.hasStarted() || player.hasFinished() || !player.withinDistance(tile)) {
						continue;
					}
					player.getPackets().sendGraphics(graphics, tile);
				}
			}
		}
	}

	public static final void sendProjectile(Entity shooter, WorldTile startTile, WorldTile receiver, int gfxId, int startHeight, int endHeight, int speed, int delay, int curve, int startDistanceOffset) {
		for (int regionId : shooter.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null) {
				continue;
			}
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null || !player.hasStarted() || player.hasFinished() || (!player.withinDistance(shooter) && !player.withinDistance(receiver))) {
					continue;
				}
				player.getPackets().sendProjectile(null, startTile, receiver, gfxId, startHeight, endHeight, speed, delay, curve, startDistanceOffset, 1);
			}
		}
	}

	public static final void sendProjectile(Entity shooter, WorldTile receiver, int gfxId, int startHeight, int endHeight, int speed, int delay, int curve, int startDistanceOffset) {
		for (int regionId : shooter.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null) {
				continue;
			}
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null || !player.hasStarted() || player.hasFinished() || (!player.withinDistance(shooter) && !player.withinDistance(receiver))) {
					continue;
				}
				player.getPackets().sendProjectile(null, shooter, receiver, gfxId, startHeight, endHeight, speed, delay, curve, startDistanceOffset, shooter.getSize());
			}
		}
	}

	public static final void sendProjectile(WorldObject object, WorldTile startTile, WorldTile endTile, int gfxId, int startHeight, int endHeight, int speed, int delay, int curve, int startOffset) {
		for (Player pl : players) {
			if (pl == null || !pl.withinDistance(object, 20)) {
				continue;
			}
			pl.getPackets().sendProjectile(null, startTile, endTile, gfxId, startHeight, endHeight, speed, delay, curve, startOffset, 1);
		}
	}

	public static final void sendProjectile(Entity shooter, Entity receiver, int gfxId, int startHeight, int endHeight, int speed, int delay, int curve, int startDistanceOffset) {
		for (int regionId : shooter.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null) {
				continue;
			}
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null || !player.hasStarted() || player.hasFinished() || (!player.withinDistance(shooter) && !player.withinDistance(receiver))) {
					continue;
				}
				int size = shooter.getSize();
				player.getPackets().sendProjectile(receiver, new WorldTile(shooter.getCoordFaceX(size), shooter.getCoordFaceY(size), shooter.getPlane()), receiver, gfxId, startHeight, endHeight, speed, delay, curve, startDistanceOffset, size);
			}
		}
	}

	public static final boolean isMultiArea(WorldTile tile) {
		int destX = tile.getX();
		int destY = tile.getY();
		return (destX >= 3200 && destX <= 3390 && destY >= 3840 && destY <= 3967) // wild
				|| (destX >= 2835 && destX <= 2880 && destY >= 5905 && destY <= 5950) || (destX >= 2992 && destX <= 3007 && destY >= 3912 && destY <= 3967) || (destX >= 2946 && destX <= 2959 && destY >= 3816 && destY <= 3831) || (destX >= 3008 && destX <= 3199 && destY >= 3856 && destY <= 3903) || (destX >= 3008 && destX <= 3071 && destY >= 3600 && destY <= 3711) || (destX >= 3270 && destX <= 3346 && destY >= 3532 && destY <= 3625) || (destX >= 2965 && destX <= 3050 && destY >= 3904 && destY <= 3959) // wild
				|| (destX >= 2815 && destX <= 2966 && destY >= 5240 && destY <= 5375) || (destX >= 2840 && destX <= 2950 && destY >= 5190 && destY <= 5230) // godwars
				|| (destX >= 3547 && destX <= 3555 && destY >= 9690 && destY <= 9699) // zaros
				// godwars
				|| KingBlackDragon.atKBD(tile) // King Black Dragon lair
				|| TormentedDemon.atTD(tile) // Tormented demon's area
				|| tile.getRegionId() == 11589 // dag kings
				|| tile.getRegionId() == 7492 // rock lobs
				|| tile.getRegionId() == 11150
				|| (destX >= 2622 && destY >= 5696 && destX <= 2573 && destY <= 5752 || tile.getRegionId() == 10074) // torms
				|| (destX >= 2368 && destY >= 3072 && destX <= 2431 && destY <= 3135) // castlewars
				// out
				|| (destX >= 2365 && destY >= 9470 && destX <= 2436 && destY <= 9532) // castlewars
				|| (destX >= 2948 && destY >= 5537 && destX <= 3071 && destY <= 5631) // Risk
				// ffa.
				|| (destX >= 2756 && destY >= 5537 && destX <= 2879 && destY <= 5631) // Safe
				|| tile.getRegionId() == 10554 || (destX >= 2970 && destX <= 3000 && destY >= 4365 && destY <= 4400)// corp
				|| (destX >= 3136 && destX <= 3327 && destY >= 3520 && destY <= 3970 || (destX >= 2376 && 5127 >= destY && destX <= 2422 && 5168 <= destY)) || (destX >= 2374 && destY >= 5129 && destX <= 2424 && destY <= 5168) // pits
				|| (destX >= 2622 && destY >= 5696 && destX <= 2573 && destY <= 5752) // torms
				|| (destX >= 2368 && destY >= 3072 && destX <= 2431 && destY <= 3135) // castlewars
				|| (destX >= 3086 && destY >= 5536 && destX <= 3315 && destY <= 5530) // Bork
				|| tile.getRegionId() == 15159 // novite-games
				|| tile.getRegionId() == 12190 || tile.getRegionId() == 12446 || (tile.getX() >= 3526 && tile.getX() <= 3550 && tile.getY() >= 5185 && tile.getY() <= 5215) // out
				|| (destX >= 2365 && destY >= 9470 && destX <= 2436 && destY <= 9532); // castlewars
		// in

		// multi
	}

	public static final boolean isPvpArea(WorldTile tile) {
		return Wilderness.isAtWild(tile);
	}

	public static final boolean inSafe(WorldTile tile) {
		int destX = tile.getX();
		int destY = tile.getY();
		if ((destX >= 3008 && destX <= 3019 && destY >= 3353 && destY <= 3358) || (destX >= 2943 && destX <= 3949 && destY >= 3367 && destY <= 3373)) {
			return true;
		} else {
			return false;
		}
	}

	public static final boolean isFalaPK(WorldTile tile) {
		int destX = tile.getX();
		int destY = tile.getY();
		if (destX >= 2936 && destX <= 3065 && destY >= 3309 && destY <= 3394 && !inSafe(tile)) {
			/*
			 * actionSender.sendPlayerOption(
			 * "<col=FFCC00><shad=CCA300>Attack<shad=0>", 1, true);
			 * getActionSender().sendInterface(12, 548, 1, 1073);
			 * actionSender.sendString
			 * ("<col=ff0000><shad=030303>Current Target", 1073, 10);
			 * actionSender
			 * .sendString("<col=0099CC><shad=007AA3>"+this.falatarget, 1073,
			 * 11);
			 */
			return true;
		} else {
			/* actionSender.sendCloseInterface(548, 12); */
			// this.falatarget = "";
			return false;
		}
	}

	public static int getIdFromName(String playerName) {
		for (Player p : players) {
			if (p == null) {
				continue;
			}
			if (p.getUsername().equalsIgnoreCase(Utils.formatPlayerNameForProtocol(playerName))) {
				return p.getIndex();
			}
		}
		return 0;
	}

	public static final Player getPlayerByIndex(int index) {
		for (Player player : getPlayers()) {
			if (player == null) {
				continue;
			}
			if (player.getIndex() == index) {
				return player;
			}
		}
		return null;
	}

	public static final void sendObjectAnimation(WorldObject object, Animation animation) {
		sendObjectAnimation(null, object, animation);
	}

	public static final void sendObjectAnimation(Entity creator, WorldObject object, Animation animation) {
		if (creator == null) {
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted() || player.hasFinished() || !player.withinDistance(object)) {
					continue;
				}
				player.getPackets().sendObjectAnimation(object, animation);
			}
		} else {
			for (int regionId : creator.getMapRegionsIds()) {
				List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
				if (playersIndexes == null) {
					continue;
				}
				for (Integer playerIndex : playersIndexes) {
					Player player = players.get(playerIndex);
					if (player == null || !player.hasStarted() || player.hasFinished() || !player.withinDistance(object)) {
						continue;
					}
					player.getPackets().sendObjectAnimation(object, animation);
				}
			}
		}
	}

	public static final void sendProjectile(WorldTile shooter, Entity receiver, int gfxId, int startHeight, int endHeight, int speed, int delay, int curve, int startDistanceOffset) {
		for (int regionId : receiver.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null) {
				continue;
			}
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null || !player.hasStarted() || player.hasFinished() || (!player.withinDistance(shooter) && !player.withinDistance(receiver))) {
					continue;
				}
				player.getPackets().sendProjectile(null, shooter, receiver, gfxId, startHeight, endHeight, speed, delay, curve, startDistanceOffset, 1);
			}
		}
	}

	/**
	 * Sends a message to everyone
	 *
	 * @param message
	 *            The message to send
	 * @param forStaff
	 *            If it's only for staff
	 * @param hide_global
	 *            If the global prefix should be removed
	 */
	public static void sendWorldMessage(String message, boolean forStaff, boolean hide_global) {
		if (!hide_global) {
			message = "<col=" + ChatColors.BLUE + ">[</col><col=800000>GLOBAL</col><col=" + ChatColors.BLUE + ">]</col>:" + message;
		}
		for (Player p : World.getPlayers()) {
			if (p == null || !p.isRunning() || (forStaff && p.getRights() == 0)) {
				continue;
			}
			p.getPackets().sendGameMessage(message, true);
		}
	}

	/**
	 * @return the connectionPool
	 */
	public static ConnectionPool<? extends DatabaseConnection> getConnectionPool() {
		return connectionPool;
	}

	/**
	 * @param connectionPool
	 *            the connectionPool to set
	 */
	public static void setConnectionPool(ConnectionPool<? extends DatabaseConnection> connectionPool) {
		World.connectionPool = connectionPool;
	}

	public static GameWorker getGameWorker() {
		return GAME_WORKER;
	}

	public static LoginWorker getLoginWorker() {
		return LOGIN_WORKER;
	}

	/**
	 * The MySQL Connection pool
	 */
	private static ConnectionPool<? extends DatabaseConnection> connectionPool;

	/**
	 * Instance of the logic service
	 */
	private static final GameWorker GAME_WORKER = new GameWorker();

	/**
	 * The instance of the login worker
	 */
	private static final LoginWorker LOGIN_WORKER = new LoginWorker();

	/**
	 * The cached thread pool executor instance.
	 * 
	 * @see Executors#newFixedThreadPool()
	 */
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

}