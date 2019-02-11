package Data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * This class is the programming representation of a played tournament. It is uniquely identified by it's ID.
 */
public class Tournament {

	private int tournamentID;
	private int numberOfRounds = 0;
	private String tournamentName;
	private String tournamentSystem;
	private int ratingPeriod;
	private LocalDate startDate;
	private LocalDate endDate;
	private int ratingBonus;


	public void insertIntoDB(Connection conn) {
		String sql = "INSERT OR IGNORE INTO Tournament(id, name, system, rounds, ratingPeriod, startDate, endDate, bonusRating) VALUES(?,?,?,?,?,?,?,?)";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, tournamentID);
			pstmt.setString(2, tournamentName);
			pstmt.setString(3, tournamentSystem);
			pstmt.setInt(4, numberOfRounds);
			pstmt.setInt(5, ratingPeriod);
			pstmt.setDate(6, Date.valueOf(startDate));
			pstmt.setDate(7, Date.valueOf(endDate));
			pstmt.setInt(8, ratingBonus);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * @param tournamentID Unique identifier of the tournament.
	 * @param name The name of the tournament.
	 * @param system The tournament system.
	 * @param ratingPeriod In which rating period the tournament takes place. Usually the rating period which features the pre-tournament ratings.
	 * @param startDate Date when the first round of the tournament started.
	 * @param endDate Date when the last round of the tournament ended.
	 * @param ratingBonus This bonus gets added to every participating players rating at the end of the tournament.
	 */
	public Tournament(int tournamentID, String name, String system, int ratingPeriod, LocalDate startDate, LocalDate endDate, int ratingBonus) {
		this.tournamentID = tournamentID;
		this.tournamentName = name;
		this.tournamentSystem = system;
		this.ratingPeriod = ratingPeriod;
		this.startDate = startDate;
		this.endDate = endDate;
		this.ratingBonus = ratingBonus;
	}

	/**
	 * Sets the number of rounds in the tournament to the parameter if it is greater than the old value.
	 * @param numberOfRounds Positive integer.
	 */
	public void setRoundsIfGreater(int numberOfRounds) {
		if (numberOfRounds > this.numberOfRounds) {
			this.numberOfRounds = numberOfRounds;
		}
	}

	int getTournamentID() {
		return tournamentID;
	}
}
