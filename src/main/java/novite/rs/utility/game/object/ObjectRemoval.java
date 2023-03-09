package novite.rs.utility.game.object;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import novite.rs.game.Region;
import novite.rs.game.World;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Dec 13, 2013
 */
public class ObjectRemoval {

	/**
	 * Starts up and populates the list
	 */
	public static void initialize() {
		populateList();
	}

	/**
	 * Populates the list with data from the file
	 */
	private static void populateList() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("data/map/nonspawning.txt"));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] split = line.split(" ");
				int id = Integer.parseInt(split[0]);
				int x = Integer.parseInt(split[1]);
				int y = Integer.parseInt(split[2]);
				int z = Integer.parseInt(split[3]);
				boolean tree = false;
				if (split.length > 4) {
					tree = Boolean.parseBoolean(split[4]);
				}
				objects.add(new CustomObject(id, new WorldTile(x, y, z), tree));
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * If the list contains the objec
	 *
	 * @param object
	 *            The object to be stopped
	 * @param x
	 *            The x coord of the obj
	 * @param y
	 *            The y coord of the obj
	 * @param z
	 *            The z coord of the obj
	 * @return
	 */
	public static CustomObject getCustomObject(WorldObject object, int x, int y, int z) {
		for (CustomObject o : objects) {
			if (o.getId() == object.getId()) {
				if (o.getTile().getX() == x && o.getTile().getY() == y && o.getTile().getPlane() == z) {
					return o;
				}
			}
		}
		return null;
	}

	/**
	 * Checks if the object should be stopped before spawning it, and if it
	 * should be, it creates a new null object at the location so it doesnt
	 * appear
	 *
	 * @param object
	 *            The object to be stopped
	 * @param x
	 *            The x coord of the obj
	 * @param y
	 *            The y coord of the obj
	 * @param z
	 *            The z coord of the obj
	 * @return
	 */
	public static boolean stopBeforeSpawning(WorldObject object, int x, int y, int z) {
		CustomObject customObject = getCustomObject(object, x, y, z);
		if (customObject != null && !customObject.isTree()) {
			for (int i = 0; i < 4; i++) {
				World.spawnObject(new WorldObject(-1, object.getType(), object.getRotation(), object.getX(), object.getY(), object.getPlane() + i));
				WorldObject[] os = new WorldObject[] { new WorldObject(-1, object.getType(), object.getRotation(), object.getX() - i, object.getY() - i, object.getPlane() + i), new WorldObject(-1, object.getType(), object.getRotation(), object.getX(), object.getY() - i, object.getPlane() + i), new WorldObject(-1, object.getType(), object.getRotation(), object.getX() - i, object.getY(), object.getPlane() + i), };
				for (WorldObject o : os) {
					World.spawnObject(o);
				}
			}
			return true;
		}
		return false;
	}

	public static void removeObjects(Region region) {
		List<WorldObject> objectList = region.getObjects();
		if (objectList == null) {
			System.err.println("List was null in region " + region.getRegionId());
			return;
		}
		for (WorldObject worldObject : objectList) {
			if (worldObject == null)
				continue;
			CustomObject object = ObjectRemoval.getCustomObject(worldObject, worldObject.getX(), worldObject.getY(), worldObject.getPlane());
			if (object != null && object.isTree()) {
				World.spawnObject(new WorldObject(-1, worldObject.getType(), worldObject.getRotation(), worldObject.getX(), worldObject.getY(), worldObject.getPlane()));
				World.removeObject(worldObject);
				System.out.println("object was a tree so it was removed");
			}
		}
	}

	/**
	 * The list of objects that arent spawned
	 */
	private static final List<CustomObject> objects = new ArrayList<>();

	public static class CustomObject {

		public CustomObject(int id, WorldTile tile, boolean tree) {
			this.id = id;
			this.tile = tile;
			this.tree = tree;
		}

		public WorldTile getTile() {
			return tile;
		}

		public int getId() {
			return id;
		}

		/**
		 * @return the tree
		 */
		public boolean isTree() {
			return tree;
		}

		private final int id;

		private final WorldTile tile;

		private final boolean tree;
	}

}
