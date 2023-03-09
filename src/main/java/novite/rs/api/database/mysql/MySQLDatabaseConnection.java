package novite.rs.api.database.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;

import novite.rs.api.database.DatabaseConnection;

/**
 * An implementation of a <code>DatabaseConnection</code> which represents a
 * MySQL Connection
 *
 * @author Nikki
 *
 */
public class MySQLDatabaseConnection extends DatabaseConnection {

	/**
	 * Static constructor which loads our driver
	 */
	static {
		try {
			loadDriver("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a database connection instance
	 *
	 * @param configuration
	 *            The database configuration
	 */
	public MySQLDatabaseConnection(MySQLDatabaseConfiguration configuration) {
		super(configuration);
	}

	/**
	 * Connect to the database
	 */
	@Override
	public boolean connect() {
		try {
			if (connection == null) {
				MySQLDatabaseConfiguration configuration = (MySQLDatabaseConfiguration) this.configuration;
				connection = DriverManager.getConnection("jdbc:mysql://" + configuration.getHost() + ":" + configuration.getPort() + "/" + configuration.getDatabase(), configuration.getUsername(), configuration.getPassword());
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}