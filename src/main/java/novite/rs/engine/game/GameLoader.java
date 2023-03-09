package novite.rs.engine.game;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import novite.rs.api.event.EventManager;
import novite.rs.api.event.command.CommandHandler;
import novite.rs.cache.Cache;
import novite.rs.cache.loaders.ItemEquipIds;
import novite.rs.engine.BlockingExecutorService;
import novite.rs.engine.CoresManager;
import novite.rs.engine.process.ProcessManagement;
import novite.rs.game.RegionBuilder;
import novite.rs.game.World;
import novite.rs.game.npc.combat.CombatScriptsHandler;
import novite.rs.game.player.actions.mining.Mining;
import novite.rs.game.player.clans.ClansManager;
import novite.rs.game.player.content.FishingSpotsHandler;
import novite.rs.game.player.content.FriendChatsManager;
import novite.rs.game.player.content.achievements.AchievementManager;
import novite.rs.game.player.content.randoms.RandomEventManager;
import novite.rs.game.player.content.scrolls.ScrollSystem;
import novite.rs.game.player.controlers.ControllerHandler;
import novite.rs.game.player.cutscenes.CutscenesHandler;
import novite.rs.game.player.dialogues.DialogueHandler;
import novite.rs.game.player.quests.QuestManager;
import novite.rs.networking.ServerChannelHandler;
import novite.rs.networking.packet.PacketSystem;
import novite.rs.utility.ItemExamines;
import novite.rs.utility.MapAreas;
import novite.rs.utility.game.Censor;
import novite.rs.utility.game.DateManager;
import novite.rs.utility.game.item.ItemNames;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.map.MapXTEA;
import novite.rs.utility.game.npc.NPCCombatDefinitionsL;
import novite.rs.utility.game.npc.NPCExamines;
import novite.rs.utility.game.npc.Nonmoving;
import novite.rs.utility.game.object.ObjectRemoval;
import novite.rs.utility.huffman.Huffman;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Feb 27, 2014
 */
public class GameLoader {

	public GameLoader() {
		load();
	}

	/**
	 * The getter
	 *
	 * @return
	 */
	public static GameLoader get() {
		return LOADER;
	}

	public BlockingExecutorService getBackgroundLoader() {
		return backgroundLoader;
	}

	/**
	 * An executor service which handles background loading tasks.
	 */
	private final BlockingExecutorService backgroundLoader = new BlockingExecutorService(Executors.newCachedThreadPool());

	/**
	 * Loads everything here
	 *
	 * @throws IOException
	 */
	public void load() {
		/** Setting the server clock time */
		DateManager.get().setTime();
		try {
			Cache.init();
			CoresManager.init();
			ServerChannelHandler.init();
			World.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getBackgroundLoader().submit(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				ItemEquipIds.init();
				ItemNames.loadNames();
				Huffman.init();
				return null;
			}
		});
		getBackgroundLoader().submit(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				RandomEventManager.get().initialize();
				ScrollSystem.get().load();
				Mining.load();
				return null;
			}
		});
		getBackgroundLoader().submit(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				MapXTEA.init();
				MapAreas.init();
				return null;
			}
		});
		getBackgroundLoader().submit(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				FriendChatsManager.init();
				ClansManager.init();
				NPCCombatDefinitionsL.init();
				Nonmoving.loadList();
				PacketSystem.load();
				return null;
			}
		});
		getBackgroundLoader().submit(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				ItemExamines.init();
				try {
					NPCExamines.loadExamines();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		getBackgroundLoader().submit(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				QuestManager.load();
				AchievementManager.load();
				return null;
			}
		});
		getBackgroundLoader().submit(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				FishingSpotsHandler.init();
				CombatScriptsHandler.init();
				DialogueHandler.init();
				ControllerHandler.init();
				CutscenesHandler.init();
				Censor.init();
				return null;
			}
		});
		getBackgroundLoader().submit(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				RegionBuilder.init();
				return null;
			}
		});
		getBackgroundLoader().submit(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				CommandHandler.get().initialize();
				ProcessManagement.get().registerEvents();
				ObjectRemoval.initialize();
				return null;
			}
		});
		getBackgroundLoader().submit(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				EventManager.get().load();
				JsonHandler.initialize();
				return null;
			}
		});
	}

	/**
	 * The instance of the loader
	 */
	private static final GameLoader LOADER = new GameLoader();

}