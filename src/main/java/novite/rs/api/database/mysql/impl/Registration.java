package novite.rs.api.database.mysql.impl;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import novite.rs.api.database.DatabaseConnection;
import novite.rs.api.database.ForumIntegration;
import novite.rs.game.World;
import novite.rs.utility.Utils;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Jul 27, 2013
 */
public class Registration {

	/**
	 * Finds out if the username entered exists in the database.
	 *
	 * @param username
	 *            The username to look for
	 * @return
	 */
	public static boolean registered(String username) {
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		try {
			username = Utils.formatPlayerNameForDisplay(username);
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM `user` WHERE " + "username='" + username + "'");
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.returnConnection();
			}
		}
		return false;
	}

	/**
	 * Registers a player into the database.
	 */
	public static void registerUser(String name, String password) {
		name = Utils.formatPlayerNameForDisplay(name);
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		try {
			Statement statement = connection.createStatement();
			Date dt = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(dt);
			int time = (int) (System.currentTimeMillis() / 1000L);
			StringBuilder salt = new StringBuilder();
			salt.append("");
			statement.execute("INSERT INTO `user` (`userid`, `usergroupid`, `membergroupids`, `displaygroupid`, `username`, `password`, `passworddate`, `email`, `styleid`, `parentemail`, `homepage`, `icq`, `aim`, `yahoo`, `msn`, `skype`, `showvbcode`, `showbirthday`, `usertitle`, `customtitle`, `joindate`, `daysprune`, `lastvisit`, `lastactivity`, `lastpost`, `lastpostid`, `posts`, `reputation`, `reputationlevelid`, `timezoneoffset`, `pmpopup`, `avatarid`, `avatarrevision`, `profilepicrevision`, `sigpicrevision`, `options`, `birthday`, `birthday_search`, `maxposts`, `startofweek`, `ipaddress`, `referrerid`, `languageid`, `emailstamp`, `threadedmode`, `autosubscribe`, `pmtotal`, `pmunread`, `salt`, `ipoints`, `infractions`, `warnings`, `infractiongroupids`, `infractiongroupid`, `adminoptions`, `profilevisits`, `friendcount`, `friendreqcount`, `vmunreadcount`, `vmmoderatedcount`, `socgroupinvitecount`, `socgroupreqcount`, `pcunreadcount`, `pcmoderatedcount`, `gmmoderatedcount`, `assetposthash`, `fbuserid`, `fbjoindate`, `fbname`, `logintype`, `fbaccesstoken`, `newrepcount`, `bloggroupreqcount`, `showblogcss`)" + " VALUES (NULL, 2, '', " + "0, '" + name + "', '" + ForumIntegration.passwordToHash(password, "") + "', '" + currentTime.split(" ")[0] + "', '', 0, '', '', '', '', '', '', '', 1, 0," + " 'Junior Member', 0, " + "" + time + ", 0, " + "" + time + ", " + time + ", 0, 0, 0, 10, 5, '', 0, 0, 0, 0, 0, 45108423," + " '', '0000-00-00', -1, -1, '', 0, 1, 0, 0, -1, 0, 0, " + "'" + salt.toString() + "', 0, 0, 0," + " '', 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, '', '', 0, '', 'vb', '', 0, 0, 1);");

			ResultSet rs = statement.executeQuery("SELECT * FROM `user` WHERE " + "username='" + name + "'");

			if (rs.next()) {
				int userId = rs.getInt("userid");
				statement.execute("INSERT INTO `userfield` (`userid`, `temp`, `field1`, `field2`, `field3`, `field4`) VALUES (" + userId + ", NULL, NULL, NULL, NULL, NULL);");
				statement.execute("INSERT INTO `usertextfield` (`userid`, `subfolders`, `pmfolders`, `buddylist`, `ignorelist`, `signature`, `searchprefs`, `rank`) VALUES (" + userId + ", NULL, NULL, NULL, NULL, NULL, NULL, '<img src=\"http://mazonic.com/images/ranks/Member.png\">');");
			}
			System.out.println("Completed registering a brand new user [" + name + "]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.returnConnection();
			}
		}
	}

}
