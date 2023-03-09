package novite.rs.game.player.content.trading.lending;

import java.util.LinkedList;

import novite.rs.game.World;
import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.utility.Saving;

public class LoanedItemsManager {

	private LinkedList<LoanedItem> loanedItems = new LinkedList<LoanedItem>();

	public void process() {
		for (LoanedItem item : loanedItems) {
			if (!checkAll(item)) {
				System.out.println("Something happened, should probably remove it.");
				loanedItems.remove(item);
				continue;
			}
			if (System.currentTimeMillis() > item.getExpiredTime()) {
				Player loaner = item.getLoaner();
				Player owner = item.getOwner();
				boolean ownerOnline = World.containsPlayer(owner.getUsername());
				boolean loanerOnline = World.containsPlayer(loaner.getUsername());
				if (!loanerOnline) {
					loaner = Saving.loadPlayer(loaner.getUsername());
				}
				if (!ownerOnline) {
					owner = Saving.loadPlayer(owner.getUsername());
				}
				if (owner == null) {
					//XXX remove the loaners lent item
					loanedItems.remove(item);
					continue;
				} else if (loaner == null) {
					//XXX attempt to return item to the loaner
					loanedItems.remove(item);
					continue;
				}
				if (loaner.getInventory().contains(item.getLentItem().getDefinitions().getLendId())) {
					loaner.getInventory().getItems().remove(new Item(item.getLentItem().getDefinitions().getLendId()));
					if (loanerOnline) {
						loaner.getInventory().refresh();
					}
				}
				if (loaner.getEquipment().getItems().containsOne(new Item(item.getLentItem().getDefinitions().getLendId()))) {
					loaner.getEquipment().getItems().remove(new Item(item.getLentItem().getDefinitions().getLendId()));
					if (loanerOnline) {
						loaner.getEquipment().refresh();
						loaner.getAppearence().generateAppearenceData();
					}
				}
				if (loaner.getBank().containsItem(item.getLentItem().getDefinitions().getLendId(), 1)) {
					loaner.getBank().removeItem(item.getLentItem().getDefinitions().getLendId());
					if (loanerOnline) {
						loaner.getBank().refreshItems();
					}
				}
				if (ownerOnline) {
					owner.sendMessage("<col=FF0000>An item you lent out has been added back to your bank.");
				}
				if (loanerOnline) {
					loaner.sendMessage("<col=FF0000>An item you borrowed has been returned to the owner.");
				}
			}
		}
	}

	public boolean checkAll(LoanedItem item) {
		return item != null && item.getLentItem() != null && item.getLoaner() != null && item.getOwner() != null;
	}
}
