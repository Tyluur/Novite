package novite.rs.game.player.quests;

import java.io.Serializable;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 22, 2014
 */
public class QuestRequirement implements Serializable {

	public QuestRequirement(String name, boolean completed) {
		this.name = name;
		this.completed = completed;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the requirement
	 */
	public boolean isCompleted() {
		return completed;
	}

	private final String name;
	private final boolean completed;

	private static final long serialVersionUID = -3153997156053239836L;

}
