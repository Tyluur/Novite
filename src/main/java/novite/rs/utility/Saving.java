package novite.rs.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import novite.rs.Constants;
import novite.rs.game.player.Player;
import novite.rs.game.player.clans.Clan;

public class Saving {

	public static final String PATH = Constants.FILES_PATH + "characters/";
	public static final String CLAN_PATH = Constants.FILES_PATH + "clans/";

	public synchronized static final boolean containsPlayer(String username) {
		return new File(PATH + username + ".p").exists();
	}

	public synchronized static Player loadPlayer(String username) {
		try {
			return (Player) loadSerializedFile(new File(PATH + username + ".p"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized static void savePlayer(Player player) {
		try {
			storeSerializableClass(player, new File(PATH + player.getUsername() + ".p"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final Object loadFile(File f) {
		if (!f.exists()) {
			return null;
		}
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(new FileInputStream(f));
			Object object = in.readObject();
			in.close();
			return object;
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error loading file " + f.getAbsolutePath());
		}
		return null;
	}

	public static final Object loadSerializedFile(File f) throws IOException, ClassNotFoundException {
		if (!f.exists()) {
			return null;
		}
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		Object object = in.readObject();
		in.close();
		return object;
	}

	public static final void storeSerializableClass(Serializable o, File f) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeObject(o);
		out.close();
	}

	public synchronized static boolean containsClan(String name) {
		return new File(CLAN_PATH + name + ".c").exists();
	}

	public synchronized static Clan loadClan(String name) {
		try {
			return (Clan) loadSerializedFile(new File(CLAN_PATH + name + ".c"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized static void saveClan(Clan clan) {
		try {
			storeSerializableClass(clan, new File(CLAN_PATH + clan.getClanName() + ".c"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public synchronized static void deleteClan(Clan clan) {
		try {
			new File(CLAN_PATH + clan.getClanName() + ".c").delete();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private Saving() {

	}

}
