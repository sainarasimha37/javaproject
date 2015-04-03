package airHockey;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Puck {
	protected static final int PUCKRADIUS = 12;
	private int width = World.GAMEWIDTH;
	private int height = World.GAMEHEIGHT;
	private int speed;
	private float colorNum;
	private Image image;
	private int resety;

	// the (x,y) coordinates of the center of the puck
	protected double puckX;
	protected double puckY;

	// the current slope the puck is moving in
	private double deltaX;
	private double deltaY;

	private boolean goal;
	private ScheduledExecutorService executor;
	private int time;

	public Puck() throws IOException {
		image = ImageIO.read(getClass().getResource("pics/puck.jpg"));
		puckX = width / 2;
		puckY = height / 2;
		speed = 0;
		resety = height / 4;
		colorNum = 0;
		executor = Executors.newScheduledThreadPool(1);
	}

	private void reset() {
		puckX = width / 2;
		// TODO see why turning to NaN
		/* if (resety == height / 4) {
		 * resety *= 3;
		 * }
		 * else {
		 * resety = height / 4;
		 * }
		 * puckY = resety; */
		puckY = height / 2;
		speed = 0;
	}

	public int move() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		puckX += deltaX;
		puckY += deltaY;

		// if hit side wall
		if (puckX - PUCKRADIUS <= 4 || puckX + PUCKRADIUS >= width - 4) {
			colorNum += .02;
			deltaX = -deltaX;
			decreaseSpeed();
			changeColor();
		}

		// hit top/bottom walls
		// if the puck hit a side within the goal range, returns the player who
		// scored a point
		// otherwise returns 0
		else {
			if (puckY - PUCKRADIUS <= 4) {
				return checkGoal(1);
			}
			else if (puckY + PUCKRADIUS >= height - 4) {
				return checkGoal(2);
			}
		}
		return 0;
	}

	private int checkGoal(int player) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		// if puck within goal range, return player who scores
		if (puckX > 70 && puckX < width - 70) {
			time = 2;
			executor.scheduleAtFixedRate(timer, 0, 3, TimeUnit.SECONDS);
			reset();
			// TODO sound.changeTrack("sound/goal.wav");
			goal = true;
			return player;
		}

		// otherwise bounce off wall
		deltaY = -deltaY;
		decreaseSpeed();
		changeColor();
		return 0;
	}

	public void drawPuck(Graphics g) {
		g.setColor(Color.getHSBColor(colorNum, 1, 1));

		g.fillOval((int) (puckX - PUCKRADIUS), (int) (puckY - PUCKRADIUS), PUCKRADIUS * 2, PUCKRADIUS * 2);
		/* g.drawImage(image,(int) (puckX - radius), (int) (puckY - radius),
		 * radius * 2, radius * 2,null); */

		if (goal) {
			g.setFont(new Font("Arial", Font.BOLD, 50));
			g.setColor(Color.BLUE);
			// g.drawString("GOAL!", 70, 260);
		}

	}

	public Runnable timer = new Runnable() {
		public void run() {
			time--;
			if (time == 0) {
				goal = false;
			}
		}
	};

	public void setSlope(int malletX, int malletY) {
		deltaX = puckY - malletY;
		deltaY = puckX - malletX;
		// double hyp=Math.sqrt(Math.pow(deltaX, 2)+Math.pow(deltaY, 2));
		// double angle1 =(90*deltaX)/(deltaX+deltaY);

		// keep track if original x or y was negative so know which end
		// direction should be negative
		// otherwise 2 negatives will just cancel out or don't know if x or y
		// was negative
		int xneg = 1;
		int yneg = 1;
		if (deltaX < 0) {
			xneg = -1;
		}
		if (deltaY < 0) {
			yneg = -1;
		}

		// calculate the inverse tangent of the slope
		double angle1 = Math.atan(deltaX / deltaY);
		double angle2 = 90 - angle1;
		deltaY = (Math.sin(angle1) * speed) * yneg;
		deltaX = (Math.sin(angle2) * speed) * xneg;
	}

	public void decreaseSpeed() {
		speed--;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int i) {
		speed = i;
	}

	public void changeColor() {
		colorNum += .02;
	}

	public boolean getGoal() {
		return goal;
	}
}