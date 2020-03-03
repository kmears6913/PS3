
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

class GameModel {
	static float xst = 1200.0f;
	static float yst = 600.0f;
	// just a small number
	public static final float EPSI = 0.0001f; 
	
	// Max horizontal screen position with a minimum of 0.
	public static final float XMAXIMUM = xst - EPSI;
	
	// Max vertical screen position with a minimum of 0.
	public static final float YMAXIMUM = yst - EPSI; 

	private GameController controller;
	private byte[] gameMap;
	private ArrayList<Robot> robots;

	public GameModel(GameController c) {
		this.controller = c;
	}

	public void initializeGame() throws Exception {
		BufferedImage img = ImageIO.read(new File("mainmap.png"));
		if (img.getWidth() != 60 || img.getHeight() != 60)
			throw new Exception("The map image should have the dimensions of 60x60 pixels");
		gameMap = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		robots = new ArrayList<Robot>();
		robots.add(new Robot(100, 100));
	}

	// These methods are for internal use only.
	public byte[] getMap() {
		return this.gameMap;
	}

	public ArrayList<Robot> getRobots() {
		return this.robots;
	}

	public void update() {
		// Update the agents
		for (int i = 0; i < robots.size(); i++)
			robots.get(i).update();
	}

	// 0 <= x < MAP WIDTH
	// 0 <= y < MAP HEIGHT
	public float getSpeedOfTravel(float x, float y) {
		int xx = (int) (x * 0.1f);
		int yy = (int) (y * 0.1f);
		if (xx >= 60) {
			xx = 119 - xx;
			yy = 59 - yy;
		}
		int pos = 4 * (60 * yy + xx);
		return Math.max(0.2f, Math.min(3.5f, -0.01f * (gameMap[pos + 1] & 0xff) + 0.02f * (gameMap[pos + 3] & 0xff)));
	}

	public GameController getController() {
		return controller;
	}

	public float getX() {
		return robots.get(0).x;
	}

	public float getY() {
		return robots.get(0).y;
	}

	public float getDestXValue() {
		return robots.get(0).xDest;
	}

	public float getDestYValue() {
		return robots.get(0).yDest;
	}

	public void setDest(float x, float y) {
		Robot s = robots.get(0);
		s.xDest = x;
		s.yDest = y;
	}

	public double getDistanceToDest(int robot) {
		Robot s = robots.get(robot);
		return Math.sqrt(Math.pow((s.x - s.xDest),2) + Math.pow((s.y - s.yDest),2));
	}

	class Robot {
		float x;
		float y;
		float xDest;
		float yDest;

		public Robot(float x, float y) {
			this.x = x;
			this.y = y;
			this.xDest = x;
			this.yDest = y;
		}

		public void update() {
			float robotSpeed = GameModel.this.getSpeedOfTravel(this.x, this.y);
			float dx = this.xDest - this.x;
			float dy = this.yDest - this.y;
			float travelDist = (float) Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
			float t = robotSpeed / Math.max(robotSpeed, travelDist);
			dx *= t;
			dy *= t;
			this.x += dx;
			this.y += dy;
			this.x = Math.max(0.0f, Math.min(XMAXIMUM, this.x));
			this.y = Math.max(0.0f, Math.min(YMAXIMUM, this.y));
		}
	}
}
