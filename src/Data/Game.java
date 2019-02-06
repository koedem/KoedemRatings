package Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Game {
	
	public RatingPoint winnerOne, winnerTwo, loserOne, loserTwo;
	public int gameOneMoves, gameTwoMoves, secondsLeftWinnerOne, secondsLeftWinnerTwo, secondsLeftLoserOne, secondsLeftLoserTwo;
	private Tournament tournament;
	private int ratingPeriod;
	private int        roundNumber;

	public void print() {
		// TODO implement
	}

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