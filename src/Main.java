import Calculation.Calculator;
import Data.Game;
import Data.Player;
import Data.RatingPoint;
import Data.Tournament;
import Database.AllData;
import Database.DBorganization;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	private AllData data = new AllData();
	private DBorganization db = new DBorganization();

	private int maxTournamentID = 0;
	private int currentRatingPeriod = 0;
	
	private final static Scanner input = new Scanner(System.in);
	
	public void main() {
		String command = "";
		while (!(command.equals("quit"))) {
			command = input.nextLine();
			String[] parts = command.split(" ");
			if (parts.length > 0) {
				if (command.startsWith("add player")) {
					addPlayer(parts);
				} else if (command.startsWith("add game")) {
					addGame(parts);
				} else if (command.equals("calculate")) {
					evaluation();
				} else if (parts[0].equals("load")) {
					data.load(parts[1], parts[2], db);
				} else if (parts[0].startsWith("store")) {
					data.store(parts[1], parts[2], db);
				} else if (command.equals("print")) {
					printPlayers();
				} else if (command.equals("print games")) {
					printGameDatabase();
				} else if (command.startsWith("add tournament")) {
					addTournament(parts);
				} else if (command.equals("help")) {
					printHelp();
				}
			}
		}
		db.closeConnection();
	}

	private void printHelp() {
		System.out.println("Legal commands and their usages:");
		System.out.println(" \"help\" : Prints this help.");
		System.out.println(" \"add player <first name> <last name> <start rating> <start rating deviation> <start rating volatility>\" :\n" +
		                   "    Adds the player to the player database and returns his identifier.\n" +
		                   "    If you don't know good values enter 1500.0 as start rating,\n" +
		                   "    350.0 as start rating deviation and 0.06 as start rating volatility.");
		System.out.println(" \"add game <rating period> <tournament ID> <round number> <winner with White pieces> <winner with Black pieces> <loser with Black pieces> " +
		                   "<loser with White pieces> \n<moves in game of White winner> <moves in game of Black winner> <seconds left White winner> <seconds left Black winner> " +
		                   "<seconds left Black loser> <seconds left White loser>\".");
		System.out.println(" \"add tournament <tournament name> <tournament system> <rating period> <start date> <end date> <rating bonus>\" :\n" +
		                   "    Adds a tournament to the tournament database. Usual tournament names are 'Open system' or 'Championship system'.\n" +
		                   "    Dates in the form of YYYY-MM-DD, e.g. '2019-02-23'.\n" +
		                   "    Rating bonus will be added to each participating players ratings, set to 0 if you don't want to add to play ratings.");
		System.out.println(" \"load <path-to-folder> <database name>\". Loads all data from a database. If the database does not exist it will be created.");
		System.out.println(" \"store <path-to-folder> <database name>\". Stores all data into an (if not yet existing, new) database.");
	}
	
	private void addPlayer(String[] parts) {
		Player newPlayer = new Player(data.player.size(), parts[2], parts[3]);
		RatingPoint ratingPoint = new RatingPoint(newPlayer, currentRatingPeriod, Double.parseDouble(parts[4]), Double.parseDouble(parts[5]), Double.parseDouble(parts[6]));
		data.player.add(newPlayer);
		data.ratingPoints.get(currentRatingPeriod).add(ratingPoint);
		newPlayer.print(ratingPoint);
	}

	private void addGame(String[] parts) {
		int ratingPeriod = Integer.parseInt(parts[2]);
		RatingPoint winnerOne = data.ratingPoints.get(ratingPeriod).get(Integer.parseInt(parts[5]));
		RatingPoint winnerTwo = data.ratingPoints.get(ratingPeriod).get(Integer.parseInt(parts[6]));
		RatingPoint loserOne = data.ratingPoints.get(ratingPeriod).get(Integer.parseInt(parts[7]));
		RatingPoint loserTwo = data.ratingPoints.get(ratingPeriod).get(Integer.parseInt(parts[8]));
		Game game = new Game(ratingPeriod, data.tournaments.get(Integer.parseInt(parts[3])), Integer.parseInt(parts[4]),
		                   winnerOne, winnerTwo, loserOne, loserTwo, Integer.parseInt(parts[9]), Integer.parseInt(parts[10]),
		                   Integer.parseInt(parts[11]), Integer.parseInt(parts[12]), Integer.parseInt(parts[13]), Integer.parseInt(parts[14]));
		data.gameDatabase.add(game);
		winnerOne.games.add(game);
		winnerTwo.games.add(game);
		loserOne.games.add(game);
		loserTwo.games.add(game);
	}

	/**
	 * This method sets up the calculation of the new ratings. First a new rating period is started and rating points
	 * get duplicated, games are linked to the correct rating points, then the actual calculation gets called.
	 */
	private void evaluation() {
		data.ratingPoints.add(new ArrayList<RatingPoint>()); // i.e. create new rating period
		for (RatingPoint rating : data.ratingPoints.get(currentRatingPeriod)) {
			rating.glickoIze();
			RatingPoint newRating = rating.copyForNextRatingPeriod();
			data.ratingPoints.get(currentRatingPeriod + 1).add(newRating); // put the copy in the next rating period
		}
		++currentRatingPeriod; // the old data stays as is, all calculations happen for the new period
		
		for (int player = 0; player < data.ratingPoints.get(currentRatingPeriod).size(); player++) {
			Calculator.calculate(data.ratingPoints.get(currentRatingPeriod - 1).get(player), data.ratingPoints.get(currentRatingPeriod).get(player));
			data.ratingPoints.get(currentRatingPeriod).get(player).print();
		}
	}
	
	private void printPlayers() {
		for (Player player : data.player) {
			player.print(data.ratingPoints.get(currentRatingPeriod).get(player.getIdentifier()));
		}
	}
	
	private void printGameDatabase() {
		for (Game game : data.gameDatabase) {
			game.print();
		}
	}

	private void addTournament(String[] parts) {
		data.tournaments.add(new Tournament(maxTournamentID, parts[2], parts[3], Integer.parseInt(parts[4]), LocalDate.parse(parts[5]),
		                               LocalDate.parse(parts[6]), Integer.parseInt(parts[7])));
		System.out.println("Tournament added with ID " + maxTournamentID);
		maxTournamentID++;
	}
}
