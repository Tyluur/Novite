package novite.rs.game.player.dialogues;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Oct 25, 2013
 */
public enum ChatAnimation {

	/**
	 * Not talking just listening
	 */
	LISTENING(
	9804),

	/**
	 * Plain talking slight head movement
	 */
	PLAIN(
	9808),

	/**
	 * Full of himself, eyes rolling
	 */
	SNOBBY(
	9832),

	/**
	 * Eyes darting back and forth, not sure
	 */
	UNSURE(
	9836),

	/**
	 * Listens then laughs a lot
	 */
	LISTEN_LAUGH(
	9840),

	/**
	 * Head swaying from left to right while talking
	 */
	SWAYING(
	9844),

	/**
	 * Smiling and talking
	 */
	NORMAL(
	9847),

	/***
	 * Laughing histerically
	 */
	LAUGHING(
	9851),

	/**
	 * Sad and talking
	 */
	SAD(
	9760),

	/**
	 * Very upset and talking. Huge frown
	 */
	CRYING(
	9765),

	/**
	 * "Why, why?"
	 */
	WHY(
	9776),

	/**
	 * Angry and talking
	 */
	ANGRY(
	9788),

	/**
	 * Extremely furious and talking
	 */
	FURIOUS(
	9792),

	/**
	 * Thinking and talking
	 */
	THINKING(
	9827);

	ChatAnimation(int animation) {
		this.animation = animation;
	}

	public int getAnimation() {
		return animation;
	}

	private final int animation;

}
