import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JWindow;

public class ComputerPlayer {
	private static ArrayList<BufferedImage> tiles;
	public static Point topLeftCorner;
	static JWindow window;

	public static boolean play() throws IOException, AWTException, InterruptedException {
		if (tiles == null) {
			tiles = ReadScreen.findNewItems();

			Robot robot = new Robot();
			BufferedImage screen = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			topLeftCorner = ReadScreen.findTopLeftCorner(screen);
//			window = new JWindow();
//			window.setBounds(0, 300, 320, 320);
//			window.setVisible(true);
		}

		return play(tiles, topLeftCorner);
	}

	/**
	 * Plays one click
	 *
	 * @param tiles
	 */
	public static boolean play(ArrayList<BufferedImage> tiles, Point topLeftCorner) throws AWTException, InterruptedException {
		long tic = System.currentTimeMillis();
		int[][] matrix = ReadScreen.readScreen(tiles);
//		System.out.println(" - Reading screen took " + (System.currentTimeMillis() - tic));
//		printMatrix(matrix);

		// check verticals
		tic = System.currentTimeMillis();
		ArrayList<Point> points = getBestMatch(matrix, 4);
//		System.out.println(" - Finding best match took " + (System.currentTimeMillis() - tic));
		if ((points != null) && (points.size() == 2)) {
			//System.out.println("Playing " + points.get(0) + " - " + points.get(1));
			Point screenPointA = ReadScreen.translateMatrixToScreen(points.get(0), topLeftCorner);
			Point screenPointB = ReadScreen.translateMatrixToScreen(points.get(1), topLeftCorner);
			Robot robot = new Robot();
			robot.mouseMove(screenPointA.x, screenPointA.y);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			//	            Thread.sleep(150);
			robot.mouseMove(screenPointB.x, screenPointB.y);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			//System.out.println("played one move");
			return true;
		}

		System.out.println("no moves left");
		return false;
	}

	private static ArrayList<ArrayList<Point>> getAllMatches(int[][] matrix) {
		ArrayList<ArrayList<Point>> points = findVertical(matrix);
		points.addAll(findHorizontal(matrix));
		points.addAll(findMiddleHorizontal(matrix));
		points.addAll(findMiddleVertical(matrix));
		return points;
	}

	private static ArrayList<Point> getBestMatch(int[][] matrix, int lookAhead) {
		ArrayList<ArrayList<Point>> matches = getAllMatches(matrix);

		ArrayList<Point> bestPoints = null;
		double bestScore = 0;
		double bestNextScore = 0;

		for (ArrayList<Point> points : matches) {
			double score = determineQualityOfMatch(points, matrix);
			double nextScore = 0;

			if (lookAhead > 0) {
				int[][] hypo = createHypotheticalMatrix(points, matrix);
				ArrayList<Point> bestMatch = getBestMatch(hypo, lookAhead - 1);
				if ((bestMatch != null) && (bestMatch.size() == 2)) {
					simulateHit(hypo);
					nextScore = determineQualityOfMatch(bestMatch, hypo);
				}
			}

			if (bestPoints == null) {
				// init for sure
				bestScore = score;
				bestPoints = points;
			} else if (Math.max(score, nextScore) > Math.max(bestScore, bestNextScore)) {
				// score or next score has an absolute higher value
				bestScore = score;
				bestPoints = points;
			} else if ((score > bestScore) && (nextScore > bestNextScore)) {
				// everything is better
				bestScore = score;
				bestPoints = points;
			} else if ((nextScore >= bestScore) && (nextScore >= bestNextScore)) {
				// the next score trumps everything
				bestScore = score;
				bestPoints = points;
			}
		}

		return bestPoints;
	}

	/**
	 * Determines how many matches the move does. 4 items in a row are better than 1.
	 */
	private static double determineQualityOfMatch(ArrayList<Point> points, int[][] matrix) {
		int[][] hypoMatrix = createHypotheticalMatrix(points, matrix);


		return Math.max(determineQualityOfMatch(points.get(0), hypoMatrix),
				determineQualityOfMatch(points.get(1), hypoMatrix));
	}

	private static int[][] createHypotheticalMatrix(ArrayList<Point> points, int[][] matrix) {
		// create hypothetical matrix
		int[][] hypoMatrix = new int[8][8];
		multiArrayCopy(matrix, hypoMatrix);
		hypoMatrix[points.get(1).x][points.get(1).y] = matrix[points.get(0).x][points.get(0).y];
		hypoMatrix[points.get(0).x][points.get(0).y] = matrix[points.get(1).x][points.get(1).y];
		return hypoMatrix;
	}

	private static double determineQualityOfMatch(Point point, int[][] matrix) {
		int lengthLeft = 0;
		int lengthRight = 0;
		int lengthUp = 0;
		int lengthDown = 0;

		for (int column = point.x; column >= 0; column--) {
			if (matrix[column][point.y] == matrix[point.x][point.y]) {
				lengthLeft = point.x - column;
			} else {
				break;
			}
		}

		for (int column = point.x; column < 8; column++) {
			if (matrix[column][point.y] == matrix[point.x][point.y]) {
				lengthRight = column - point.x;
			} else {
				break;
			}
		}

		for (int row = point.x; row >= 0; row--) {
			if (matrix[point.x][row] == matrix[point.x][point.y]) {
				lengthUp = point.x - row;
			} else {
				break;
			}
		}

		for (int row = point.x; row < 8; row++) {
			if (matrix[point.x][row] == matrix[point.x][point.y]) {
				lengthDown = row - point.x;
			} else {
				break;
			}
		}

		if ((lengthLeft + lengthRight > 2) && (lengthUp + lengthDown > 2)) {
			return lengthLeft + lengthRight + lengthUp + lengthDown - 1;
		} else {
			return Math.max(lengthLeft + lengthRight, lengthUp + lengthDown);
		}
	}

	/**
	 * Finds vertical match
	 *
	 * @param matrix
	 * @return
	 */
	private static ArrayList<ArrayList<Point>> findVertical(int[][] matrix) {
		ArrayList<ArrayList<Point>> result = new ArrayList<ArrayList<Point>>();
		for (int row = 0; row < 7; row++) {
			for (int column = 0; column < 8; column++) {
				if (matrix == null) {
					// unrecognized field
					continue;
				}
				if (matrix[column][row] == -1) {
					// unrecognized field
					continue;
				}

				if (matrix[column][row] == matrix[column][row + 1]) {
					// bottom right
					if ((column < 7) && (row < 6) && (matrix[column][row] == matrix[column + 1][row + 2])) {
						//System.out.println("vertical: found bottom right");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column, row + 2));
						tmp.add(new Point(column + 1, row + 2));
						result.add(tmp);
					}

					// bottom left
					if ((column > 0) && (row < 6) && (matrix[column][row] == matrix[column - 1][row + 2])) {
						//System.out.println("vertical: found bottom left");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column, row + 2));
						tmp.add(new Point(column - 1, row + 2));
						result.add(tmp);
					}

					// top right
					if ((column < 7) && (row > 0) && (matrix[column][row] == matrix[column + 1][row - 1])) {
						//System.out.println("vertical: found top right");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column, row - 1));
						tmp.add(new Point(column + 1, row - 1));
						result.add(tmp);
					}

					// top left
					if ((column > 0) && (row > 0) && (matrix[column][row] == matrix[column - 1][row - 1])) {
						//System.out.println("vertical: found top left");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column, row - 1));
						tmp.add(new Point(column - 1, row - 1));
						result.add(tmp);
					}

					// bottom down
					if ((row < 5) && (matrix[column][row] == matrix[column][row + 3])) {
						//System.out.println("vertical: found bottom down");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column, row + 2));
						tmp.add(new Point(column, row + 3));
						result.add(tmp);
					}

					// top up
					if ((row > 1) && (matrix[column][row] == matrix[column][row - 2])) {
						//System.out.println("vertical: found top up");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column, row - 1));
						tmp.add(new Point(column, row - 2));
						result.add(tmp);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Finds horizontal match
	 */
	private static ArrayList<ArrayList<Point>> findHorizontal(int[][] matrix) {
		ArrayList<ArrayList<Point>> result = new ArrayList<ArrayList<Point>>();
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 7; column++) {
				if (matrix == null) {
					// unrecognized field
					continue;
				}
				if (matrix[column][row] == -1) {
					// unrecognized field
					continue;
				}

				if (matrix[column][row] == matrix[column + 1][row]) {
					// bottom right
					if ((column < 6) && (row < 7) && (matrix[column][row] == matrix[column + 2][row + 1])) {
						//System.out.println("found bottom right");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column + 2, row));
						tmp.add(new Point(column + 2, row + 1));
						result.add(tmp);
					}

					// bottom left
					if ((column > 0) && (row < 7) && (matrix[column][row] == matrix[column - 1][row + 1])) {
						//System.out.println("found bottom left");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column - 1, row));
						tmp.add(new Point(column - 1, row + 1));
						result.add(tmp);
					}

					// top right
					if ((column < 6) && (row > 0) && (matrix[column][row] == matrix[column + 2][row - 1])) {
						//System.out.println("found top right");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column + 2, row));
						tmp.add(new Point(column + 2, row - 1));
						result.add(tmp);
					}

					// top left
					if ((column > 0) && (row > 0) && (matrix[column][row] == matrix[column - 1][row - 1])) {
						//System.out.println("found top left");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column - 1, row));
						tmp.add(new Point(column - 1, row - 1));
						result.add(tmp);
					}

					// left out
					if ((column > 1) && (matrix[column][row] == matrix[column - 2][row])) {
						//System.out.println("left out");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column - 1, row));
						tmp.add(new Point(column - 2, row));
						result.add(tmp);
					}

					// right out
					if ((column < 5) && (matrix[column][row] == matrix[column + 3][row])) {
						//System.out.println("right out (column: " + column + " row: " + row + ")");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column + 2, row));
						tmp.add(new Point(column + 3, row));
						result.add(tmp);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Finds middle match
	 */
	private static ArrayList<ArrayList<Point>> findMiddleVertical(int[][] matrix) {
		ArrayList<ArrayList<Point>> result = new ArrayList<ArrayList<Point>>();
		for (int row = 0; row < 6; row++) {
			for (int column = 0; column < 8; column++) {
				if (matrix == null) {
					// unrecognized field
					continue;
				}
				if (matrix[column][row] == -1) {
					// unrecognized field
					continue;
				}

				if (matrix[column][row] == matrix[column][row + 2]) {
					// left
					if ((column > 0) && (matrix[column][row] == matrix[column - 1][row + 1])) {
						//System.out.println("left middle vertical");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column, row + 1));
						tmp.add(new Point(column - 1, row + 1));
						result.add(tmp);
					}

					// right
					if ((column < 7) && (matrix[column][row] == matrix[column + 1][row + 1])) {
						//System.out.println("right middle vertical");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column, row + 1));
						tmp.add(new Point(column + 1, row + 1));
						result.add(tmp);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Finds middle match
	 */
	private static ArrayList<ArrayList<Point>> findMiddleHorizontal(int[][] matrix) {
		ArrayList<ArrayList<Point>> result = new ArrayList<ArrayList<Point>>();
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 6; column++) {
				if (matrix == null) {
					// unrecognized field
					continue;
				}
				if (matrix[column][row] == -1) {
					// unrecognized field
					continue;
				}

				if (matrix[column][row] == matrix[column + 2][row]) {
					// up
					if ((row > 0) && (matrix[column][row] == matrix[column + 1][row - 1])) {
						//System.out.println("up middle horizontal");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column + 1, row));
						tmp.add(new Point(column + 1, row - 1));
						result.add(tmp);
					}

					// down
					if ((row < 7) && (matrix[column][row] == matrix[column + 1][row + 1])) {
						//System.out.println("down middle vertical");
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(new Point(column + 1, row));
						tmp.add(new Point(column + 1, row + 1));
						result.add(tmp);
					}
				}
			}
		}
		return result;
	}

	private static void printMatrix(int[][] matrix) {
		Graphics graph = window.getContentPane().getGraphics();
		if (matrix != null)
			for (int row = 0; row < 8; row++) {
				for (int column = 0; column < 8; column++) {
					System.out.print(matrix[column][row] + " ");
					if (matrix[column][row] != -1)
						graph.setColor(new Color(tiles.get(matrix[column][row]).getColorModel().getRed(((BufferedImage)tiles.get(matrix[column][row])).getRGB(1, 2)),
							tiles.get(matrix[column][row]).getColorModel().getGreen(((BufferedImage)tiles.get(matrix[column][row])).getRGB(1, 2)),
							tiles.get(matrix[column][row]).getColorModel().getBlue(((BufferedImage)tiles.get(matrix[column][row])).getRGB(1, 2))));
					else
						graph.setColor(Color.BLACK);
					graph.fillRect( column * 40, row * 40,40, 40);
				}
				System.out.println();
			}
		graph.create();
	}
	
//	private static void setColor(Graphics graph, int[] rgb) {
//		switch (rgb){
//		case 0: {
//			graph.setColor(new org.eclipse.swt.graphics.Color(device, rgb))
//			graph.setColor(Color.RED);
//			System.out.println("RED");
//			break;
//		}
//		case 1: {
//			graph.setColor(Color.BLUE);
//			System.out.println("BLUE");
//			break;
//		}
//		case 2: {
//			graph.setColor(Color.GREEN);
//			System.out.println("GREEN");
//			break;
//		}
//		case 3: {
//			graph.setColor(Color.GRAY);
//			System.out.println("GRAY");
//			break;
//		}
//		case 4: {
//			graph.setColor(Color.CYAN);
//			System.out.println("CYAN");
//			break;
//		}
//		case 5: {
//			graph.setColor(Color.LIGHT_GRAY);
//			System.out.println("LIGHT_GRAY");
//			break;
//		}
//		case 6: {
//			graph.setColor(Color.PINK);
//			System.out.println("PINK");
//			break;
//		}
//		default: {
//			graph.setColor(Color.BLACK);
//			System.out.println("BLACK");
//			break;
//		}
//
//		}
//	}

	public static void multiArrayCopy(int[][] source, int[][] destination) {
		for (int a = 0; a < source.length; a++) {
			System.arraycopy(source[a], 0, destination[a], 0, source[a].length);
		}
	}

	/**
	 * Clears the board for matching items.
	 * <p/>
	 * Mark nixed fields with -2.
	 */
	public static boolean simulateHit(int[][] matrix) {
		// catch horizontal
		int lastColor = -2;
		int lastColumn = -1;
		int length = 0;
		for (int column = 0; column < 6; column++) {
			for (int row = 0; row < 8; row++) {
				if (matrix[column][row] == lastColor) {
					length++;
				} else {
					// clear previous row, if hit
					if (length > 3) {
						for (int i = lastColumn; i < column; i++) {
							matrix[i][row] = -2;
						}
					}

					lastColor = matrix[column][row];
					lastColumn = column;
					length = 1;
				}
			}
			lastColor = -2;
			lastColumn = -1;
			length = 0;
		}

		// catch vertical
		lastColor = -2;
		int lastRow = -1;
		length = 0;
		for (int row = 0; row < 6; row++) {
			for (int column = 0; column < 8; column++) {
				if (matrix[column][row] == lastColor) {
					length++;
				} else {
					// clear previous row, if hit
					if (length > 3) {
						for (int i = lastRow; i < row; i++) {
							matrix[column][i] = -2;
						}
					}

					lastColor = matrix[column][row];
					lastRow = row;
					length = 1;
				}
			}
			lastColor = -2;
			lastRow = -1;
			length = 0;
		}

		// let everything drop down
		boolean dirty = false;
		for (int row = 7; row >= 0; row--) {
			for (int column = 7; column >= 0; column--) {
				if (matrix[column][row] == -2) {
					for (int i = row; i >= 0; i--) {
						matrix[column][i] = matrix[column][i - 1];
					}
					matrix[column][0] = 100 + new Random().nextInt(10000);
					dirty = true;
				}
			}
		}

		// recurse
		if (dirty) {
			simulateHit(matrix);
		}

		return dirty;
	}
}