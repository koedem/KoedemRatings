package Database;

import Data.Game;
import Data.Player;
import Data.RatingPoint;
import Data.Tournament;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 */
public class AllData {

	public ArrayList<Tournament>             tournaments  = new ArrayList<Tournament>();
	public ArrayList<Game>                   gameDatabase = new ArrayList<Game>();
	public ArrayList<Player>                 player       = new ArrayList<Player>();
	public ArrayList<ArrayList<RatingPoint>> ratingPoints = new ArrayList<>();

	// For now we only load players and games as to make new calculations possible. Loading games and tournaments as well is useful and will be added at some point. TODO
	public void load(String path, String name, DBorganization db) {
		db.setup(path, name);
		Connection conn = db.getConn();

		String playersql = "SELECT id, firstName, lastName " + "FROM Player\n " + "ORDER BY id ASC" ;
		try (PreparedStatement rpstmt  = conn.prepareStatement(playersql)) {
			ResultSet players = rpstmt.executeQuery();
			while (players.next()) {
				player.add(new Player(players.getInt("id"), players.getString("firstName"), players.getString("lastName")));
			}
			System.out.println("Successfully loaded players.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		String RPsql = "SELECT playerID, ratingPeriod, rating, ratingDeviation, volatility, ratingChange, ratingDeviationChange "
		               + "FROM RatingPoint\n" + "ORDER BY ratingPeriod ASC, playerID ASC" ;

		try (PreparedStatement rpstmt  = conn.prepareStatement(RPsql)){
			ResultSet ratingPoints = rpstmt.executeQuery();

			while (ratingPoints.next()) {
				// this is a bit of a hack, the player with ID 0 exists in every rating period and through the ordering will always appear first
				// i.e. we need a new rating period here
				if (ratingPoints.getInt("playerID") == 0) {
					this.ratingPoints.add(new ArrayList<>());
				}
				this.ratingPoints.get(ratingPoints.getInt("ratingPeriod")).add(new RatingPoint(ratingPoints.getInt("playerID"), ratingPoints.getInt("ratingPeriod"),
				        ratingPoints.getDouble("rating"), ratingPoints.getDouble("ratingDeviation"), ratingPoints.getDouble("volatility")));
			}
			if (this.ratingPoints.size() == 0) { // otherwise inserting a rating point later could lead to out of bounds exception
				this.ratingPoints.add(new ArrayList<>());
			}
			System.out.println("Sucessfully loaded rating points.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void store(String path, String name, DBorganization db) {
		db.setup(path, name);
		Connection conn = db.getConn();
		for (Player pl : player) {
			pl.insertIntoDB(conn);
		}
		for (ArrayList<RatingPoint> rperiod : ratingPoints) {
			for (RatingPoint rp : rperiod) {
				rp.insertIntoDB(conn);
			}
		}
		for (Tournament tn : tournaments) {
			tn.insertIntoDB(conn);
		}
		for (Game gm : gameDatabase) {
			gm.insertIntoDB(conn);
		}
	}
}
