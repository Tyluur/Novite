package novite.rs.game.item;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;

public class FloorItem extends Item {

	/**
	 *
	 */
	private static final long serialVersionUID = 1490108289233691666L;
	private WorldTile tile;
	private Player owner;
	private boolean invisible;
	private boolean grave;
	// 0 visible, 1 invisible, 2 visible and reappears 30sec after taken
	private int type;

	public FloorItem(int id) {
		super(id);
	}

	@Override
	public void setAmount(int amount) {
		this.amount = amount;
	}

	public FloorItem(Item item, WorldTile tile, Player owner, boolean underGrave, boolean invisible) {
		super(item.getId(), item.getAmount());
		this.tile = tile;
		this.owner = owner;
		grave = underGrave;
		this.invisible = invisible;
	}

	public FloorItem(Item item, WorldTile tile, boolean appearforever) {
		super(item.getId(), item.getAmount());
		this.tile = tile;
		this.type = appearforever ? 2 : 0;
	}

	public WorldTile getTile() {
		return tile;
	}

	public boolean isGrave() {
		return grave;
	}

	public boolean isInvisible() {
		return invisible;
	}

	public Player getOwner() {
		return owner;
	}

	public boolean hasOwner() {
		return owner != null;
	}

	public boolean isForever() {
		return type == 2;
	}

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

}
