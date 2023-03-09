package novite.rs.game.npc.others;

import java.util.concurrent.TimeUnit;

import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;

/**
 * 
 * @author Tyluur <itstyluur@gmail.com>
 * @since Nov 26, 2013
 */
public class Follower extends NPC {

	/**
	 * Creates a new following npc, which will follow the target until they are
	 * unavailable
	 * 
	 * @param id
	 *            The id of the npc
	 * @param tile
	 *            The tile the npc is spawned
	 * @param target
	 *            The target to follow
	 */
	public Follower(int id, WorldTile tile, String target) {
		super(id, tile, -1, true);
		this.target = target;
		this.spawned = Utils.currentTimeMillis();
		call();
	}

	@Override
	public void processNPC() {
		if (TimeUnit.MILLISECONDS.toSeconds(Utils.currentTimeMillis() - spawned) >= 60) {
			finish();
			return;
		}
		Player target = World.getPlayerByDisplayName(this.getTarget());
		if (target == null) {
			return;
		}
		if (!withinDistance(target, 10)) {
			setNextWorldTile(new WorldTile(target.getX(), target.getY(), target.getPlane()));
		}
		sendFollow();
	}

	/**
	 * Makes the following npc appear as close to you as they can get
	 */
	public void call() {
		Player owner = World.getPlayerByDisplayName(getTarget());
		if (owner == null) {
			return;
		}
		int size = getSize();
		WorldTile teleTile = null;
		int[][] checkNearDirs = Utils.getCoordOffsetsNear(size);
		for (int dir = 0; dir < checkNearDirs[0].length; dir++) {
			final WorldTile tile = new WorldTile(new WorldTile(owner.getX() + checkNearDirs[0][dir], owner.getY() + checkNearDirs[1][dir], owner.getPlane()));
			if (World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(), size)) { // if
				teleTile = tile;
				break;
			}
		}
		if (teleTile == null) {
			return;
		}
		setNextWorldTile(teleTile);
	}

	protected void sendFollow() {
		Player owner = World.getPlayerByDisplayName(getTarget());
		if (owner == null) {
			return;
		}
		if (getLastFaceEntity() != owner.getClientIndex()) {
			setNextFaceEntity(owner);
		}
		if (isFrozen()) {
			return;
		}
		int size = getSize();
		int targetSize = owner.getSize();
		if (Utils.colides(getX(), getY(), size, owner.getX(), owner.getY(), targetSize) && !owner.hasWalkSteps()) {
			resetWalkSteps();
			if (!addWalkSteps(owner.getX() + targetSize, getY())) {
				resetWalkSteps();
				if (!addWalkSteps(owner.getX() - size, getY())) {
					resetWalkSteps();
					if (!addWalkSteps(getX(), owner.getY() + targetSize)) {
						resetWalkSteps();
						if (!addWalkSteps(getX(), owner.getY() - size)) {
							return;
						}
					}
				}
			}
			return;
		}
		resetWalkSteps();
		if (!clipedProjectile(owner, true) || !Utils.isInRange(getX(), getY(), size, owner.getX(), owner.getY(), targetSize, 0)) {
			calcFollow(owner, 2, true, false);
		}
	}

	public String getTarget() {
		return target;
	}

	protected final String target;
	protected final long spawned;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6324002259215759669L;
}
