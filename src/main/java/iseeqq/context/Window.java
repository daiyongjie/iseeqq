package iseeqq.context;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;

import iseeqq.model.ImgCut;
import iseeqq.tool.ScreenShot;


/**
 * 
 * 
 * @author 戴永杰
 *
 * @date 2017年11月14日 下午5:36:08 
 * @version V1.0   
 *
 */
public class Window {

	public ImageIcon getRunProIcon(String proName) throws IOException {
		String cmd = "wmic process where name='" + proName + "' get ExecutablePath";
		// C:\Windows\System32\wbem\WMIC.exe
		String proPath = exceWin32(cmd, proName).replace("ExecutablePath", "").trim();
		if (!proPath.contains(proName)) {
			return null;
		}
		ImageIcon imageicon = (ImageIcon) getProIcon(proPath);
		return imageicon;
	}

	public Icon getProIcon(String file) {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		return fsv.getSystemIcon(new File(file));
	}

	private String exceWin32(String cmd, String keyStr) {
		BufferedReader input = null;
		String line;
		StringBuffer sbStr = new StringBuffer();
		try {
			// System.out.println(cmd);
			Process p = Runtime.getRuntime().exec(cmd);

			input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				if (line.contains(keyStr)) {
					return line;
				}
				sbStr.append(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sbStr.toString();
	}

	/**
	 * 输出一张图片
	 */
	public static void outPic(BufferedImage bi, String name) {
		String suf = name.substring(name.lastIndexOf(".") + 1);
		try {
			ImageIO.write(bi, suf, new File(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void outIcon(ImageIcon imgIcon, String name) {
		Image image = imgIcon.getImage();
		BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		outPic(bi, name);
	}

	private static final int MAX_TITLE_LENGTH = 1024;

	public static ImgCut getForegroundWindowPost(Robot robot) {
		HWND hwnd = User32.INSTANCE.GetForegroundWindow();
		RECT rect = new RECT();
		User32.INSTANCE.GetWindowRect(hwnd, rect);
		ImgCut img = new ImgCut();
		img.setX(rect.left);
		img.setY(rect.top);
		img.setW(rect.right - rect.left);
		img.setH(rect.bottom - rect.top);
		img.setImg(ScreenShot.getScreen(robot, img.getX(), img.getY(), img.getW(), img.getH()));
		return img;
	}

	public static String getForegroundWindowTitle() {
		char[] buffer = new char[MAX_TITLE_LENGTH * 2];
		HWND hwnd = User32.INSTANCE.GetForegroundWindow();
		User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
		return Native.toString(buffer);

	}
	
	public static void closeWindow(ImgCut imgCut,Robot robot){
		int x = imgCut.getX()+imgCut.getW()-25;
		int y = imgCut.getY()+20;
		robot.mouseMove(x, y);
		robot.delay(200);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.delay(200);
	}
	
	/**
	 * 最小化窗口
	 * @param imgCut
	 * @param robot
	 */
	public static void minimizeWindow(ImgCut imgCut,Robot robot){
		int x = imgCut.getX()+imgCut.getW()-50;
		int y = imgCut.getY()+20;
		robot.mouseMove(x, y);
		robot.delay(200);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.delay(200);
	}
}