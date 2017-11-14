package iseeqq.tool;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class ScreenShot {

	
	/**
	 * 全屏截图
	 * @param robot
	 * @return
	 */
	public static BufferedImage getScreen(Robot robot) {

		return robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

	}
	
	public static BufferedImage getScreen(Robot robot, int x, int y, int width, int height) {
		return  robot.createScreenCapture(new Rectangle(x, y, width, height));

	}
}
