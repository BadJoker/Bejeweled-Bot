import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class ReadScreen {
	private static Rectangle optimizedRectangle = null;
	static final int red = -189640;
    static final int redfire = -123334;
    static final int redSpark = -25391;
    static final int redSpark2 = -28733;
    static final int redSpark3 = -25390;
    static final int redfire2 = -123591;
    static final int redMulti1 = -1896418;
    static final int redMulti3 = -1306594;
    static final int redMulti4 = -5567466;
    //static final int redMulti5 = -67804;
    
    static final int blue = -15626242;
    static final int bluefire = -15429377;
    static final int bluefire2 = -15363586;
    static final int blueSpark = -10818817;
    static final int blueSpark2 = -10950913;
    static final int blueSpark3 = -12727297;
    static final int blueSpark4 = -986896;
    static final int blueMulti1 = -14185501;
    static final int blueMulti2 = 14118421;
    static final int blueMulti3 = 14118422;
    static final int blueMulti4 = -14849621;

    static final int green = -15619548;
    static final int greenfire = -15625985;
    static final int greenfire2 = -15488219;
    static final int greenSpark = -15291096;
    static final int greenSpark2 = -8585273;
    static final int greenSpark3 = -8060976;
    static final int greenMulti2 = -16732405;
    static final int greenMulti3 = -16743416;
    static final int greenMulti4 = -12900265;
    static final int greenMulti5 = -16748281;
   
    
    static final int white = -1447447;
    static final int whitefire = -1052689;
    static final int whitefire2 = -1381655;
    static final int whiteSpark = -1;
    static final int whiteMulti = -4342339;
    static final int whiteMulti4 = -7368817;
    static final int whiteMulti6 = -8750470;
    //12128899
    static final int orange = -1086684;
    static final int orangefire = -1019864;
    static final int orangeSpark = -18049;
    static final int orangeMulti = -1871577;
    static final int orangeMulti2 = 361430;
    

    static final int yellow = -67804;
    static final int yellowfire = -1447447;
    static final int yellowfire2 = -2010;
    static final int yellowfire3 = -2011;
    static final int yellowcoin = -3235545;
    static final int yellowSpark = -66;
    static final int yellowSpark2 = -171;
    static final int yellowSpark3 = -59;
    static final int yellowSpark4 = -126;
    static final int yellowMulti = -2303744;
    static final int yellowMulti4 = -5856512;

    static final int purple = -519944;
    static final int purplefire = -519176;
    static final int purplefire2 = -452616;
    static final int purpleSpark = 41985;
    static final int purpleSpark2 = -35073;
    static final int purpleSpark3 = -29185;
    static final int purpleSpark4 = -28929;
    static final int purpleSpark5 = -26881;
    static final int purpleMulti = -7074669;
    static final int purpleMulti2 = -3927869;
    static final int purpleMulti3 = -3403317;
    static final int purpleMulti5 = -8516994;

    
    static final int multi = -12641758;
    static final int multi2 = -15726569;
    static final int multi3 = -1579031;
    static final int multi4 = -15685084;
    static final int multi5 = -14865870;
    static final int multi6 = -13885155;


	public static ArrayList<BufferedImage> findNewItems() throws AWTException, IOException {
		// get screen shot
		Robot robot = new Robot();
		BufferedImage screen = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

		//System.out.println(screen.getRGB(777, 452) + "" + (new Integer(-5145147).equals(screen.getRGB(777, 452))));
		//System.out.println(screen.getRGB(777, 453));
		//System.out.println(screen.getRGB(777, 454));


		// find images
		Point start = findTopLeftCorner(screen);
		if (start == null) {
			// failed
			//System.out.println("Couldn't find Bejeweled");
			return null;
		}

		// cut tiles out
		ArrayList<BufferedImage> tiles = new ArrayList<BufferedImage>();
		for (int column = 0; column < 8; column++) {
			for (int row = 0; row < 8; row++) {
				BufferedImage wholeTile = screen.getSubimage(start.x + (column * 40), start.y + (row * 40), 40, 40);
				BufferedImage tile = wholeTile.getSubimage(18, 18, 4, 4);
				Color color1 = new Color(tile.getColorModel().getRed(tile.getRGB(1, 2)),
						tile.getColorModel().getGreen(tile.getRGB(1, 2)),
						tile.getColorModel().getBlue(tile.getRGB(1, 2)));
//				System.out.println("color1=" + color1.getRGB());
				if (!containsImage(tiles, tile)) {
					tiles.add(tile);
				}
			}
		}

		// save tiles
		String path = "c:/stuff/bejeweled/";
		if (!new File(path).exists()) {
			new File(path).mkdir();
		}
		for (BufferedImage tile : tiles) {
			ImageIO.write(tile, "png", new File(path + tiles.indexOf(tile) + ".png"));
		}

		//System.out.println("done processing new tiles");
		return tiles;
	}

	public static Point findTopLeftCorner(BufferedImage screen) {
		Point start = null;
//		File file = new File("c:/stuff/bejeweled/blau_normal.png");
//		File file2 = new File("c:/stuff/bejeweled/blau_feuer.png");
//		File file3 = new File("c:/stuff/bejeweled/blau_blitz.png");
//		try {
//			BufferedImage image = ImageIO.read(file);
//			BufferedImage image2 = ImageIO.read(file2);
//			BufferedImage image3 = ImageIO.read(file3);
//			for (int a = 0; a < image.getWidth(); a++) {
//				for (int b =0; b < image.getHeight() ; b++) {
//					System.out.println("x=" + a + ", y=" + b + "Color=" + image.getRGB(a, b));
//					System.out.println("x=" + a + ", y=" + b + "Color=" + image2.getRGB(a, b));
//					System.out.println("x=" + a + ", y=" + b + "Color=" + image3.getRGB(a, b));
////					for (int d = 0; d < image.getWidth(); d++) {
////						for (int e =0; e < image.getHeight() ; e++) {
////							if (image.getRGB(a, b) == image2.getRGB(d, e))
////							for (int f = 0; f < image.getWidth(); f++) {
////								for (int g =0; g < image.getHeight() ; g++) {
////					if (image.getRGB(a, b) == image3.getRGB(f, g)) {
////						System.out.println("------------------------------------------------");
////						System.out.println("--------Found @ x=" + a + ", y=" + b + "-----------");
////					}}}}}
//					System.out.println("------------------------------------------------");
//				}
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println(screen.getHeight() + "+" + screen.getWidth());
		for (int x = 0; x < screen.getWidth(); x++) {
			for (int y =0; y < screen.getHeight() - 2; y++) {
				if (new Integer(-12181238).equals(screen.getRGB(x, y))) {
					if (y > 1) {
					System.out.println(screen.getRGB(x, y - 1));
					System.out.println(screen.getRGB(x, y - 2));
					if (new Integer(-7446217).equals(screen.getRGB(x, y - 1)))
						if (new Integer(-9322).equals(screen.getRGB(x, y - 2))) {
					start = new Point(x, y);
					return start;
						}
				}}
			}
		}
		return start;
	}

	public static int[][] readScreen(ArrayList<BufferedImage> tiles) throws AWTException {
		// get screen shot
		Robot robot = new Robot();
		Rectangle screenSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage screen = robot.createScreenCapture((optimizedRectangle == null) ? screenSize : optimizedRectangle);

		// find images
		Point start = findTopLeftCorner(screen);
		if (start == null) {
			// failed
			System.out.println("Couldn't find Bejeweled");
			return null;
		}

		// find optimized rectangle
		if (optimizedRectangle == null) {
//			optimizedRectangle = new Rectangle(start.x - 25, start.y - 110, 8 * 40 + 70, 8 * 40 + 155);
			optimizedRectangle = new Rectangle(start.x, start.y - 2, 8 * 40, 8 * 40 + 4);
			screen = robot.createScreenCapture(optimizedRectangle);
			System.out.println("optimized Rect: " + optimizedRectangle);
			System.out.println("screen size: " + screenSize);
		}

		// read tiles
		int[][] matrix = new int[8][8];
		for (int column = 0; column < 8; column++) {
			for (int row = 0; row < 8; row++) {
				
				System.out.println("start.x=" + start.x + ", start.y=" + start.y);
				BufferedImage wholeTile = screen.getSubimage(/*start.x*/0 + (column * 40), /*start.y*/2 + (row * 40), 40, 40);
				BufferedImage tile = wholeTile.getSubimage(18, 18, 4, 4);
				System.out.println("column=" + column + ", row=" + row);
				matrix[column][row] = indexOfImages(tiles, tile);
				Color color1 = new Color(tile.getColorModel().getRed(tile.getRGB(1, 2)),
						tile.getColorModel().getGreen(tile.getRGB(1, 2)),
						tile.getColorModel().getBlue(tile.getRGB(1, 2)));
				System.out.println("color1=" + color1.getRGB());
//				matrix[column][row] = new Color(tile.getColorModel().getRed(tile.getRGB(1, 2)),
//						tile.getColorModel().getGreen(tile.getRGB(1, 2)),
//						tile.getColorModel().getBlue(tile.getRGB(1, 2)));
			}
		}

		return matrix;
	}

	private static boolean compareImages(BufferedImage imageA, BufferedImage imageB) {
//		int[] dataA = imageA.getRGB(0, 0, imageA.getWidth() - 1, imageA.getHeight() - 1, null, 0, imageA.getWidth());
//		int[] dataB = imageB.getRGB(0, 0, imageB.getWidth() - 1, imageB.getHeight() - 1, null, 0, imageB.getWidth());
		Color color1 = new Color(imageA.getColorModel().getRed(imageA.getRGB(1, 2)),
				imageA.getColorModel().getGreen(imageA.getRGB(1, 2)),
				imageA.getColorModel().getBlue(imageA.getRGB(1, 2)));
		
		Color color2 = new Color(imageB.getColorModel().getRed(imageB.getRGB(1, 2)),
				imageB.getColorModel().getGreen(imageB.getRGB(1, 2)),
				imageB.getColorModel().getBlue(imageB.getRGB(1, 2)));
		
//		System.out.println("color1=" + color1.getRGB());
//		System.out.println("color2=" + color2.getRGB());
		boolean equal = false;
		int colIndex1 = getColorIndex(color1);
		int colIndex2 = getColorIndex(color2);
		if (colIndex1 == 0 || colIndex2 == 0)
			equal = true;
		else
			equal = colIndex1 == colIndex2;
//		System.out.println("equal=" + equal);
		
		return equal;
		//return Arrays.equals(dataA, dataB);
	}

	private static int indexOfImages(ArrayList<BufferedImage> images, BufferedImage image) {
		for (BufferedImage testImage : images) {
			if (compareImages(testImage, image)) {
				return images.indexOf(testImage);
			}
		}
		return -1;
	}

	private static boolean containsImage(ArrayList<BufferedImage> images, BufferedImage image) {
		for (BufferedImage testImage : images) {
			if (compareImages(testImage, image)) {
				return true;
			}
		}
		return false;
	}

	public static Point translateMatrixToScreen(Point point, Point topLeftCorner) {
		return new Point((point.x * 40) + topLeftCorner.x + 5, (point.y * 40) + topLeftCorner.y + 5);
	}
	
	public static int getColorIndex(Color col) {
		int color = col.getRGB();
		if (color == multi || color == multi2 || color == multi3 || color == multi4 || color == multi5 || color == multi6) {
			return 0;
		}
		
		if (color == white || color == whitefire  || color == whitefire2 || color == whiteSpark || color == whiteMulti || color == whiteMulti4 || color == whiteMulti6) 
		{
			return 1;
		}

		if (color == red || color == redfire || color == redSpark || color == redSpark2  || color == redSpark3  || color == redfire2 || color == redMulti1 || color == redMulti3 || color == redMulti4) 
		{
			return 2;
		}

		if (color == orange || color == orangefire || color == orangeSpark || color == orangeMulti || color == orangeMulti2) 
		{
			return 3;
		}

		if (color == yellow || color == yellowfire || color == yellowfire2 || color == yellowfire3 || color == yellowcoin || color == yellowMulti
				 || color == yellowMulti4 || color == yellowSpark || color == yellowSpark2 || color == yellowSpark3 || color == yellowSpark4) 
		{
			return 4;
		}

		if (color == purple || color == purplefire  || color == purplefire2 || color == purpleSpark || color == purpleSpark2 || color == purpleSpark3  || color == purpleSpark4 || color == purpleSpark5 || color == purpleMulti  || color == purpleMulti2 
			|| color == purpleMulti3 || color == purpleMulti5) 
		{
			return 5;
		}

		if (color == blue || color == bluefire || color == bluefire2 || color == blueSpark || color == blueSpark2 || color == blueSpark3 || color == blueSpark4 ||color == blueMulti1 || color == blueMulti2 || color == blueMulti3 || color == blueMulti4)
		{
			return 6;
		}
		if (color == green || color == greenfire || color == greenfire2 || color == greenMulti2  || color == greenSpark  || color == greenSpark2   || color == greenSpark3 || color == greenMulti3 || color == greenMulti4 || color == greenMulti5)
		{
			return 7;
		}
		
		System.out.println("Not found: color=" + color);
		return -1;
		
	}

}

