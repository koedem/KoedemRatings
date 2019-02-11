package Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import Calculation.Calculator;

public class RatingPoint {

	private Player player;
	private int    playerID;
	private int    ratingPeriod;
	private double rating;
	private double ratingChange;
	private double ratingDeviation;
	private double ratingDeviationChange;
	private double volatility;
	public double glickoTwoRating;
	public double glickoTwoRatingDeviation;
	
	public ArrayList<Game> games = new ArrayList<Game>();
	
	public RatingPoint(Player player, int ratingPeriod, double rating, double RD, double volatility) {
		this.ratingPeriod = ratingPeriod;
		this.rating = rating;
		this.ratingDeviation = RD;
		this.volatility = volatility;
		this.player = player;
		this.playerID = player.getIdentifier();
	}

	/**
	 * Transform the ratings to the Glicko2 scale as defined in
	 * <a href="http://www.glicko.net/glicko/glicko2.pdf">the paper on glicko2</a>, step 2.
	 */
	public void glickoIze() {
		glickoTwoRating = (rating - 1500) / Calculator.CONVERSION_FACTOR_2_1;
		glickoTwoRatingDeviation = ratingDeviation / Calculator.CONVERSION_FACTOR_2_1;
	}

	int getPlayerID() {
		return playerID;
	}

	Player getPlayer() {
		return player;
	}

	double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public double getRatingDeviation() {
		return ratingDeviation;
	}

	public void setRatingDeviation(double ratingDeviation) {
		this.ratingDeviation = ratingDeviation;
	}

	public double getVolatility() {
		return volatility;
	}

	public void setVolatility(double volatility) {
		this.volatility = volatility;
	}

	/**
	 * @return a copy of the given rating point with ratingPeriod incremented by one.
	 */
	public RatingPoint copyForNextRatingPeriod() {
		return new RatingPoint(player, ratingPeriod + 1, rating, ratingDeviation, volatility);
	}

	public void setRatingChange(double ratingChange) {
		this.ratingChange = ratingChange;
	}

	public void setRatingDeviationChange(double ratingDeviationChange) {
		this.ratingDeviationChange = ratingDeviationChange;
	}

	/**
	 * Insert a new row into the player table
	 *
	 */
	public void insertIntoDB(Connection conn) {
		String sql = "INSERT OR IGNORE INTO RatingPoint(playerID, ratingPeriod, rating, ratingDeviation, volatility, ratingChange, ratingDeviationChange) VALUES(?,?,?,?,?,?,?)";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, playerID);
			pstmt.setInt(2, ratingPeriod);
			pstmt.setDouble(3, rating);
			pstmt.setDouble(4, ratingDeviation);
			pstmt.setDouble(5, volatility);
			pstmt.setDouble(6, ratingChange);
			pstmt.setDouble(7, ratingDeviationChange);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void print() {
		System.out.println("Name: " + player.getLastName() + ", " + player.getFirstName() + ", Rating: " + String.format("%.2f", rating)
		                   + " ( " + String.format("%+.2f", ratingChange) + " ), RD: " + String.format("%.2f", ratingDeviation)
		                   + " ( " + String.format("%+.2f", ratingDeviationChange) + " ), volalility: " + String.format("%.4f", volatility));
	}
}
