package novite.rs.utility.game.json.impl;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import novite.rs.Constants;
import novite.rs.api.database.ForumIntegration;
import novite.rs.engine.process.impl.PunishmentProcessor;
import novite.rs.utility.game.json.JsonLoader;
import novite.rs.utility.game.punishments.Punishment;
import novite.rs.utility.game.punishments.Punishment.PunishmentType;

import com.google.gson.reflect.TypeToken;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 24, 2014
 */
public class PunishmentLoader extends JsonLoader<Punishment> {

	@Override
	public void initialize() {
		getPunishments().clear();

		List<Punishment> punishments = getList();
		for (Punishment punishment : punishments) {
			getPunishments().add(punishment);
		}
	}

	@Override
	public String getFileLocation() {
		return Constants.FILES_PATH + "punishments.json";
	}

	/**
	 * Adds the punishment to the server
	 *
	 * @param key
	 *            The key of the punishment
	 * @param type
	 *            The type of punishmnt
	 * @param milliseconds
	 *            The time in milliseconds it will be applied for
	 */
	public void addPunishment(String key, PunishmentType type, long milliseconds) {
		synchronized (PunishmentProcessor.LOCK_OBJECT) {
			Punishment punishment = new Punishment(key, type, System.currentTimeMillis() + milliseconds);
			List<Punishment> punishments = getList();
			punishments.add(punishment);
			punishment.onAdd();
			save(punishments);
			initialize();
		}
	}

	/**
	 * Finds out if the punishment exists
	 *
	 * @param key
	 *            The key to look for
	 * @param type
	 *            The type of punishment
	 * @return
	 */
	public static boolean isPunished(String key, PunishmentType type) {
		synchronized (PunishmentProcessor.LOCK_OBJECT) {
			Iterator<Punishment> it = PUNISHMENTS.iterator();
			while (it.hasNext()) {
				Punishment punishment = it.next();
				if (punishment.getType() == type) {
					if (key.replaceAll("_", " ").equalsIgnoreCase(punishment.getKey())) {
						return true;
					}
				}
			}
			return false;
		}
	}

	/**
	 * Finds out if the key is banned (ip ban, forum ban, or punishment timed
	 * ban)
	 *
	 * @param key
	 *            The key to check
	 * @param type
	 *            The type of punishment
	 * @return
	 */
	public static boolean isBanned(String key, PunishmentType type) {
		if (isPunished(key, type)) {
			return true;
		} else if (type == PunishmentType.BAN) {
			if (Constants.SQL_ENABLED && ForumIntegration.isBanned(key)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the list of punishments
	 *
	 * @return
	 */
	public List<Punishment> getList() {
		List<Punishment> list = load();
		if (list == null) {
			list = new ArrayList<>();
		}
		return list;
	}

	@Override
	protected List<Punishment> load() {
		List<Punishment> autospawns = null;
		String json = null;
		try {
			File file = new File(getFileLocation());
			if (!file.exists()) {
				return null;
			}
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			json = new String(chars);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		autospawns = gson.fromJson(json, new TypeToken<List<Punishment>>() {
		}.getType());
		return autospawns;
	}

	/**
	 * The list of punishments
	 */
	private static final List<Punishment> PUNISHMENTS = new ArrayList<>();

	/**
	 * @return the punishments
	 */
	public static List<Punishment> getPunishments() {
		return PUNISHMENTS;
	}

	/**
	 * Forces the removal of a punishment
	 *
	 * @param key
	 *            The key of the punishment
	 * @param type
	 *            The type of punishment
	 */
	public boolean forceRemovePunishment(String key, PunishmentType type) {
		synchronized (PunishmentProcessor.LOCK_OBJECT) {
			List<Punishment> punishments = PunishmentLoader.getPunishments();
			ListIterator<Punishment> it = punishments.listIterator();
			boolean found = false;
			while (it.hasNext()) {
				Punishment punishment = it.next();
				if (punishment.getType() == type) {
					if (key.replaceAll("_", " ").equalsIgnoreCase(punishment.getKey())) {
						System.out.println("Successfully removed a punishment: " + punishment);
						punishment.onRemove();
						it.remove();
						found = true;
					}
				}
			}
			if (found) {
				save(punishments);
				initialize();
			}
			return found;
		}
	}

}
