package iseeqq.model;

import java.awt.image.BufferedImage;


/**
 * 
 * 
 * @author 戴永杰
 *
 * @date 2017年11月14日 下午5:36:58 
 * @version V1.0   
 *
 */
public class ImgCut {

	private BufferedImage img;

	private int x;

	private int y;

	private int w;

	private int h;
	public int getH() {
		return h;
	}
	public BufferedImage getImg() {
		return img;
	}
	public int getW() {
		return w;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setH(int h) {
		this.h = h;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
		this.h = img.getHeight();
		this.w = img.getWidth();
	}

	public void setW(int w) {
		this.w = w;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "ImgCut [img=" + img + ", x=" + x + ", y=" + y + ", w=" + w + ", h=" + h + "]";
	}

}
