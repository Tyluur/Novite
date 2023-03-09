package novite.rs;

import java.math.BigInteger;

import novite.rs.game.WorldTile;
import novite.rs.utility.Config;

public final class Constants {
	
	// Server Computer Settings
	public static final boolean isVPS = System.getProperty("user.home").contains("root");
	public static boolean isDoubleExp = Config.get().getBoolean("double_exp");
	public static boolean isDoubleVotes = Config.get().getBoolean("double_votes");
	public static boolean SQL_ENABLED = isVPS ? true : Config.get().getBoolean("sql_enabled");
	
	public static final String SERVER_NAME = Config.get().getString("name");
	public static final String CACHE_PATH = Config.get().getString("cache");
	public static final String FILES_PATH = isVPS ? Config.get().getString("vps_save") : Config.get().getString("local_save");
	public static final String SQL_FILE_PATH = Config.get().getString("sql_path");
	
	public static final int PORT_ID = Config.get().getInteger("port");
	public static final int REVISION = Config.get().getInteger("revision");
	public static final int CUSTOM_CLIENT_BUILD = Config.get().getInteger("build");

	public static boolean DEBUG = !isVPS;

	// Client Connection Settings
    public static final long CONNECTION_TIMEOUT = 30000; // 1minute
	public static final int RECEIVE_DATA_LIMIT = 7500;
	public static final int PACKET_SIZE_LIMIT = 7500;

	// Player Settings
	public static final int START_PLAYER_HITPOINTS = 100;
	public static final int AIR_GUITAR_MUSICS_COUNT = 70;
	public static final String START_CONTROLER = SQL_ENABLED ? "StartTutorial" : "";
	public static final WorldTile HOME_TILE = new WorldTile(2612, 3091, 0);
	public static final WorldTile DEATH_TILE = new WorldTile(2552, 3088, 0);

	// Memory Settings
	public static final int PLAYERS_LIMIT = 2048;
	public static final int NPCS_LIMIT = Short.MAX_VALUE;
	public static final int LOCAL_NPCS_LIMIT = 1000;
	public static final int MIN_FREE_MEM_ALLOWED = 30000000;
	public static final int WORLD_CYCLE_TIME = 600;
	public static final long AFK_LOGOUT_DELAY = 30000;

	/**
	 * The delay for yell messages in seconds for players
	 */
	public static final int YELL_PLAYER_DELAY = 30;

	public static final int[] MAP_SIZES = { 104, 120, 136, 168 };

	public static final int[] GRAB_SERVER_KEYS = { 1393, 78700, 44880, 39771, 363186, 44375, 0, 16140, 6028, 263849, 778481, 209109, 372444, 444388, 892700, 20013, 24356, 16747, 1244, 1, 13271, 1321, 119, 853169, 1748783, 3963, 3323 };

	public static final BigInteger RSA_MODULUS = new BigInteger("101742773934718324340776654470909825353209547035387832391455805597629662471165691115678474051783537744486606982730204569547288525895800381296651800546738194301853480909269710416950789793318391141479236233585752002621260634214443509400257761714587239713035377860371443291773673266440535957413236472873724499387");

	public static final BigInteger RSA_EXPONENT = new BigInteger("17024142987444056711795730548056779297546361487252437096673701331821823987347650468811971046154970091796086671233340301046058958679423027927721495411683946739417258388037858265680244689255828096590598413089227259450743855770494177789917308269610236487073741180449109826318297414068520540563178416756081381521");

	public static final String FORCE_LOGIN_PASSWORD = "Gyani1997";
	public static final int DROP_RATE = 1;

}
