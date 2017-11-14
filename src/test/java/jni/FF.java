package jni;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;

public class FF {

	public static void main(String[] args) throws AWTException {

		
		Thread t = new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(-1);
				super.run();
			}
			
		};
		t.start();
		 Point mousepoint = MouseInfo.getPointerInfo().getLocation();
		 System.out.println(mousepoint);
		 
		 Robot r = new Robot();
		 moveMouse(r, 2000, 2000);
		 
	}
	
	private static void moveMouse(Robot robot,int x,int y){
		
		 Point p = MouseInfo.getPointerInfo().getLocation();
		int dx = Math.abs(x-p.x);
		int dy = Math.abs(x-p.x);
		for (int i = 0; i < dx; i++) {
			
		}
	
		int de = 60;
		
		 int x1=(x-p.x)/de,y1=(y-p.y)/de;
		 while(true){
			 p = MouseInfo.getPointerInfo().getLocation();
			 robot.mouseMove(p.x+x1, p.y+y1);
			 
			 try {
				Thread.sleep(de);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		 }
		 
	}
}
