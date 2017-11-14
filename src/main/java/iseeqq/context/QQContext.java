package iseeqq.context;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import iseeqq.context.Listener.QQListener;
import iseeqq.img.FingerPrint;
import iseeqq.model.ImgCut;
import iseeqq.model.QQMsg;
import iseeqq.tool.ClipboardUtil;
import iseeqq.tool.ImgUtil;
import iseeqq.tool.QQ;
import iseeqq.tool.ScreenShot;
import iseeqq.tool.Source;


/**
 * 
 * 
 * @author 戴永杰
 *
 * @date 2017年11月14日 下午5:36:03 
 * @version V1.0   
 *
 */
public class QQContext {

	private Robot robot;

	public Robot getRobot() {
		return robot;
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public int getQqStatus() {
		return qqStatus;
	}

	public String getQqStatusMsg() {

		switch (qqStatus) {
		case -2:
			return "未识别到QQ图标位置";
		case -1:
			return "QQ程序未启动";
		case 0:
			return "QQ离线了";
		case 1:
			return "QQ在线中";
		default:
			System.out.println("未识别状态" + qqStatus);
			return "";
		}
	}

	public void setQqStatus(int qqStatus) {
		this.qqStatus = qqStatus;
	}

	public ImgCut getQqPost() {
		return qqPost;
	}

	public void setQqPost(ImgCut qqPost) {
		this.qqPost = qqPost;
	}

	/**
	 * -1 未找到 0 离线 1 在线
	 */
	private int qqStatus = 0;

	/**
	 * 处于扫描中状态
	 */
	private boolean scan;

	public boolean isScan() {
		return scan;
	}

	public void setScan(boolean scan) {
		this.scan = scan;
	}

	private ImgCut qqPost;

	public void addListerner(QQListener qqListener) {
		// 比较两图片的相似度

		try {
			float compare = new FingerPrint(qqPost.getImg()).compare(new FingerPrint(getCurQQPost()));
			if (compare > 0.80) { // 相似度大于90%
				if (QQ.getQQStatus(getCurQQPost()) == 0) {
					System.out.println("QQ离线中");
					this.scan = false;
				} else {
					System.out.println("等待接收消息");
				}
			} else { // 相似度大于90%
				System.out.println("打开消息窗口");

				ImgCut openWindow = openWindow();
				if (openWindow != null)
					handleMessage(getMessageCode(openWindow), qqListener, openWindow);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized int getMessageCode(ImgCut openWindow) {

		if ("验证消息".equals(Window.getForegroundWindowTitle())) {
			return 0;
		}
		int[] hongBao = checkRedBag(openWindow);// 有红包啦
		if (hongBao != null) {
			readRedBag(openWindow, hongBao);
			return hongBao[0];
		}
		return 1;
	}

	/**
	 * 
	 * @param openWindow
	 * @return
	 */
	public synchronized int[] checkRedBag(ImgCut openWindow) {

		int[] result = null;
		int[] checkImagePatch = ImgUtil.checkImagePatch(Source.pthbImage, openWindow.getImg());
		if (checkImagePatch != null) {
			// 检测红包的位置处理
			result = new int[3];
			result[0] = 2; // 普通红包
			result[1] = openWindow.getX() + checkImagePatch[0] + 70;
			result[2] = openWindow.getY() + checkImagePatch[1] + 70;
		}
		checkImagePatch = ImgUtil.checkImagePatch(Source.klhbImage, openWindow.getImg());
		if (checkImagePatch != null) {
			// 检测红包的位置处理
			result = new int[3];
			result[0] = 3; // 口令红包
			result[1] = openWindow.getX() + checkImagePatch[0] + 70;
			result[2] = openWindow.getY() + checkImagePatch[1] + 70;
		}
		return result;
	}

	/**
	 * 
	 * @param type
	 *            0[系统消息] 1[消息] 2[普通红包] 3 口令红包
	 * @param qqListener
	 */
	public synchronized void handleMessage(int type, QQListener qqListener, ImgCut openWindow) {
		QQMsg readQQMsg = null;
		if (qqListener == null)
			return;
		switch (type) {
		case 0: // 系统消息 直接关闭
			clearWindow(openWindow);
			break;
		case 1:
			readQQMsg = readQQMsg();
			qqListener.onMessage(Window.getForegroundWindowTitle(), readQQMsg);
			clearQQMsg(openWindow);
			break;
		case 2:
		case 3:
			readQQMsg = readQQMsg();
			qqListener.onRedEnvelope(Window.getForegroundWindowTitle(), readQQMsg);
			clearQQMsg(openWindow);
			break;
		default:
			break;
		}
	}

	public synchronized void clearWindow(ImgCut openWindow) {
		// 关闭窗口
		robot.mouseMove(openWindow.getX() + openWindow.getW() - 30, openWindow.getY() + 20);
		robot.delay(interval);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.delay(interval);
	}

	public synchronized void writeQQMsg(QQMsg qqmsg) {
		for (Object msg : qqmsg.getMessage()) {
			if (msg instanceof String) {
				String msgStr = ((String) msg).trim();
				if (msgStr.trim().length() != 0)
					ClipboardUtil.setSysClipboardText(msgStr);
			} else if (msg instanceof File) {
				Image imgObj = null;
				try {
					imgObj = ImageIO.read((File) msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
				ClipboardUtil.setClipboardImage(imgObj);

			} else if (msg instanceof Image) {
				ClipboardUtil.setClipboardImage((Image) msg);
			}
			// 粘贴
			robot.delay(interval);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.delay(interval);
			// shift+enter
			robot.keyPress(KeyEvent.VK_SHIFT);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_SHIFT);

		}
		// 发送出去 enter
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);

	}

	
	
	public synchronized void writeQQMsg(String name, QQMsg msg) {

		robot.mouseMove(qqPost.getX() + 3, qqPost.getY() + 3);
		// 右键
		robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
		ClipboardUtil.setSysClipboardText(name);

		// 上 上 回车
		robot.delay(interval);
		robot.keyPress(KeyEvent.VK_UP);
		robot.keyRelease(KeyEvent.VK_UP);
		robot.delay(interval);
		robot.keyPress(KeyEvent.VK_UP);
		robot.keyRelease(KeyEvent.VK_UP);
		robot.delay(interval);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		robot.delay(interval);

		if (!Window.getForegroundWindowTitle().equals(name)) {
			robot.mouseMove(100, 100);
			// win+d 显示桌面
			robot.keyPress(KeyEvent.VK_WINDOWS);
			robot.keyPress(KeyEvent.VK_D);
			robot.keyRelease(KeyEvent.VK_WINDOWS);

			System.out.println("未找到" + name);
			return;
		}

		ImgCut qqWin = Window.getForegroundWindowPost(robot);
		robot.mouseMove(qqWin.getX() + 10, qqWin.getX() + 10);
		robot.delay(interval);
		// // 左键
		// robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		// robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		// robot.delay(interval);
		// ctrl+V
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(interval);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		robot.delay(interval);

		writeQQMsg(msg);
		System.out.println();
		clearQQMsg(Window.getForegroundWindowPost(robot));
	}

	private int interval = 300;

	public synchronized QQMsg readQQMsg() {
		robot.delay(interval);
		robot.keyPress(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_TAB);
		robot.delay(interval);
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_A);
		robot.delay(interval);
		robot.keyPress(KeyEvent.VK_C);
		robot.keyRelease(KeyEvent.VK_C);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(500+interval);
		return ClipboardUtil.getSystemClipboardQQ();
	}

	public synchronized void readRedBag(ImgCut qqWindow, int[] hongBao) {
		robot.mouseMove(hongBao[1], hongBao[2]);
		robot.delay(interval);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.delay(interval + 500);// 防止延迟

		if (hongBao[0] == 2) {

			// 关闭窗口
			ImgCut foregroundWindowPost = Window.getForegroundWindowPost(robot);
			robot.mouseMove(foregroundWindowPost.getX() + foregroundWindowPost.getW() - 30,
					foregroundWindowPost.getY() + 20);
			robot.delay(interval);
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			robot.delay(interval);
		} else if (hongBao[0] == 3) {
			ImgCut foregroundWindowPost = Window.getForegroundWindowPost(robot);
			// Window.outPic(foregroundWindowPost.getImg(), "img/temp.png");
			int[] checkImagePatch = ImgUtil.checkImagePatch(Source.srklImage, foregroundWindowPost.getImg());
			if (checkImagePatch != null) {
				robot.mouseMove(foregroundWindowPost.getX() + checkImagePatch[0] + 50,
						foregroundWindowPost.getY() + checkImagePatch[1] + 35);
				robot.delay(interval);
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				robot.delay(interval);

				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
				robot.delay(interval);

				// 关闭窗口
				foregroundWindowPost = Window.getForegroundWindowPost(robot);
				robot.mouseMove(foregroundWindowPost.getX() + foregroundWindowPost.getW() - 30,
						foregroundWindowPost.getY() + 20);
				robot.delay(interval);
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				robot.delay(interval);
			}
		}
	}

	public static void main(String[] args) throws AWTException, InterruptedException, Exception {

		Thread.sleep(3000);
		String foregroundWindowTitle = Window.getForegroundWindowTitle();
		System.out.println(foregroundWindowTitle);

		Robot r = new Robot();
		Window.minimizeWindow(Window.getForegroundWindowPost(r), r);

	}

	public synchronized void clearQQMsg(ImgCut qqWindow) {
		robot.mouseMove(qqWindow.getX() + qqWindow.getW() / 2, qqWindow.getY() + qqWindow.getH() / 2);

		// 右键
		robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
		// 方向 up 清屏
		robot.keyPress(KeyEvent.VK_UP);
		robot.keyRelease(KeyEvent.VK_UP);
		robot.delay(interval);
		// 回车
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		robot.delay(interval);

		// 关闭窗口
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_W);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(interval);

		if ("提示".equals(Window.getForegroundWindowTitle())) {
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			robot.delay(interval);
		}

		// 清空剪贴板
		ClipboardUtil.clear();

	}

	public ImgCut openWindow() {
		robot.mouseMove(qqPost.getX() + 5, qqPost.getY());
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.delay(1000 + interval);

		if ("QQ".equals(Window.getForegroundWindowTitle())) { // 打开失败 关闭窗口
			Window.minimizeWindow(Window.getForegroundWindowPost(robot), robot);
			return null;
		}
		return Window.getForegroundWindowPost(robot);
	}

	public BufferedImage getCurQQPost() {
		return ScreenShot.getScreen(robot, qqPost.getX(), qqPost.getY(), qqPost.getW(), qqPost.getH());
	}

}
