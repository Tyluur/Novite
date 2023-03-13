package novite.rs.game.npc.others;

import java.util.concurrent.TimeUnit;

import novite.rs.game.ForceTalk;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 21, 2014
 */
public class SandwichLady extends Follower {

	public SandwichLady(int id, WorldTile tile, String target) {
		super(id, tile, target);
		sandwich = Sandwiches.values()[Utils.random(Sandwiches.values().length - 1)];
	}

	@Override
	public void processNPC() {
		if (getAttributes().get("completed_existance") != null) {
			finish();
			return;
		}
		Player target = World.getPlayerByDisplayName(getTarget());
		if (target == null) {
			finish();
			return;
		}
		if (TimeUnit.MILLISECONDS.toSeconds(Utils.currentTimeMillis() - spawned) >= 60) {
			target.closeInterfaces();
			target.getAttributes().put("failed_sandwich_lady", "timer");
			getAttributes().put("sandwich_timer_finished", true);
			return;
		}
		/** Making sure we havent failed while message should be sent */
		if (target.getAttributes().get("failed_sandwich_lady") == null) {
			if (lastMessageSent == -1 || TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastMessageSent) >= 15) {
				setNextForceTalk(new ForceTalk(MESSAGES[Utils.random(MESSAGES.length - 1)].replaceAll("@NAME@", getTarget())));
				lastMessageSent = System.currentTimeMillis();
			}
		}
		if (!withinDistance(target, 10)) {
			setNextWorldTile(new WorldTile(target.getX(), target.getY(), target.getPlane()));
		}
		sendFollow();
	}
	
	@Override
	public void finish() {
		super.finish();
		Player target = World.getPlayerByDisplayName(getTarget());
		if (target == null) {
			return;
		}
		target.setCurrentRandomEvent(null);
	}

	/**
	 * @return the sandwich
	 */
	public Sandwiches getSandwich() {
		return sandwich;
	}

	public enum Sandwiches {

		BAGUETTE(10), TRIANGLE_SANDWICH(12), SQUARE_SANDWICH(14), BREAD(16), MEAT_PIE(18), CHOCOLATE_BAR(22), DOUGHNUT(20);

		Sandwiches(int buttonId) {
			this.buttonId = buttonId;
		}

		/**
		 * @return the buttonId
		 */
		public int getButtonId() {
			return buttonId;
		}

		private final int buttonId;

		/**
		 * Gets the name of the sandwich
		 * 
		 * @return
		 */
		public String getName() {
			return Utils.formatPlayerNameForDisplay(name());
		}
	}
	
	/**
	 * The sandwich you are to pick
	 */
	private final Sandwiches sandwich;

	/**
	 * The time a message was last sent
	 */
	private long lastMessageSent = -1;

	/**
	 * The possible messages the lady can say
	 */
	private static final String[] MESSAGES = new String[] { "Sandwiches, @NAME@!", "All types of sandwiches, @NAME@.", "You think I made these just for fun, @NAME@?!!?", "Variety of sandwiches made just for you, @NAME@!" };

	/**
	 * 
	 */
	private static final long serialVersionUID = 995670996698630243L;

}
