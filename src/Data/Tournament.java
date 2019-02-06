package Data;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 *
 */
public class Tournament implements Serializable {

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

	public Tournament(int tournamentID, String name, String system, int ratingPeriod, LocalDate startDate, LocalDate endDate, int ratingBonus) {
		this.tournamentID = tournamentID;
		this.tournamentName = name;
		this.tournamentSystem = system;
		this.ratingPeriod = ratingPeriod;
		this.startDate = startDate;
		this.endDate = endDate;
		this.ratingBonus = ratingBonus;
	}

	public void setRoundsIfGreater(int numberOfRounds) {
		if (numberOfRounds > this.numberOfRounds) {
			this.numberOfRounds = numberOfRounds;
		}
	}

	public int getTournamentID() {
		return tournamentID;
	}
}
