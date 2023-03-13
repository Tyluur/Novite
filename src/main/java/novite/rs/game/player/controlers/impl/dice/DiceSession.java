package novite.rs.game.player.controlers.impl.dice;

import novite.rs.game.item.Item;
import novite.rs.game.item.ItemsContainer;
import novite.rs.game.player.Player;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 14, 2014
 */
public class DiceSession {
	
	public DiceSession(Player player, Player target) {
		this.player = player;
		this.target = target;
		this.stake = new ItemsContainer<Item>(28, false);
		this.currentStage = Stages.OFFERING_ITEMS;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the target
	 */
	public Player getTarget() {
		return target;
	}

	/**
	 * @return the stake
	 */
	public ItemsContainer<Item> getStake() {
		return stake;
	}

	/**
	 * @return the currentStage
	 */
	public Stages getCurrentStage() {
		return currentStage;
	}

	/**
	 * @param currentStage the currentStage to set
	 */
	public void setCurrentStage(Stages currentStage) {
		this.currentStage = currentStage;
	}

	/**
	 * @return the accepted
	 */
	public boolean hasAccepted() {
		return accepted;
	}

	/**
	 * @param accepted the accepted to set
	 */
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	/**
	 * @return the rollResult
	 */
	public int getRollResult() {
		return rollResult;
	}

	/**
	 * @param rollResult the rollResult to set
	 */
	public void setRollResult(int rollResult) {
		this.rollResult = rollResult;
	}

	/**
	 * @return the gaveItems
	 */
	public boolean isGaveItems() {
		return gaveItems;
	}

	/**
	 * @param gaveItems the gaveItems to set
	 */
	public void setGaveItems(boolean gaveItems) {
		this.gaveItems = gaveItems;
	}

	private final Player player, target;
	private final ItemsContainer<Item> stake;
	
	private Stages currentStage;
	
	private boolean accepted = Boolean.FALSE;
	private boolean gaveItems = false;
	
	private int rollResult;
	
	public enum Stages {
		OFFERING_ITEMS, CONFIRMING
	}
	
	// 334, 335,
}
