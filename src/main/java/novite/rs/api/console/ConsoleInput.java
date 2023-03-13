package novite.rs.api.console;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 20, 2014
 */
public interface ConsoleInput {

	/**
	 * Gets the propable inputs that the input can be identified by
	 * 
	 * @return
	 */
	public String[] getPropableInputs();

	/**
	 * What to do when the input is handled
	 */
	public void onInput();
}
