package novite.rs.api.input;

/**
 *
 * @author Jonathan <jonathanbeaudoin1996@hotmail.com>
 * @since Jan 23, 2014
 */
public abstract class IntegerInputAction {

	/**
	 * Used to dynamically handle a integer input, saves time and is much
	 * cleaner than the traditional way of doing it
	 *
	 * @param input
	 */
	public abstract void handle(int input);

}
