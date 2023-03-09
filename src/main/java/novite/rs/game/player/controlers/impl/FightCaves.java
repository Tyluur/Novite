package novite.rs.game.player.controlers.impl;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import novite.rs.engine.CoresManager;
import novite.rs.game.Animation;
import novite.rs.game.RegionBuilder;
import novite.rs.game.World;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.fightcave.FightCavesNPC;
import novite.rs.game.npc.fightcave.TzKekCaves;
import novite.rs.game.npc.fightcave.TzTok_Jad;
import novite.rs.game.player.Player;
import novite.rs.game.player.actions.summoning.Summoning;
import novite.rs.game.player.content.pet.Pets;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class FightCaves extends Controller {

	public static final WorldTile OUTSIDE = new WorldTile(2441, 5174, 0);

	private static final int THHAAR_MEJ_JAL = 2617;

	private static final int[] MUSICS = { 1088, 1082, 1086 };

	public void playMusic() {
		player.getMusicsManager().playMusic(selectedMusic);
	}

	private final int[][] WAVES = { { 2734 }, { 2734, 2734 }, { 2736 }, { 2736, 2734 }, { 2736, 2734, 2734 }, { 2736, 2736 }, { 2739 }, { 2739, 2734 }, { 2739, 2734, 2734 }, { 2739, 2736 }, { 2739, 2736, 2734 }, { 2739, 2736, 2734, 2734 }, { 2739, 2736, 2736 }, { 2739, 2739 }, { 2741 }, { 2741, 2734 }, { 2741, 2734, 2734 }, { 2741, 2736 }, { 2741, 2736, 2734 }, { 2741, 2736, 2734, 2734 }, { 2741, 2736, 2736 }, { 2741, 2739 }, { 2741, 2739, 2734 }, { 2741, 2739, 2734, 2734 }, { 2741, 2739, 2736 }, { 2741, 2739, 2736, 2734 }, { 2741, 2739, 2736, 2734, 2734 }, { 2741, 2739, 2736, 2736 }, { 2741, 2739, 2739 }, { 2741, 2741 }, { 2743 }, { 2743, 2734 }, { 2743, 2734, 2734 }, { 2743, 2736 }, { 2743, 2736, 2734 }, { 2743, 2736, 2734, 2734 }, { 2743, 2736, 2736 }, { 2743, 2739 }, { 2743, 2739, 2734 }, { 2743, 2739, 2734, 2734 }, { 2743, 2739, 2736 }, { 2743, 2739, 2736, 2734 }, { 2743, 2739, 2736, 2734, 2734 }, { 2743, 2739, 2736, 2736 }, { 2743, 2739, 2739 }, { 2743, 2741 }, { 2743, 2741, 2734 }, { 2743, 2741, 2734, 2734 }, { 2743, 2741, 2736 }, { 2743, 2741, 2736, 2734 }, { 2743, 2741, 2736, 2734, 2734 }, { 2743, 2741, 2736, 2736 }, { 2743, 2741, 2739 }, { 2743, 2741, 2739, 2734 }, { 2743, 2741, 2739, 2734, 2734 }, { 2743, 2741, 2739, 2736 }, { 2743, 2741, 2739, 2736, 2734 }, { 2743, 2741, 2739, 2736, 2734, 2734 }, { 2743, 2741, 2739, 2736, 2736 }, { 2743, 2741, 2739, 2739 }, { 2743, 2741, 2741 }, { 2743, 2743 }, { 2745 } };

	private int[] boundChuncks;
	private Stages stage;
	private boolean logoutAtEnd;
	private boolean login;
	public boolean spawned;
	public int selectedMusic;

	public static void enterFightCaves(Player player) {
		if (player.getFamiliar() != null || player.getPet() != null || Summoning.hasPouch(player) || Pets.hasPet(player)) {
			player.getDialogueManager().startDialogue("SimpleNPCMessage", THHAAR_MEJ_JAL, "Get rid of your familiar or pet before entering!");
			return;
		}
		player.getControllerManager().startController("FightCaves", 1);
	}

	private static enum Stages {
		LOADING, RUNNING, DESTROYING
	}

	@Override
	public void start() {
		loadCave(false);
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int packetId) {
		if (stage != Stages.RUNNING) {
			return false;
		}
		if (interfaceId == 182 && (componentId == 6 || componentId == 13)) {
			if (!logoutAtEnd) {
				logoutAtEnd = true;
				player.getPackets().sendGameMessage("<col=ff0000>You will be logged out automatically at the end of this wave.");
				player.getPackets().sendGameMessage("<col=ff0000>If you log out sooner, you will have to repeat this wave.");
			} else {
				player.logout();
			}
			return false;
		}
		return true;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 9357) {
			if (stage != Stages.RUNNING) {
				return false;
			}
			exitCave(1);
			return false;
		}
		return true;
	}

	/*
	 * return false so wont remove script
	 */
	@Override
	public boolean login() {
		loadCave(true);
		return false;
	}

	public void loadCave(final boolean login) {
		this.login = login;
		stage = Stages.LOADING;
		player.lock(); // locks player
		CoresManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				// finds empty map bounds
				boundChuncks = RegionBuilder.findEmptyChunkBound(8, 8);
				// copys real map into the empty map
				// 552 640
				RegionBuilder.copyAllPlanesMap(302, 639, boundChuncks[0], boundChuncks[1], 64);
				RegionBuilder.copyAllPlanesMap(296, 632, boundChuncks[0], boundChuncks[1], 64);
				// selects a music
				selectedMusic = MUSICS[Utils.random(MUSICS.length)];
				player.setNextWorldTile(!login ? getWorldTile(46, 61) : getWorldTile(32, 32));
				// 1delay because player cant walk while teleing :p, + possible
				// issues avoid
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						if (!login) {
							WorldTile walkTo = getWorldTile(32, 32);
							player.addWalkSteps(walkTo.getX(), walkTo.getY());
						}
						player.getDialogueManager().startDialogue("SimpleNPCMessage", THHAAR_MEJ_JAL, "You're on your own now, JalYt.<br>Prepare to fight for your life!");
						player.setForceMultiArea(true);
						playMusic();
						player.unlock(); // unlocks player
						stage = Stages.RUNNING;
					}

				}, 1);
				if (!login) {
					/*
					 * lets stress less the worldthread, also fastexecutor used
					 * for mini stuff
					 */
					CoresManager.fastExecutor.schedule(new TimerTask() {

						@Override
						public void run() {
							try {
								if (stage != Stages.RUNNING) {
									return;
								}
								startWave();
							} catch (Throwable t) {
								t.printStackTrace();
							}
						}
					}, 6000);
				}
			}
		});
	}

	public WorldTile getSpawnTile() {
		switch (Utils.random(5)) {
		case 0:
			return getWorldTile(11, 16);
		case 1:
			return getWorldTile(51, 25);
		case 2:
			return getWorldTile(10, 50);
		case 3:
			return getWorldTile(46, 49);
		case 4:
		default:
			return getWorldTile(32, 30);
		}
	}

	@Override
	public void moved() {
		if (stage != Stages.RUNNING || !login) {
			return;
		}
		login = false;
		setWaveEvent();
	}

	public void startWave() {
		int currentWave = getCurrentWave();
		if (currentWave > WAVES.length) {
			win();
			return;
		}
		player.getInterfaceManager().sendTab(player.getInterfaceManager().hasResizableScreen() ? 6 : 9, 316);
		player.sendMessage("You are now starting wave: " + currentWave);
		player.getPackets().sendConfig(639, currentWave);
		if (stage != Stages.RUNNING) {
			return;
		}
		for (int id : WAVES[currentWave - 1]) {
			if (id == 2736) {
				new TzKekCaves(id, getSpawnTile());
			} else if (id == 2745) {
				new TzTok_Jad(id, getSpawnTile(), this);
			} else {
				new FightCavesNPC(id, getSpawnTile());
			}
		}
		spawned = true;
	}

	public void spawnHealers() {
		if (stage != Stages.RUNNING) {
			return;
		}
		for (int i = 0; i < 4; i++) {
			new FightCavesNPC(2746, getSpawnTile());
		}
	}

	public void win() {
		if (stage != Stages.RUNNING) {
			return;
		}
		exitCave(4);
	}

	public void nextWave() {
		playMusic();
		setCurrentWave(getCurrentWave() + 1);
		if (logoutAtEnd) {
			player.forceLogout();
			return;
		}
		setWaveEvent();
	}

	public void setWaveEvent() {
		if (getCurrentWave() == 63) {
			player.getDialogueManager().startDialogue("SimpleNPCMessage", THHAAR_MEJ_JAL, "Look out, here comes TzTok-Jad!");
		}
		CoresManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					if (stage != Stages.RUNNING) {
						return;
					}
					startWave();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, 600);
	}

	@Override
	public void process() {
		if (spawned) {
			List<Integer> npcs = World.getRegion(player.getRegionId()).getNPCsIndexes();
			if (npcs == null || npcs.isEmpty()) {
				spawned = false;
				nextWave();
			}
		}
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage("You have been defeated!");
				} else if (loop == 3) {
					player.reset();
					exitCave(1);
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		exitCave(2);
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage("You can't teleport out of the arena!");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage("You can't teleport out of the arena!");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage("You can't teleport out of the arena!");
		return false;
	}

	/*
	 * logout or not. if didnt logout means lost, 0 logout, 1, normal, 2 tele
	 */
	public void exitCave(int type) {
		stage = Stages.DESTROYING;
		WorldTile outside = OUTSIDE;
		if (type == 0 || type == 2) {
			player.setLocation(outside);
		} else {
			player.setForceMultiArea(false);
			player.getPackets().closeInterface(player.getInterfaceManager().hasResizableScreen() ? 6 : 9);
			if (type == 1 || type == 4) {
				player.setNextWorldTile(outside);
				if (type == 4) {
					player.setCompletedFightCaves();
					player.reset();
					player.getDialogueManager().startDialogue("SimpleNPCMessage", THHAAR_MEJ_JAL, "You even defeated Tz Tok-Jad, I am most impressed! Please accept this gift as a reward.");
					player.getPackets().sendGameMessage("You were victorious!!");
					if (!player.getInventory().addItem(6570, 1)) {
						World.addGroundItem(new Item(6570, 1), new WorldTile(player), player, true, 60);
						World.addGroundItem(new Item(6529, 16064), new WorldTile(player), player, true, 60);
					} else if (!player.getInventory().addItem(6529, 16064)) {
						World.addGroundItem(new Item(6529, 16064), new WorldTile(player), player, true, 60);
					}
				} else if (getCurrentWave() == 1) {
					player.getDialogueManager().startDialogue("SimpleNPCMessage", THHAAR_MEJ_JAL, "Well I suppose you tried... better luck next time.");
				} else {
					int tokkul = (getCurrentWave() * 8032 / WAVES.length) * 10;
					if (!player.getInventory().addItem(6529, tokkul)) {
						World.addGroundItem(new Item(6529, tokkul), new WorldTile(player), player, true, 60);
					}
					player.getDialogueManager().startDialogue("SimpleNPCMessage", THHAAR_MEJ_JAL, "Well done in the cave, here, take TokKul as reward.");
				}
			}
			removeControler();
		}
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				RegionBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
			}
		}, 1200, TimeUnit.MILLISECONDS);
	}

	/*
	 * gets worldtile inside the map
	 */
	public WorldTile getWorldTile(int mapX, int mapY) {
		return new WorldTile(boundChuncks[0] * 8 + mapX, boundChuncks[1] * 8 + mapY, 0);
	}

	/*
	 * return false so wont remove script
	 */
	@Override
	public boolean logout() {
		/*
		 * only can happen if dungeon is loading and system update happens
		 */
		if (stage != Stages.RUNNING) {
			return false;
		}
		exitCave(0);
		return false;

	}

	public int getCurrentWave() {
		if (getArguments() == null || getArguments().length == 0) {
			return 0;
		}
		return (Integer) getArguments()[0];
	}

	public void setCurrentWave(int wave) {
		if (getArguments() == null || getArguments().length == 0) {
			this.setArguments(new Object[1]);
		}
		getArguments()[0] = wave;
	}

	@Override
	public void forceClose() {
		/*
		 * shouldnt happen
		 */
		if (stage != Stages.RUNNING) {
			return;
		}
		exitCave(2);
	}
}
