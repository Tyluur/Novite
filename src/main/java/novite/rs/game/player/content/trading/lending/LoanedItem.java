package novite.rs.game.player.content.trading.lending;

import java.util.concurrent.TimeUnit;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;

public class LoanedItem {

	private int hours;
	private boolean untilLogout;
	private Player owner;
	private Player loaner;
	private Item lentItem;

	private LoanedItem(int hours, Player owner, Player loaner, Item lentItem) {
		this.hours = hours;
		this.untilLogout = (hours == 0);
		this.owner = owner;
		this.loaner = loaner;
		this.lentItem = lentItem;
	}

	public Player getOwner() {
		return owner;
	}

	public Player getLoaner() {
		return loaner;
	}

	public Item getLentItem() {
		return lentItem;
	}

	public boolean removeOnLogout() {
		return untilLogout;
	}

	public long getExpiredTime() {
		return System.currentTimeMillis() + TimeUnit.HOURS.toMillis(hours);
	}
}
