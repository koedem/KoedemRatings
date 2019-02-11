package Calculation;

import Data.Game;
import Data.Player;
import Data.RatingPoint;

public class Calculator {

	/**
	 * The system constant according to the Glicko paper.
	 */
	private static double tau = 0.5;
	
	public static final double CONVERSION_FACTOR_2_1 = 173.7178;

	/**
	 * Calculate the rating change of oldPoint according to the games of oldPoint and store the new rating in newPoint.
	 * @param oldPoint The pre-rating period rating point containing all games played in the rating period. The values in here won't be changed.
	 * @param newPoint The post-rating period rating point containing the new rating. The method assumes that newPoint is a (deep) copy of oldPoint
	 *                    i.e. already contains the pre rating period rating etc.
	 */
	public static void calculate(RatingPoint oldPoint, RatingPoint newPoint) {
		double phi = oldPoint.glickoTwoRatingDeviation;
		double sigma = oldPoint.getVolatility();
		if (oldPoint.games.size() != 0) {
			double v     = 0.0;
			double deltaOverV = 0.0;
			for (int i = 0; i < oldPoint.games.size(); i++) {
				Game    game   = oldPoint.games.get(i);
				boolean winner = oldPoint.equals(game.winnerOne) || oldPoint.equals(game.winnerTwo);
				double  my;
				double  myI;
				if (winner) { // the team rating gets calculated as the sum of the players ratings
					my = game.winnerOne.glickoTwoRating + game.winnerTwo.glickoTwoRating;
					myI = game.loserOne.glickoTwoRating + game.loserTwo.glickoTwoRating;
				} else {
					my = game.loserOne.glickoTwoRating + game.loserTwo.glickoTwoRating;
					myI = game.winnerOne.glickoTwoRating + game.winnerTwo.glickoTwoRating;
				}
				// The "opponent RD" is the 2-norm of all other players RDs. (including the players partner)
				double phiI = Math.sqrt(Math.pow(game.winnerOne.glickoTwoRatingDeviation, 2) + Math.pow(game.winnerTwo.glickoTwoRatingDeviation, 2) +
				                        Math.pow(game.loserOne.glickoTwoRatingDeviation, 2) + Math.pow(game.loserTwo.glickoTwoRatingDeviation, 2) -
				                        Math.pow(oldPoint.glickoTwoRatingDeviation, 2));
				double G = g(phiI);
				double e = E(my, myI, phiI);
				v += G * G * e * (1.0 - e);

				if (winner) {
					deltaOverV += G * (1 - e);
				} else {
					deltaOverV += G * (0 - e);
				}
			}
			v = 1.0 / v;

			double delta = deltaOverV * v;

			double vol = volatility(sigma, delta, phi, v);

			double phiStar = Math.sqrt(phi * phi + vol * vol);

			double phiPrime = 1 / Math.sqrt(1 / (phiStar * phiStar) + 1 / v);

			double ratingChange = deltaOverV * phiPrime * phiPrime; // This is the second part of step 7, the sum is simply delta over v from earlier.
			double myPrime = oldPoint.glickoTwoRating + ratingChange;
			newPoint.setRatingChange(CONVERSION_FACTOR_2_1 * ratingChange);
			newPoint.setRating(CONVERSION_FACTOR_2_1 * myPrime + 1500);
			newPoint.setRatingDeviation(CONVERSION_FACTOR_2_1 * phiPrime);
			newPoint.setVolatility(vol);
		} else {
			newPoint.setRatingDeviation(CONVERSION_FACTOR_2_1 * Math.sqrt(phi * phi + sigma * sigma));
		}
		newPoint.setRatingDeviationChange(newPoint.getRatingDeviation() - oldPoint.getRatingDeviation());
	}

	/**
	 * Function to calculate the new volatility as defined in <a href="http://www.glicko.net/glicko/glicko2.pdf">the paper on glicko2</a>, step 5.
	 * @param sigma
	 * @param delta
	 * @param phi
	 * @param v
	 * @return
	 */
	private static double volatility(double sigma, double delta, double phi, double v) {
		double A = Math.log(sigma * sigma);
		double B;
		if (delta * delta > phi * phi + v) {
			B = Math.log(delta * delta - phi * phi - v);
		} else {
			int k = 1;
			while (f(A - k * tau, sigma, delta, phi, v) < 0) {
				k++;
			}
			B = A - k * tau;
		}
		
		double fA = f(A, sigma, delta, phi, v);
		double fB = f(B, sigma, delta, phi, v);
		
		while(Math.abs(B - A) > 0.000001) {
			double C = A + (A - B) * fA / (fB - fA);
			double fC = f(C, sigma, delta, phi, v);
			if (fC * fB < 0) {
				A = B;
				fA = fB;
			} else {
				fA /= 2;
			}
			
			B = C;
			fB = fC;
		}
		return Math.exp(A / 2);
	}

	/**
	 * Function f as defined in <a href="http://www.glicko.net/glicko/glicko2.pdf">the paper on glicko2</a>, step 5.
	 */
	private static double f(double x, double sigma, double delta, double phi, double v) {
		double a = Math.log(sigma * sigma);
		
		return (Math.exp(x) * (delta * delta - phi * phi - v - Math.exp(x))) / (2 * (phi * phi + v + Math.exp(x)) 
				* (phi * phi + v + Math.exp(x))) - (x - a) / (tau * tau);
	}

	/**
	 * Function g as defined in <a href="http://www.glicko.net/glicko/glicko2.pdf">the paper on glicko2</a>, step 3.
	 * @param phi A rating deviation.
	 * @return g(phi) as defined above.
	 */
	private static double g(double phi) {
		return 1.0 / Math.sqrt(1.0 + 3.0 * phi * phi / (Math.PI * Math.PI));
	}

	/**
	 * Function E as defined in <a href="http://www.glicko.net/glicko/glicko2.pdf">the paper on glicko2</a>, step 3.
	 * @param my The players rating.
	 * @param myJ The players opponents rating.
	 * @param phiJ The players opponents RD.
	 * @return E(my, myJ, phiJ) as defined above.
	 */
	private static double E(double my, double myJ, double phiJ) {
		return 1.0 / (1.0 + Math.exp(-g(phiJ) * (my - myJ)));
	}
}
