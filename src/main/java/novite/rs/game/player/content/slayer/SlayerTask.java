package novite.rs.game.player.content.slayer;

import java.io.Serializable;

import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.dialogues.SimpleNPCMessage;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 22, 2014
 */
public class SlayerTask implements Serializable {

	public SlayerTask(Type type, String name, int amount) {
		this.setType(type);
		this.setName(name);
		this.setAmount(amount);
		this.startAmount = amount;
	}

	/**
	 * Handles the death of your task
	 * 
	 * @param player
	 *            The player
	 * @param npc
	 *            The task
	 */
	public void handleDeath(Player player, NPC npc) {
		player.getSkills().addXp(Skills.SLAYER, npc.getMaxHitpoints() / 10);
		if (player.getSlayerTask().getAmount() - 1 == 0) {
			/** Adding slayer points */
			int value = type == Type.EASY ? 5 : type == Type.MEDIUM ? 10 : type == Type.HARD ? 15 : 20;
			player.getSlayerManager().setPoints(player.getSlayerManager().getPoints() + value);
			/** Removing the variable */
			player.setSlayerTask(null);
			player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 8467, "You have completed your slayer task. Speak to me", "at home to collect your points and receive a new task.", "You receive " + value + " slayer points.");
		} else {
			player.getSlayerTask().decreaseAmount();
			for (int i : checkpoints) {
				if (player.getSlayerTask().getAmount() == i) {
					player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 8467, "You're doing great, only " + i, player.getSlayerTask().getName() + " left to slay.");
				}
			}
		}
	}

	/**
	 * The checkpoints that the player receives notifications at
	 */
	private final static int[] checkpoints = new int[] { 2, 3, 4, 5, 10, 15, 25, 50, 75, 100, 125, 150 };

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * Decreases the amount
	 */
	public void decreaseAmount() {
		amount--;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * The type of task
	 */
	private Type type;

	/**
	 * The name of the task
	 */
	private String name;

	/**
	 * The amount left for the task
	 */
	private int amount;
	
	/**
	 * The amount of the task we started with
	 */
	private final int startAmount;

	@Override
	public boolean equals(Object o) {
		if (o instanceof NPC) {
			NPC npc = (NPC) o;
			if (npc.getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	/**
	 * @return the startAmount
	 */
	public int getStartAmount() {
		return startAmount;
	}

	private static final long serialVersionUID = -2651565832718013979L;

}
