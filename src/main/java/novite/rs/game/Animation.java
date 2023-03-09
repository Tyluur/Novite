package novite.rs.game;

import java.util.Arrays;

import novite.rs.utility.Utils;

public final class Animation {

	private int[] ids;
	private int speed;

	public Animation(int id) {
		this(id, 0);
	}

	public Animation(int id, int speed) {
		this(id, id, id, id, speed);
	}

	public Animation(int id1, int id2, int id3, int id4, int speed) {
		int[] anims = new int[] { id1, id2, id3, id4 };
		for (int id : anims) {
			if (!Utils.animationExists(id) && id != -1) {
				System.err.println("Bad animation!!!!! " + id);
				Arrays.fill(anims, -1);
			}
		}
		this.ids = anims;
		this.speed = speed;
	}

	public int[] getIds() {
		return ids;
	}

	public int getSpeed() {
		return speed;
	}
}
