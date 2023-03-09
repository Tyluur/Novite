package novite.rs.game.player.content.slayer;

import java.util.ArrayList;
import java.util.List;

import novite.rs.cache.loaders.NPCDefinitions;

/**
 * 
 * @author Tyluur <itstyluur@gmail.com>
 * @since Dec 13, 2013
 */
public enum Tasks {

	EASY("Goblin", 81, 117, 18, 103, "Ghost", 1648, 1612), 
	MEDIUM(52, 125, 1643, 82, 1616, 1610, 112, "Bloodveld"), 
	HARD(1624, 1604, 9172, 1610, 1613, 1615, 2783, 55, 49, 84, 13820, 13821, 13822, 14696),
	ELITE("Ice strykewyrm", "Glacor", 50, 6260, 6203);

	/**
	 * The {@code Tasks} constructor. The parameters array will accept String
	 * and integers. The npc ids entered are transformed into a String for the
	 * task.
	 * 
	 * @param parameters
	 *            The parameters of the task. String or integers only.
	 */
	Tasks(Object... parameters) {
		this.parameters = parameters;
	}

	/**
	 * Gets the type of task this is.
	 * 
	 * @return
	 */
	public Type getType() {
		return Type.valueOf(name());
	}

	/**
	 * Creates a list of all of the names that can be generated from the
	 * parameters array
	 * 
	 * @return
	 */
	private final List<String> getNamesApplicable() {
		if (names == null) {
			loadProperties();
		}
		return names;
	}

	/**
	 * Loads all of the information into the list
	 */
	private void loadProperties() {
		names = new ArrayList<String>();
		for (Object object : parameters) {
			if (object instanceof String) {
				names.add((String) object);
			} else if (object instanceof Integer) {
				int id = (int) object;
				names.add(NPCDefinitions.getNPCDefinitions(id).getName());
			}
		}
	}

	/**
	 * Finds all of the tasks available for the type you are looking for
	 * 
	 * @param type
	 *            The type of task you want @see {@link Type}
	 * @return
	 */
	public static List<String> getTasksNames(Type type) {
		for (Tasks task : Tasks.values()) {
			if (task.name().equalsIgnoreCase(type.name())) {
				return task.getNamesApplicable();
			}
		}
		return null;
	}

	/**
	 * Looks for the task that would give the name
	 * 
	 * @param name
	 *            The name to look for
	 * @return A {@code Tasks} {@code Object}
	 */
	public static Tasks getTasksByName(String name) {
		for (Tasks tasks : Tasks.values()) {
			for (String names : tasks.getNamesApplicable()) {
				if (names.equalsIgnoreCase(name)) {
					return tasks;
				}
			}
		}
		return null;
	}

	/**
	 * The parameters applicable for this task type, can be an integer or string
	 * (id or name)
	 */
	private final Object[] parameters;

	private List<String> names;

}