package novite.rs.utility.game;


/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since 2012-11-29
 */

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Jul 12, 2013
 */
public class ForumGroup {

	public ForumGroup(GroupType type, ForumGroups group) {
		this.type = type;
		this.group = group;
	}

	/**
	 * @return the groupId
	 */
	public ForumGroups getGroup() {
		return group;
	}

	/**
	 * @return the type
	 */
	public GroupType getType() {
		return type;
	}

	private final ForumGroups group;
	private final GroupType type;
	
	@Override
	public String toString() {
		return "ForumGroup[type=" + type + ", group=" + group + "]";
	}

	public enum ForumGroups {

		OWNER(6),

		ADMINISTRATOR(5),

		GLOBAL_MODERATOR(7),

		SERVER_MODERATOR(10),

		FORUM_MODERATOR(9),

		REGULAR_DONATOR(11),

		SUPER_DONATOR(12),

		EXTREME_DONATOR(13),
		
		SUPPORT(14),

		NORMAL(2),

		BANNED(8);

		ForumGroups(int id) {
			this.id = id;
		}

		private final int id;
		
		/**
		 * @return The forum group user id.
		 */
		public int getId() {
			return id;
		}

		public static ForumGroups getGroup(int id) {
			for (ForumGroups group : ForumGroups.values()) {
				if (group.getId() == id)
					return group;
			}
			return null;
		}

	}
	
	public enum GroupType {
		MAIN, SECONDARY
	}

}