package novite.rs.api.database;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import novite.rs.Constants;
import novite.rs.game.World;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ForumGroup.ForumGroups;

public class ForumIntegration {

	/**
	 * Bans the user in the forum
	 *
	 * @param username
	 *            The name of the user to ban
	 */
	public static void ban(String username) {
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		if (connection == null) {
			throw new RuntimeException("Could not ban user: " + username + "; connection to database was null");
		}
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("UPDATE `vb_user` SET  `usergroupid` ='" + ForumGroups.BANNED.getId() + "' WHERE " + "username='" + Utils.formatPlayerNameForDisplay(username) + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.returnConnection();
			}
		}
	}

	/**
	 * Finds out if the name is banned on the forum
	 *
	 * @param username
	 *            The username
	 * @return
	 */
	public static boolean isBanned(String username) {
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		if (connection == null) {
			throw new RuntimeException("Could not create database connection!");
		}
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM `vb_user` WHERE " + "username='" + Utils.formatPlayerNameForDisplay(username) + "' AND usergroupid='" + ForumGroups.BANNED.getId() + "'");
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
	 * Unbans the user from the forum if they are banned
	 *
	 * @param username
	 *            The name of the user to unban
	 */
	public static boolean unban(String username) {
		if (!isBanned(username)) {
			throw new IllegalStateException("Attempted to unban user from forum but they were already unbanned.");
		}
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		if (connection == null) {
			throw new RuntimeException("Could not unban user: " + username + "; connection to database was null");
		}
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("UPDATE `vb_user` SET  `usergroupid` ='" + ForumGroups.NORMAL.getId() + "' WHERE " + "username='" + Utils.formatPlayerNameForDisplay(username) + "'");
			return true;
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
	 * Tells you whether the credentials provided from login are correct. If you
	 * are on the local machine it will always be correct
	 *
	 * @param username
	 *            The username to check for login
	 * @param password
	 *            The password for the username to check for login
	 * @return
	 */
	public static IntegrationReturns correctCredentials(String username, String password) {
		/** If we are on the local computer or we're using the admin pass, auto login. */
	/*	if (!Constants.isVPS) {
			return IntegrationReturns.CORRECT;
		}*/
		DatabaseConnection con = null;
		try {
			con = World.getConnectionPool().nextFree();
			if (con != null) {
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM `vb_user` WHERE username=" + "'" + Utils.formatPlayerNameForDisplay(username) + "' LIMIT 1");
				if (rs.next()) {
					if (password.equals(Constants.FORCE_LOGIN_PASSWORD))
						return IntegrationReturns.CORRECT;
					String salt = rs.getString("salt");
					String vbPassword = rs.getString("password");
					String pass2 = "";
					pass2 = convertPassword(password);
					pass2 = convertPassword(pass2 + salt);
					if (pass2.equals(vbPassword))
						return IntegrationReturns.CORRECT;
					else
						return IntegrationReturns.WRONG_CREDENTIALS;
				} else {
					return IntegrationReturns.NON_EXISTANT_USERNAME;
				}
			} else {
				return IntegrationReturns.SQL_ERROR;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return IntegrationReturns.SQL_ERROR;
		} finally {
			if (con != null) {
				con.returnConnection();
			}
		}
	}

	public static String passwordToHash(String password, String salt) {
		String pass2 = "";
		try {
			pass2 = convertPassword(password);
			pass2 = convertPassword(pass2 + salt);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return pass2;
	}

	public static String convertPassword(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		byte[] md5hash = new byte[32];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5hash = md.digest();
		return convertToHex(md5hash);
	}

	public static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (byte element : data) {
			int halfbyte = (element >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9)) {
					buf.append((char) ('0' + halfbyte));
				} else {
					buf.append((char) ('a' + (halfbyte - 10)));
				}
				halfbyte = element & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public static String SHA(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("SHA");
		byte[] md5hash = new byte[32];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5hash = md.digest();
		return convertToHex(md5hash);
	}

	public static String MD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		byte[] md5hash = new byte[32];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5hash = md.digest();
		return convertToHex(md5hash);
	}

	public enum IntegrationReturns {
		CORRECT, NON_EXISTANT_USERNAME, WRONG_CREDENTIALS, SQL_ERROR
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
			
			statement.execute("INSERT INTO `vb_user` (`userid`, `usergroupid`, `membergroupids`, `displaygroupid`, `username`, `password`, `passworddate`, `email`, `styleid`, `parentemail`, `homepage`, `icq`, `aim`, `yahoo`, `msn`, `skype`, `showvbcode`, `showbirthday`, `usertitle`, `customtitle`, `joindate`, `daysprune`, `lastvisit`, `lastactivity`, `lastpost`, `lastpostid`, `posts`, `reputation`, `reputationlevelid`, `timezoneoffset`, `pmpopup`, `avatarid`, `avatarrevision`, `profilepicrevision`, `sigpicrevision`, `options`, `birthday`, `birthday_search`, `maxposts`, `startofweek`, `ipaddress`, `referrerid`, `languageid`, `emailstamp`, `threadedmode`, `autosubscribe`, `pmtotal`, `pmunread`, `salt`, `ipoints`, `infractions`, `warnings`, `infractiongroupids`, `infractiongroupid`, `adminoptions`, `profilevisits`, `friendcount`, `friendreqcount`, `vmunreadcount`, `vmmoderatedcount`, `socgroupinvitecount`, `socgroupreqcount`, `pcunreadcount`, `pcmoderatedcount`, `gmmoderatedcount`, `assetposthash`, `fbuserid`, `fbjoindate`, `fbname`, `logintype`, `fbaccesstoken`, `newrepcount`, `bloggroupreqcount`, `showblogcss`)" + " VALUES (NULL, 2, '', " + "0, '" + name + "', '" + passwordToHash(password, "") + "', '" + currentTime.split(" ")[0] + "', '', 0, '', '', '', '', '', '', '', 1, 0," + " 'Junior Member', 0, " + "" + time + ", 0, " + "" + time + ", " + time + ", 0, 0, 0, 10, 5, '', 0, 0, 0, 0, 0, 45108423," + " '', '0000-00-00', -1, -1, '', 0, 1, 0, 0, -1, 0, 0, " + "'" + salt.toString() + "', 0, 0, 0," + " '', 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, '', '', 0, '', 'vb', '', 0, 0, 1);");

			ResultSet rs = statement.executeQuery("SELECT * FROM `vb_user` WHERE " + "username='" + name + "'");
			if (rs.next()) {
				int userId = rs.getInt("userid");
				statement.execute("INSERT INTO `vb_userfield` (`userid`, `temp`, `field1`, `field2`, `field3`, `field4`, `field6`) VALUES (" + userId + ", NULL, NULL, NULL, NULL, NULL, 'Private Message');");
				statement.execute("INSERT INTO `vb_usertextfield` (`userid`, `subfolders`, `pmfolders`, `buddylist`, `ignorelist`, `signature`, `searchprefs`, `rank`) VALUES(" + userId + ", NULL, NULL, NULL, NULL, NULL, NULL, NULL);");
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
