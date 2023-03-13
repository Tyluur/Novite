package novite.rs.game.player.content.scrolls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import novite.rs.utility.tools.FileClassLoader;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 21, 2014
 */
public class ScrollSystem {

	public static void main(String[] args) {
		get().load();
		for (ClueScroll scroll : get().scrolls) {
			StringBuilder bldr = new StringBuilder();
			if (scroll.getHints() != null) {
				for (String s : scroll.getHints()) {
					bldr.append(s);
				}
			}
			System.out.println(scroll.getClass().getSimpleName() + ":\t" + (scroll.getInformationInterface() == null ? bldr.toString() : scroll.getInformationInterface()));
		}
	}

	/**
	 * Loads all clue scrolls into {@link #scrolls} map
	 */
	public void load() {
		for (Object clazz : FileClassLoader.getClassesInDirectory(ScrollSystem.class.getPackage().getName() + ".impl")) {
			scrolls.add((ClueScroll) clazz);
		}
		if (randomScrolls == null) {
			get().randomScrolls = new ArrayList<>(scrolls);
		}
	}

	/**
	 * Gets a random scroll from the {@link #scrolls} map of scrolls
	 * 
	 * @param type
	 *            The type of scroll to filter through
	 * @return
	 */
	public static ClueScroll getRandomScroll(ScrollType type) {
		synchronized (LOCK) {
			if (get().randomScrolls.size() > 0) {
				Collections.shuffle(get().randomScrolls);
				return get().randomScrolls.get(0);
			}
		}
		return null;
	}

	/**
	 * Gets the {@link #INSTANCE} of the class
	 * 
	 * @return
	 */
	public static ScrollSystem get() {
		return INSTANCE;
	}

	/**
	 * The object used to synchronize
	 */
	private static final Object LOCK = new Object();

	/**
	 * The list of all scrolls. This is populated the first time a user tries
	 * getting a random scroll
	 */
	private List<ClueScroll> randomScrolls;

	/**
	 * The map of clue scrolls the players can complete, identified by the type
	 * of scroll
	 */
	private List<ClueScroll> scrolls = new ArrayList<ClueScroll>();

	/**
	 * The instance of the clue scroll manager
	 */
	private static final ScrollSystem INSTANCE = new ScrollSystem();
}
