package Database;

import java.io.File;
import java.sql.*;

/**
 * This class is responsible for interactions with the sqlite database.
 */
public class DBorganization {

	private Connection conn;
	private String url = "jdbc:sqlite:H:/Schach/Ködem/DB/";

	/**
	 * Create the database.
	 */
	private void createNewDatabase(String fileName) {

		String url = this.url + fileName;

		try (Connection conn = DriverManager.getConnection(url)) {
			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();
				System.out.println("Driver name: " + meta.getDriverName());
				System.out.println("A new database has been created.");
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Set up the required tables.
	 *
	 */
	private void setupTables(String fileName) {
		String url = this.url + fileName;

		String sql = "CREATE TABLE IF NOT EXISTS Player (\n"
		             + "	id integer PRIMARY KEY,\n"
		             + "	firstName text NOT NULL,\n"
		             + "	lastName text NOT NULL"
		             + ");";

		try (Connection conn = DriverManager.getConnection(url);
		     Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		sql = "CREATE TABLE IF NOT EXISTS RatingPoint (\n"
		      + "	playerID integer NOT NULL,\n"
		      + "	ratingPeriod integer NOT NULL,\n"
		      + "	rating double NOT NULL,\n"
		      + "	ratingDeviation double NOT NULL,\n"
		      + "   volatility double NOT NULL,\n"
		      + "	ratingChange double,\n"
		      + "	ratingDeviationChange double,\n"
		      + "    FOREIGN KEY (playerID) REFERENCES Player(id),\n"
		      + "    PRIMARY KEY (playerID, ratingPeriod)"
		      + ");";

		try (Connection conn = DriverManager.getConnection(url);
		     Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		sql = "CREATE TABLE IF NOT EXISTS Game (\n"
		      + "   winnerOne integer NOT NULL,\n"
		      + "   winnerTwo integer NOT NULL,\n"
		      + "   loserOne integer NOT NULL,\n"
		      + "   loserTwo integer NOT NULL,\n"
		      + "   tournamentID integer NOT NULL,\n"
		      + "   round integer NOT NULL,\n"
		      + "   ratingPeriod integer NOT NULL,\n"
		      + "   movesOne integer,\n"
		      + "   movesTwo integer,\n"
		      + "   secondsLeftWinnerOne integer,\n"
		      + "   secondsLeftWinnerTwo integer,\n"
		      + "   secondsLeftLoserOne integer,\n"
		      + "   secondsLeftLoserTwo integer,\n"
		      + "   FOREIGN KEY (winnerOne) REFERENCES Player(id),\n"
		      + "   FOREIGN KEY (winnerTwo) REFERENCES Player(id),\n"
		      + "   FOREIGN KEY (loserOne) REFERENCES Player(id),\n"
		      + "   FOREIGN KEY (loserTwo) REFERENCES Player(id),\n"
		      + "   PRIMARY KEY (winnerOne, tournamentID, round)"
		      + ");";

		try (Connection conn = DriverManager.getConnection(url);
		     Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		sql = "CREATE TABLE IF NOT EXISTS Tournament (\n"
		      + "   id integer NOT NULL,\n"
		      + "   name text NOT NULL,\n"
		      + "   system text,\n"
		      + "   rounds integer,\n"
		      + "   ratingPeriod integer NOT NULL,\n"
		      + "   startDate date,\n"
		      + "   endDate date,\n"
		      + "   bonusRating integer,\n"
		      + "   PRIMARY KEY (id)"
		      + ");";

		try (Connection conn = DriverManager.getConnection(url);
		     Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	/**
	 * Connect to the database
	 *
	 * @return the Connection object
	 */
	private Connection connect(String fileName) {
		// SQLite connection string
		String url = this.url + fileName;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	void setup(String path, String dbName) {
		url = "jdbc:sqlite:".concat(path);
		if (!(url.endsWith(File.separator))) {
			url = url.concat(File.separator);
		}
		createNewDatabase(dbName);
		setupTables(dbName);
		if (conn != null) {
			closeConnection(); // close old connection first
		}
		conn      = this.connect(dbName);
	}

	Connection getConn() {
		return conn;
	}

	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
