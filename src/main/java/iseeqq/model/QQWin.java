package iseeqq.model;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import iseeqq.context.Window;
import iseeqq.img.ImgAnalyze;
import iseeqq.tool.ImgUtil;

public class QQWin {

	/**
	 * 截图
	 */
	private ImgCut imgCut;

	/**
	 * 0 未识别1 个人 2 群 3 讨论组
	 */
	private int type;

	/**
	 * 容错率
	 */
	private final int threshold = 10;

	public QQWin(ImgCut imgCut) {

		this.imgCut = imgCut;
		
		
		BufferedImage qqImg = ImgUtil.touchRetouch(imgCut.getImg(), new int[] { 222, 225, 230 }, 5);
		// 反色
		qqImg = ImgUtil.inverseImage(qqImg);
		Window.outPic(qqImg, "img/qqScan.png");

		ImgCut[] splitImgY = ImgUtil.shootShadowSimple(qqImg).splitImgY(40);
		if(splitImgY.length==2){
			if(splitImgY[0].getH()>splitImgY[1].getH()){ //上>下 个人
				type = 1;
			}else{ //群 或 讨论组
				ImgCut[] splitImgX = ImgUtil.shootShadowSimple(splitImgY[1].getImg()).splitImgX(40);
				if(splitImgX.length==2){
					
					ImgCut[] splitImgY1 = ImgUtil.shootShadowSimple(splitImgX[1].getImg()).splitImgY(20);
			
					for (int i = 0; i < splitImgY1.length; i++) {
						Window.outPic(splitImgY1[i].getImg(), "img/"+i+".png");
					}
					if(splitImgY1.length ==0){ //左侧切割
						type = 3;
					}else{
						type = 2;
					}
				}else{
					type = 0;
				}
			}
		}else{
			type = 0;
		}
		
		System.out.println("窗口类型"+type);
	}
	
	private List<Integer> margin(List<Integer> xList) {
		int x = xList.get(0);
		Iterator<Integer> iterator = xList.iterator();
		iterator.next();
		while (iterator.hasNext()) {
			Integer next = iterator.next();
			if (next - x < 10) {
				iterator.remove();
			} else {
				x = next;
			}
		}
		return xList;

	}

	@Override
	public String toString() {
		return "QQWin [imgCut=" + imgCut + ", type=" + type + ", threshold=" + threshold + "]";
	}

	public static void main(String[] args) throws AWTException, Exception {
		Thread.sleep(3000);
		Robot robot = new Robot();

		ImgCut foregroundWindowPost = Window.getForegroundWindowPost(robot);
		Window.outPic(foregroundWindowPost.getImg(), "img/yaunshi.png");
		QQWin qq = new QQWin(foregroundWindowPost);

	}

}
