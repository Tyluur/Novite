package novite.rs.utility.game.punishments;

import novite.rs.api.database.ForumIntegration;
import novite.rs.engine.process.impl.PunishmentProcessor;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 24, 2014
 */
public class Punishment {

	public Punishment(String key, PunishmentType type, long duration) {
		this.setKey(key);
		this.setType(type);
		this.setDuration(duration);
	}

	/**
	 * Finds out if the punishment duration is over
	 * 
	 * @return
	 */
	public boolean isComplete() {
		synchronized (PunishmentProcessor.LOCK_OBJECT) {
			return System.currentTimeMillis() >= duration;
		}
	}

	/**
	 * Handles what to do for the punishment when it's added to the list in
	 * special cases, e.g banning on forums
	 */
	public void onAdd() {
		synchronized (PunishmentProcessor.LOCK_OBJECT) {
			switch (getType()) {
			case BAN:
				ForumIntegration.ban(getKey());
				break;
			default:
				break;
			}
		}

	}

	/**
	 * Handles what to do on the removal of the punishment
	 */
	public boolean onRemove() {
		synchronized (PunishmentProcessor.LOCK_OBJECT) {
			switch (getType()) {
			case BAN:
				if (ForumIntegration.unban(getKey())) {
					return true;
				}
				return false;
			default:
				return true;
			}
		}
	}

	/**
	 * @return the type
	 */
	public PunishmentType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(PunishmentType type) {
		this.type = type;
	}

	/**
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	private String key;
	private PunishmentType type;
	private long duration;

	public enum PunishmentType {

		MUTE, IPMUTE, BAN, IPBAN
	}

	@Override
	public String toString() {
		return "Punishment[key=" + getKey() + ", type=" + getType() + ", duration=" + duration + "]";
	}

}
