package iseeqq.context;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.StandardSocketOptions;

import javax.swing.ImageIcon;

import iseeqq.context.Listener.QQListener;
import iseeqq.img.FingerPrint;
import iseeqq.model.ImgCut;
import iseeqq.tool.Color;
import iseeqq.tool.ImgUtil;
import iseeqq.tool.QQ;
import iseeqq.tool.ScreenShot;

/**
 * 
 * 
 * @author 戴永杰
 *
 * @date 2017年11月9日 下午4:41:37
 * @version V1.0
 *
 */
public class ISeeQQContext implements Runnable {

	private QQContext qqContext = null;

	private QQListener qqListener;

	public ISeeQQContext(QQContext qqContext, Robot robot) {
		this.qqContext = qqContext;
		qqContext.setRobot(robot);
	}

	public void setQqListener(QQListener qqListener) {
		this.qqListener = qqListener;
	}

	@Override
	public void run() {
		try {
			searchQQPostion(qqContext);
			if (qqContext.isScan() || qqContext.getQqStatus() == 1) { // QQ已启动
				qqContext.setScan(true);
				qqContext.addListerner(qqListener);
			} else {
				System.out.println(qqContext.getQqStatusMsg());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public boolean searchQQPostion(QQContext qqContext) throws IOException, InterruptedException {

		if (qqContext.isScan()) {
			return true;
		}

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int h = (int) screenSize.getHeight();
		int w = (int) screenSize.getWidth();
		ImageIcon runProIcon = new Window().getRunProIcon("QQ.exe");
		if (runProIcon == null) {
			qqContext.setQqStatus(-1);
		} else {
			// 保守点，上下边线都减去1px
			BufferedImage bottom = ScreenShot.getScreen(qqContext.getRobot(), 0, h - 39, w - 80, 35);
			BufferedImage qqIcon = (BufferedImage) runProIcon.getImage();

			BufferedImage kouTuYS = ImgUtil.touchRetouch(bottom, Color.rgb(bottom.getRGB(1, 1)), 50);

			ImgCut[] splitImgX = ImgUtil.shootShadowSimple(kouTuYS).splitImgX();
			for (ImgCut imgCutX : splitImgX) {
				ImgCut[] splitImgY = ImgUtil.shootShadowSimple(imgCutX.getImg()).splitImgY();
				for (ImgCut imgCutY : splitImgY) {
					BufferedImage cat = ImgUtil.cat(imgCutX.getX() - 1, imgCutY.getY(), imgCutX.getW() + 2,
							imgCutY.getH(), bottom);
					// 图片大小 10*10~20*20 之间的做比较
					if (cat.getWidth() < 10 || cat.getWidth() > 20 || cat.getHeight() < 10 || cat.getHeight() > 20) {
						continue;
					}
					float compare = new FingerPrint(qqIcon).compare(new FingerPrint(ImgUtil.copy(cat)));
					if (compare > 0.8) { // 相似度大于80% ，认为找到QQ图片
						imgCutX.setY(h - 39 + imgCutY.getY());
						imgCutX.setH(imgCutY.getH());
						imgCutX.setImg(cat);
						qqContext.setQqPost(imgCutX);
						qqContext.setQqStatus(QQ.getQQStatus(cat));
						return true;
					}
				}
			}

			BufferedImage bottom2 = ScreenShot.getScreen(qqContext.getRobot(), 0, h - 39, w - 80, 35);
			label: for (int i = 0; i < bottom.getHeight(); i++) {
				for (int j = 0; j < bottom.getWidth(); j++) {
					if (bottom2.getRGB(j, i) != bottom.getRGB(j, i)) {
						// 查找点击忽略按钮
						qqContext.getRobot().setAutoDelay(200);
						qqContext.getRobot().mouseMove(j + 3, h - 39 + i + 5);
						qqContext.getRobot().delay(200);
						qqContext.getRobot().mouseMove(j + 2, h - 35 + i);
						qqContext.getRobot().delay(200);
						qqContext.getRobot().mouseMove(j + 2, h - 39 - 25);
						qqContext.getRobot().delay(200);
						qqContext.getRobot().mouseMove(j - 80, h - 39 - 25);
						qqContext.getRobot().delay(200);
						qqContext.getRobot().mousePress(InputEvent.BUTTON1_DOWN_MASK);
						qqContext.getRobot().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
						qqContext.getRobot().delay(200);
						break label;
					}
				}
			}
			qqContext.setQqStatus(-2);
		}
		return false;
	}

}
