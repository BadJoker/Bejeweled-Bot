import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JWindow;

public class RobotWindow {
	private static void pressHintButton() throws AWTException {
		Robot robot = new Robot();
		BufferedImage screen = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		Point topLeftCorner = ReadScreen.findTopLeftCorner(screen);

		if (topLeftCorner == null) 
			return;
		Point hintButton = new Point(topLeftCorner.x, topLeftCorner.y);

		robot.mouseMove(topLeftCorner.x - 66, topLeftCorner.y + 269);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	private static void startNextGame() throws AWTException, InterruptedException {
		Thread.sleep(5000);
		Robot robot = new Robot();
		robot.mouseMove(ComputerPlayer.topLeftCorner.x - 66 + 50, ComputerPlayer.topLeftCorner.y + 269);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);

		Thread.sleep(1000);
		robot.mouseMove(ComputerPlayer.topLeftCorner.x - 66 + 50, ComputerPlayer.topLeftCorner.y + 269 + 20);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	public static void main(String[] args) {
		JWindow window = new JWindow();

		JButton screenCaptureButton = new JButton("screencapture");
		screenCaptureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ReadScreen.findNewItems();
				} catch (Exception ex) {
					//System.out.println("There was a problem: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		});
		window.getContentPane().add(screenCaptureButton, BorderLayout.NORTH);

		JButton playButton = new JButton("play");
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ComputerPlayer.play();
				} catch (Exception ex) {
					//System.out.println("There was a problem: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		});
		window.getContentPane().add(playButton, BorderLayout.CENTER);

		JButton play5Button = new JButton("play 65 seconds");
		play5Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					while (true) {
						long start = System.currentTimeMillis();
						//	                    for (int i = 0; i < 20; i++) {
						boolean hintOn = false;
						while (start + (70 * 1000) > System.currentTimeMillis()) {
							long tic = System.currentTimeMillis();
							boolean noMove = !ComputerPlayer.play();
							System.out.println("play duration " + (System.currentTimeMillis() - tic));

							if (!noMove) {
								hintOn = false;
							}

//							if (!hintOn && noMove) {
//								// computer player failed -> help human player
//								pressHintButton();
//								hintOn = true;
//							}
							long delay = 170 - (System.currentTimeMillis() - tic);
							if (delay > 0) {
								Thread.sleep(delay);
							}
						}
						System.out.println("65 seconds done");

						// start next one
						startNextGame();
					}
				} catch (Exception ex) {
					//System.out.println("There was a problem: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		});
		window.getContentPane().add(play5Button, BorderLayout.SOUTH);

		window.setSize(100, 100);
		window.setVisible(true);

		//System.out.println("started");
	}
}