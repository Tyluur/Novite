package novite.rs.game.item;

import java.io.Serializable;

import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.cache.loaders.ItemEquipIds;

/**
 * Represents a single item.
 * <p/>
 *
 * @author Graham / edited by Dragonkk(Alex)
 */
public class Item implements Serializable {

	private static final long serialVersionUID = -6485003878697568087L;

	private short id;
	protected int amount;

	public int getId() {
		return id;
	}

	@Override
	public Item clone() {
		return new Item(id, amount);
	}

	public Item(int id) {
		this(id, 1);
	}

	public Item(int id, int amount) {
		this(id, amount, false);
	}

	public Item(int id, int amount, boolean amt0) {
		this.id = (short) id;
		this.amount = amount;
		if (this.amount <= 0 && !amt0) {
			this.amount = 1;
		}
	}

	public ItemDefinitions getDefinitions() {
		return ItemDefinitions.getItemDefinitions(id);
	}

	public int getEquipId() {
		return ItemEquipIds.getEquipId(id);
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void setId(int id) {
		this.id = (short) id;
	}

	public int getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return "Item[" + id + ", " + getAmount() + ", " + getDefinitions().name + "]";
	}

	public String getName() {
		return getDefinitions().getName();
	}

}
