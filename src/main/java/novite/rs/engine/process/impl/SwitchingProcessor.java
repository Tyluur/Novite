package novite.rs.engine.process.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import novite.rs.engine.process.TimedProcess;
import novite.rs.game.player.Player;
import novite.rs.networking.codec.handlers.ButtonHandler;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 8, 2014
 */
public class SwitchingProcessor implements TimedProcess {

	@Override
	public Timer getTimer() {
		return new Timer(900, TimeUnit.MILLISECONDS);
	}

	@Override
	public void execute() {
		try {
			synchronized (LOCK) {
				ItemSwitch itemSwitch;
				while ((itemSwitch = queue.poll()) != null) {
					ButtonHandler.sendWear(itemSwitch.getPlayer(), itemSwitch.getSlotId(), itemSwitch.getItemId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<ItemSwitch> getSwitchesForPlayer(Player player, boolean remove) {
		List<ItemSwitch> switches = new ArrayList<>();
		Iterator<ItemSwitch> it = queue.iterator();
		while (it.hasNext()) {
			ItemSwitch item = it.next();
			if (item.getPlayer().equals(player)) {
				switches.add(item);
				if (remove) {
					it.remove();
				}
			}
		}
		return switches;
	}

	private static final Queue<ItemSwitch> queue = new ConcurrentLinkedQueue<ItemSwitch>();
	public static final Object LOCK = new Object();

	public static void addToQueue(ItemSwitch itemSwitch) {
		queue.add(itemSwitch);
	}

	public static class ItemSwitch {

		public ItemSwitch(Player player, int slotId, int itemId) {
			this.player = player;
			this.slotId = slotId;
			this.itemId = itemId;
		}

		/**
		 * @return the player
		 */
		public Player getPlayer() {
			return player;
		}

		/**
		 * @return the slotId
		 */
		public int getSlotId() {
			return slotId;
		}

		/**
		 * @return the itemId
		 */
		public int getItemId() {
			return itemId;
		}

		private final Player player;
		private final int slotId;
		private final int itemId;
	}
}
