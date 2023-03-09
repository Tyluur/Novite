package novite.rs.utility.game.npc.drops;

import java.util.List;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 31, 2014
 */
public class NPCDrop {

	public NPCDrop(String name, List<Drop> drops) {
		this.name = name;
		this.drops = drops;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the drops
	 */
	public List<Drop> getDrops() {
		return drops;
	}

	private final String name;
	private final List<Drop> drops;

}
