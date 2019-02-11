package Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class contains the player data like player-id and name. Ratings are stored seperately in rating point objects.
 */
public class Player {

	private int identifier;
	private String firstName;
	private String lastName;

	public Player(int identifier, String firstName, String lastName) {
		this.identifier = identifier;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public int getIdentifier() {
		return identifier;
	}

	public void insertIntoDB(Connection conn) {
		String sql = "INSERT OR IGNORE INTO Player(id, firstName, lastName) VALUES(?,?,?)";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, identifier);
			pstmt.setString(2, firstName);
			pstmt.setString(3, lastName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void print(RatingPoint ratingPoint) {
		System.out.println("PlayerID: " + identifier + " , Name: " + lastName + ", "
		                   + firstName + " Rating: " + String.format("%.2f",ratingPoint.getRating()) + " ( RD: "
		                   + String.format("%.2f",ratingPoint.getRatingDeviation()) + " )");
	}

	String getFirstName() {
		return firstName;
	}

	String getLastName() {
		return lastName;
	}
}
