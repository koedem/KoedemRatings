package Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class holds all data belonging to a game.
 */
public class Game {
	
	public RatingPoint winnerOne, winnerTwo, loserOne, loserTwo;
	private int gameOneMoves, gameTwoMoves, secondsLeftWinnerOne, secondsLeftWinnerTwo, secondsLeftLoserOne, secondsLeftLoserTwo;
	private Tournament tournament;
	private int ratingPeriod;
	private int        roundNumber;

	public void print() {
		System.out.println("White: " + winnerOne.getPlayer().getLastName() + ", " + winnerOne.getPlayer().getFirstName() + " ( " + String.format("%.2f",winnerOne.getRating())
		                   + " ), Seconds left: " + secondsLeftWinnerOne + "   1 - 0   " + loserOne.getPlayer().getLastName() + ", " + loserOne.getPlayer().getFirstName()
		                   + " ( " + String.format("%.2f",loserOne.getRating()) + " ) , Seconds left: " + secondsLeftLoserOne);
		System.out.println("Black: " + winnerTwo.getPlayer().getLastName() + ", " + winnerTwo.getPlayer().getFirstName() + " ( " + String.format("%.2f",winnerTwo.getRating())
		                   + " ), Seconds left: " + secondsLeftWinnerTwo + "   1 - 0   " + loserTwo.getPlayer().getLastName() + ", " + loserTwo.getPlayer().getFirstName()
		                   + " ( " + String.format("%.2f", loserTwo.getRating()) + " ) , Seconds left: " + secondsLeftLoserTwo + "\n");
	}

	/**
	 *
	 * @param ratingPeriod The rating period in which the game took place.
	 * @param tournament The tournament to which this game belongs.
	 * @param roundNumber The round in which the game was played.
	 * @param winnerOne The player of the winning team who had the White pieces. The rating points correspond to the pre tournament ratings.
	 * @param winnerTwo The player of the winning team who had the Black pieces.
	 * @param loserOne The player of the losing team who had the Black pieces.
	 * @param loserTwo The player of the losing team who had the White pieces.
	 * @param movesOne Number of moves played in the game between winner one and loser one.
	 * @param movesTwo Number of moves played in the game between winner two and loser two.
	 * @param secondsLeftWinnerOne Seconds left on the clock for winner one.
	 * @param secondsLeftWinnerTwo Seconds left on the clock for winner two.
	 * @param secondsLeftLoserOne Seconds left on the clock for loser one.
	 * @param secondsLeftLoserTwo Seconds left on the clock for loser two.
	 */
	public Game(int ratingPeriod, Tournament tournament, int roundNumber, RatingPoint winnerOne, RatingPoint winnerTwo,
	            RatingPoint loserOne, RatingPoint loserTwo, int movesOne, int movesTwo, int secondsLeftWinnerOne,
	            int secondsLeftWinnerTwo, int secondsLeftLoserOne, int secondsLeftLoserTwo) {
		this.ratingPeriod = ratingPeriod;
		this.tournament = tournament;
		this.roundNumber = roundNumber;
		this.winnerOne = winnerOne;
		this.winnerTwo = winnerTwo;
		this.loserOne = loserOne;
		this.loserTwo = loserTwo;
		this.gameOneMoves = movesOne;
		this.gameTwoMoves = movesTwo;
		this.secondsLeftWinnerOne = secondsLeftWinnerOne;
		this.secondsLeftWinnerTwo = secondsLeftWinnerTwo;
		this.secondsLeftLoserOne = secondsLeftLoserOne;
		this.secondsLeftLoserTwo = secondsLeftLoserTwo;
	}

	public void insertIntoDB(Connection conn) {
		String sql = "INSERT OR IGNORE INTO Game(winnerOne, winnerTwo, loserOne, loserTwo, tournamentID, round, ratingPeriod," +
		             " movesOne, movesTwo, secondsLeftWinnerOne, secondsLeftWinnerTwo, secondsLeftLoserOne," +
		             " secondsLeftLoserTwo) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, winnerOne.getPlayerID());
			pstmt.setInt(2, winnerTwo.getPlayerID());
			pstmt.setInt(3, loserOne.getPlayerID());
			pstmt.setInt(4, loserTwo.getPlayerID());
			pstmt.setInt(5, tournament.getTournamentID());
			pstmt.setInt(6, roundNumber);
			pstmt.setInt(7, ratingPeriod);
			pstmt.setInt(8, gameOneMoves);
			pstmt.setInt(9, gameTwoMoves);
			pstmt.setInt(10, secondsLeftWinnerOne);
			pstmt.setInt(11, secondsLeftWinnerTwo);
			pstmt.setInt(12, secondsLeftLoserOne);
			pstmt.setInt(13, secondsLeftLoserTwo);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}