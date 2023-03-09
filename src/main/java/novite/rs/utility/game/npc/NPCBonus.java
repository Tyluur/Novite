package novite.rs.utility.game.npc;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 21, 2014
 */
public class NPCBonus {

	public NPCBonus(int id, int[] bonuses) {
		this.id = id;
		this.bonuses = bonuses;
	}

	/**
	 * 1. stabatt(melee att)
	 * 2. slashatt(unused)
	 * 3. crushatt(unused)
	 * 4. magicatt
	 * 5. rangeatt
	 * 6. stabdef
	 * 7. slashdef
	 * 8. crushdef
	 * 9. magicdef
	 * 10. rangedef
	 *
	 * @return the bonuses
	 */
	public int[] getBonuses() {
		return bonuses;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	private final int id;
	private final int[] bonuses;

}
