package novite.rs.game.npc.others;

import java.util.concurrent.TimeUnit;

import novite.rs.game.ForceTalk;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 30, 2014
 */
public class TimeSpeakingNPC extends NPC {

	public TimeSpeakingNPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, String message) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		forceTalk = new ForceTalk(message);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (lastSentTime == -1 || (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastSentTime) >= 1)) {
			setNextForceTalk(forceTalk);
			lastSentTime = System.currentTimeMillis();
		}
	}

	private final ForceTalk forceTalk;
	private long lastSentTime = -1;

	/**
	 *
	 */
	private static final long serialVersionUID = -7928975269061240435L;

}
