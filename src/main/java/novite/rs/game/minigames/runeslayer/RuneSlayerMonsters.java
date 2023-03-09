package novite.rs.game.minigames.runeslayer;

import java.util.ArrayList;
import java.util.List;

import novite.rs.utility.Utils;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Dec 25, 2013
 */
public class RuneSlayerMonsters {

	public static int getBestRandomId(int floor, boolean boss) {
		if (!boss) {
			List<Integer> floors = Monsters.getBestByFloor(floor).getMonsters();
			if (floors == null) {
				return 1;
			}
			return floors.get(Utils.random(floors.size()));
		} else {
			Monsters monsters = Monsters.getBestByFloor(floor);
			if (monsters == null) {
				return 1;
			}
			return monsters.boss;
		}
	}

	public enum Monsters {

		BEGINNING(1, 9948) {
			@Override
			public List<Integer> getMonsters() {
				List<Integer> ids = new ArrayList<Integer>();
				ids.add(1648);
				ids.add(10797);
				ids.add(110);
				ids.add(2685);
				ids.add(90);
				ids.add(1633);
				ids.add(2025);
				ids.add(688);
				ids.add(13661);
				ids.add(2028);
				return ids;
			}
		},

		MEDIUM(5, 9948) {

			@Override
			public List<Integer> getMonsters() {
				List<Integer> ids = new ArrayList<Integer>();
				ids.add(49);
				ids.add(111);
				ids.add(82);
				ids.add(84);
				ids.add(10704);
				ids.add(52);
				ids.add(2028);
				ids.add(6206);
				return ids;
			}

		},

		ADVANCED(10, 9948) {

			@Override
			public List<Integer> getMonsters() {
				List<Integer> ids = new ArrayList<Integer>();
				ids.add(55);
				ids.add(6250);
				ids.add(6263);
				ids.add(7552);
				return ids;
			}

		};

		Monsters(int range, int boss) {
			this.range = range;
			this.boss = boss;
		}

		private final int range;
		private final int boss;

		public abstract List<Integer> getMonsters();

		public static Monsters getBestByFloor(int floor) {
			Monsters bestMonsters = Monsters.BEGINNING;
			int closestRange = Math.abs(bestMonsters.range - floor);
			for (Monsters monster : Monsters.values()) {
				int current = Math.abs(monster.range - floor);
				if (current < closestRange) {
					bestMonsters = monster;
					closestRange = current;
				}
			}
			return bestMonsters;
		}
	}

}
