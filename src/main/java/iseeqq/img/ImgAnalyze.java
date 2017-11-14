package iseeqq.img;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import iseeqq.model.ImgCut;
import iseeqq.tool.ImgUtil;

/**
 * 图片识别处理类
 * 
 * @author Administrator
 * 
 *         参考 http://blog.csdn.net/shengfn/article/details/53582694
 *
 */
public class ImgAnalyze {

	private BufferedImage img;

	public ImgAnalyze(BufferedImage img) {
		super();
		this.img = img;
	}

	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}

	/**
	 * 水平投影
	 */
	private int[] xPro;

	/**
	 * 垂直投影
	 */
	private int[] yPro;

	public int[] getxPro() {
		return xPro;
	}

	public void setxPro(int[] xPro) {
		this.xPro = xPro;
	}

	public int[] getyPro() {
		return yPro;
	}

	public void setyPro(int[] yPro) {
		this.yPro = yPro;
	}

	/**
	 * X 轴分割
	 * 
	 * @param threshold
	 *            误差值
	 * @return
	 */
	public ImgCut[] splitImgX(int threshold) {
		List<ImgCut> splitImgArr = new ArrayList<>();

		ImgCut cut = null;

		int x0 = 0;
		for (int i = 0; i < xPro.length; i++) {
			if (xPro[i] > yPro.length - threshold) {
				
				if (i - x0 < 5){
					x0 = i;
					continue; // 5个像素可忽略
				}
				cut = new ImgCut();
				cut.setX(0);
				cut.setY(i);
				cut.setImg(ImgUtil.cat(x0, 0, i-x0, yPro.length, img));
				splitImgArr.add(cut);
				x0 = i;
				
			}
		}
		return splitImgArr.toArray(new ImgCut[splitImgArr.size()]);
	}

	/**
	 * Y 轴分割
	 * 
	 * @param threshold
	 *            误差值
	 * @return
	 */
	public ImgCut[] splitImgY(int threshold) {

		List<ImgCut> splitImgArr = new ArrayList<>();

		ImgCut cut = null;

		int y0 = 0;
		for (int i = 0; i < yPro.length; i++) {
			if (yPro[i] > xPro.length - threshold) {
				
				if (i - y0 < 5){
					y0 = i;
					continue; // 5个像素可忽略
				}
				cut = new ImgCut();
				cut.setX(0);
				cut.setY(i);
				cut.setImg(ImgUtil.cat(0, y0, xPro.length, i - y0, img));
				splitImgArr.add(cut);
				y0 = i;
				
			}
		}
		return splitImgArr.toArray(new ImgCut[splitImgArr.size()]);
	}

	/**
	 * X轴 分割 ,误差为0
	 * 
	 * @return
	 */
	public ImgCut[] splitImgX() {

		List<ImgCut> splitImgArr = new ArrayList<>();

		boolean sp1 = true, sp2 = true;

		int x = 0;

		ImgCut cut = null;
		for (int i = 0; i < xPro.length; i++) {
			if (xPro[i] != 0) {
				if (sp1) {
					sp2 = true;
					x = i - 1;
					sp1 = false;
				}
			} else {
				sp1 = true;
				if (sp2) {
					sp2 = false;
					if (x == i)
						continue;
					cut = new ImgCut();
					cut.setImg(ImgUtil.cat(x, 0, i - x, img.getHeight(), img));
					cut.setX(x);
					cut.setW(i - x);
					splitImgArr.add(cut);
				}
			}
		}
		return splitImgArr.toArray(new ImgCut[splitImgArr.size()]);

	}

	public static void main(String[] args) throws IOException {

		// BufferedImage img = ImageIO.read(new File("img/000.png"));
		//
		// ImgAnalyze shootShadowSimple = ImgUtil.shootShadowSimple(img);
		//
		// ImgCut[] splitImgX = shootShadowSimple.splitImgX();
		// int i++;
		// for (ImgCut imgCut : splitImgX) {
		// Window.outPic(imgCut.getImg(), "img/x"+i+".png");
		// }
	}

	/**
	 * Y 轴分割 ,误差为0
	 * 
	 * @return
	 */
	public ImgCut[] splitImgY() {

		List<ImgCut> splitImgArr = new ArrayList<>();
		boolean sp1 = true, sp2 = true;
		int y = 0;
		ImgCut cut = null;

		for (int i = 0; i < yPro.length; i++) {
			if (yPro[i] != 0) {
				if (sp1) {
					sp2 = true;
					y = i - 2;
					sp1 = false;
				}
			} else {
				sp1 = true;
				if (sp2) {
					sp2 = false;
					if (y == i)
						continue;
					cut = new ImgCut();
					cut.setImg(ImgUtil.cat(0, y, img.getWidth(), i - y, img));
					cut.setY(y);
					cut.setH(i - y);
					splitImgArr.add(cut);
				}
			}
		}
		return splitImgArr.toArray(new ImgCut[splitImgArr.size()]);
	}

}
