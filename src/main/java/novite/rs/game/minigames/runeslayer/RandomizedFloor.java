package novite.rs.game.minigames.runeslayer;

import java.util.ArrayList;
import java.util.List;

import novite.rs.game.RegionBuilder;
import novite.rs.game.WorldTile;
import novite.rs.utility.Utils;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Dec 24, 2013
 */
public class RandomizedFloor {

	public RandomizedFloor() {
		randomizeFloor();
		getFloor().createFloor(boundChunks);
	}

	/**
	 * Sets the current floor to a random one in the {@link Floors}
	 * enumeration
	 */
	private void randomizeFloor() {
		List<Floors> floors = new ArrayList<Floors>();
		for (Floors f : Floors.values()) {
			floors.add(f);
		}
		setFloor(floors.get(Utils.random(floors.size())));
	}

	/**
	 * Retrieves a new {@code WorldTile} using the boundChunks of the dynamic
	 * region.
	 *
	 * @param mapX
	 *            The 'x' coordinate value.
	 * @param mapY
	 *            The 'y' coordinate value.
	 * @return a new {@code WorldTile}
	 */
	public WorldTile getWorldTile(int mapX, int mapY) {
		return new WorldTile(boundChunks[0] * 8 + mapX, boundChunks[1] * 8 + mapY, 0);
	}

	/**
	 * Removes the custom region from the game.
	 */
	public void removeRegion() {
		RegionBuilder.destroyMap(boundChunks[0], boundChunks[1], 20, 20);
	}

	public Floors getFloor() {
		return floor;
	}

	public void setFloor(Floors floor) {
		this.floor = floor;
	}

	/**
	 * The current floor we are on.
	 */
	private Floors floor;

	/**
	 * The bound chunks.
	 */
	private final int[] boundChunks = RegionBuilder.findEmptyChunkBound(20, 20);

	/**
	 * The enumeration of the floors that the team could go into. The floor
	 * will also randomize the tiles for which players land on, and the
	 * monster locations is based on those tiles.
	 *
	 * @author Tyluur
	 *
	 */
	public enum Floors {

		WHITE(
		new int[][] { { 14, 554 } }) {
			@Override
			public List<WorldTile> getFloorTiles() {
				List<WorldTile> tiles = new ArrayList<WorldTile>();
				int[][] array = new int[][] { { 7, 7 }, { 8, 22 }, { 40, 25 }, { 40, 8 }, };
				for (int[] element : array) {
					tiles.add(new WorldTile(element[0], element[1], 0));
				}
				return tiles;
			}
		},

		BLACK(
		new int[][] { { 10, 688 } }) {
			@Override
			public List<WorldTile> getFloorTiles() {
				List<WorldTile> tiles = new ArrayList<WorldTile>();
				int[][] array = new int[][] { { 7, 7 }, { 23, 7 }, { 24, 22 }, { 42, 22 }, };
				for (int[] element : array) {
					tiles.add(new WorldTile(element[0], element[1], 0));
				}
				return tiles;
			}
		},

		BLUE(
		new int[][] { { 44, 534 } }) {

			@Override
			public List<WorldTile> getFloorTiles() {
				List<WorldTile> tiles = new ArrayList<WorldTile>();
				int[][] array = new int[][] { { 7, 7 }, { 10, 23 }, { 7, 40 }, };
				for (int[] element : array) {
					tiles.add(new WorldTile(element[0], element[1], 0));
				}
				return tiles;
			}

		},

		DARK(
		new int[][] { { 14, 583 } }) {

			@Override
			public List<WorldTile> getFloorTiles() {
				List<WorldTile> tiles = new ArrayList<WorldTile>();
				int[][] array = new int[][] { { 7, 32 }, { 23, 32 }, { 23, 48 }, { 40, 48 }, };
				for (int[] element : array) {
					tiles.add(new WorldTile(element[0], element[1], 0));
				}
				return tiles;
			}

		};

		Floors(int[][] coords) {
			this.coords = coords;
		}

		private final int[][] coords;

		/**
		 * Generates a dynamic map region and sets the boundChunks values to
		 * that of the region.
		 * @param boundChunks The bound chunks of the region
		 */
		public void createFloor(int[] boundChunks) {
			for (int[] coord : coords) {
				RegionBuilder.copyAllPlanesMap(coord[0], coord[1], boundChunks[0], boundChunks[1], 20);
			}
		}

		/**
		 * The tiles in the floor
		 * @return
		 */
		public abstract List<WorldTile> getFloorTiles();
	}
}
