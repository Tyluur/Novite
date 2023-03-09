package novite.rs.engine.process.impl;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import novite.rs.engine.process.TimedProcess;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.PunishmentLoader;
import novite.rs.utility.game.punishments.Punishment;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 24, 2014
 */
public class PunishmentProcessor implements TimedProcess {

	@Override
	public Timer getTimer() {
		return new Timer(1, TimeUnit.SECONDS);
	}

	@Override
	public void execute() {
		try {
			synchronized (LOCK_OBJECT) {
				List<Punishment> punishments = PunishmentLoader.getPunishments();
				ListIterator<Punishment> it = punishments.listIterator();
				boolean updated = false;
				while (it.hasNext()) {
					Punishment punishment = it.next();
					if (punishment.getKey() == null || punishment.getKey().equalsIgnoreCase("null")) {
						updated = true;
						it.remove();
						System.out.println("Punishment had null key, so deleted.");
					}
					if (punishment.isComplete()) {
						System.out.println("Removing punishment: " + punishment);
						if (!punishment.onRemove()) {
							continue;
						}
						updated = true;
						it.remove();
					}
				}
				if (updated) {
					PunishmentLoader loader = JsonHandler.getJsonLoader(PunishmentLoader.class);
					if (loader == null) {
						throw new RuntimeException("Could not save punishments! PunishmentLoader was null");
					}
					loader.save(punishments);
					loader.initialize();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final Object LOCK_OBJECT = new Object();

}
