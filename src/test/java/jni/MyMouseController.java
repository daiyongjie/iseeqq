package jni;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.MouseInfo;
import java.awt.AWTException;
import java.awt.event.InputEvent;
import javax.swing.*;

public class MyMouseController {

	private Dimension dim; // 存储屏幕尺寸
	private Robot robot;// 自动化对象

	public MyMouseController() {
		dim = Toolkit.getDefaultToolkit().getScreenSize();
		System.out.println("屏幕大小为：" + dim.getWidth() + " " + dim.getHeight());
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public void Move(int width, int heigh) { // 鼠标移动函数
		System.out.println("enter Move()...");
		Point mousepoint = MouseInfo.getPointerInfo().getLocation();
		System.out.println("移动前坐标：" + mousepoint.x + " " + mousepoint.y);
		width += mousepoint.x;
		heigh += mousepoint.y;
		try {
			robot.delay(3000);
			robot.mouseMove(width, heigh);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("移动后坐标：" + width + " " + heigh);
		// robot.mousePress(InputEvent.BUTTON1_MASK);//鼠标单击
	}

	public static void main(String args[]) throws Exception {

		MyMouseController mmc = new MyMouseController();

		System.out.println("mouse control start:");
		mmc.Move(20, 20);// 坐标为相对坐标
		System.out.println("=======第二次移动=======");
		mmc.Move(512, 384);

		System.out.println("mouse control stop.");

	}
}