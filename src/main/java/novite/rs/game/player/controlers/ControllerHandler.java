package novite.rs.game.player.controlers;

import java.util.HashMap;

import novite.rs.game.minigames.akrisae.AkrisaeController;
import novite.rs.game.minigames.clanwars.FfaZone;
import novite.rs.game.minigames.clanwars.RequestController;
import novite.rs.game.minigames.clanwars.WarControler;
import novite.rs.game.minigames.games.GamesHandler;
import novite.rs.game.minigames.games.GamesLobby;
import novite.rs.game.player.controlers.impl.CorpBeastControler;
import novite.rs.game.player.controlers.impl.DTControler;
import novite.rs.game.player.controlers.impl.DuelArena;
import novite.rs.game.player.controlers.impl.DuelControler;
import novite.rs.game.player.controlers.impl.FightCaves;
import novite.rs.game.player.controlers.impl.GodWars;
import novite.rs.game.player.controlers.impl.JadinkoLair;
import novite.rs.game.player.controlers.impl.JailControler;
import novite.rs.game.player.controlers.impl.Kalaboss;
import novite.rs.game.player.controlers.impl.ObeliskControler;
import novite.rs.game.player.controlers.impl.PitsControler;
import novite.rs.game.player.controlers.impl.StartTutorial;
import novite.rs.game.player.controlers.impl.Wilderness;
import novite.rs.game.player.controlers.impl.ZGDControler;
import novite.rs.game.player.controlers.impl.dice.DiceControler;
import novite.rs.game.player.controlers.impl.dice.DiceGame;
import novite.rs.game.player.controlers.impl.guilds.warriors.WarriorsGuild;
import novite.rs.game.player.controlers.impl.quest.DesertTreasure;
import novite.rs.game.player.controlers.impl.quest.LDQuest;
import novite.rs.game.player.controlers.impl.quest.RFDQuest;

public class ControllerHandler {

	private static final HashMap<Object, Class<Controller>> handledControllers = new HashMap<Object, Class<Controller>>();

	@SuppressWarnings("unchecked")
	public static final void init() {
		try {
			handledControllers.put("Wilderness", (Class<Controller>) Class.forName(Wilderness.class.getCanonicalName()));
			handledControllers.put("Kalaboss", (Class<Controller>) Class.forName(Kalaboss.class.getCanonicalName()));
			handledControllers.put("ZGDControler", (Class<Controller>) Class.forName(ZGDControler.class.getCanonicalName()));
			handledControllers.put("DuelArena", (Class<Controller>) Class.forName(DuelArena.class.getCanonicalName()));
			handledControllers.put("DuelControler", (Class<Controller>) Class.forName(DuelControler.class.getCanonicalName()));
			handledControllers.put("CorpBeastControler", (Class<Controller>) Class.forName(CorpBeastControler.class.getCanonicalName()));
			handledControllers.put("FightPitsControler", (Class<Controller>) Class.forName(PitsControler.class.getCanonicalName()));
			handledControllers.put("DTControler", (Class<Controller>) Class.forName(DTControler.class.getCanonicalName()));
			handledControllers.put("JailControler", (Class<Controller>) Class.forName(JailControler.class.getCanonicalName()));
			//handledControllers.put("CastleWarsPlaying", (Class<Controller>) Class.forName(CastleWarsPlaying.class.getCanonicalName()));
			//handledControllers.put("CastleWarsWaiting", (Class<Controller>) Class.forName(CastleWarsWaiting.class.getCanonicalName()));
			handledControllers.put("ObeliskControler", (Class<Controller>) Class.forName(ObeliskControler.class.getCanonicalName()));
			handledControllers.put("StartTutorial", (Class<Controller>) Class.forName(StartTutorial.class.getCanonicalName()));
			//handledControllers.put("RuneSlayer", (Class<Controller>) Class.forName(RuneSlayerGame.class.getCanonicalName()));
			handledControllers.put("FightCaves", (Class<Controller>) Class.forName(FightCaves.class.getCanonicalName()));
			handledControllers.put("RFDQuest", (Class<Controller>) Class.forName(RFDQuest.class.getCanonicalName()));
			handledControllers.put("GodWars", (Class<Controller>) Class.forName(GodWars.class.getCanonicalName()));
			handledControllers.put("JadinkoLair", (Class<Controller>) Class.forName(JadinkoLair.class.getCanonicalName()));
			handledControllers.put("WarriorsGuild", (Class<Controller>) Class.forName(WarriorsGuild.class.getCanonicalName()));
			handledControllers.put("clan_wars_request", (Class<Controller>) Class.forName(RequestController.class.getCanonicalName()));
			handledControllers.put("clan_war", (Class<Controller>) Class.forName(WarControler.class.getCanonicalName()));
			handledControllers.put("clan_wars_ffa", (Class<Controller>) Class.forName(FfaZone.class.getCanonicalName()));
			handledControllers.put("Akrisae", (Class<Controller>) Class.forName(AkrisaeController.class.getCanonicalName()));
			handledControllers.put("DesertTreasure", (Class<Controller>) Class.forName(DesertTreasure.class.getCanonicalName()));
			handledControllers.put("DiceControler", (Class<Controller>) Class.forName(DiceControler.class.getCanonicalName()));
			handledControllers.put("DiceGame", (Class<Controller>) Class.forName(DiceGame.class.getCanonicalName()));
			handledControllers.put("LDQuest", (Class<Controller>) Class.forName(LDQuest.class.getCanonicalName()));
			handledControllers.put("GamesLobby", (Class<Controller>) Class.forName(GamesLobby.class.getCanonicalName()));
			handledControllers.put("GamesHandler", (Class<Controller>) Class.forName(GamesHandler.class.getCanonicalName()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static final void reload() {
		handledControllers.clear();
		init();
	}

	public static final Controller getControler(Object key) {
		if (key instanceof Controller) {
			return (Controller) key;
		}
		Class<Controller> classC = handledControllers.get(key);
		if (classC == null) {
			return null;
		}
		try {
			return classC.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
