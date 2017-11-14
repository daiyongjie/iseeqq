package iseeqq.tool;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.imageio.ImageIO;

import org.junit.Test;

import iseeqq.context.Window;
import iseeqq.img.ImgAnalyze;
import iseeqq.model.CutPoint;
import iseeqq.model.ImgCut;

public class ImgUtilTest {

	@Test
	public void testReplaceImageColor() throws Exception {

		Robot r = new Robot();
		// BufferedImage image = ImageIO.read(new File("img/hb.png"));
		BufferedImage image = Window.getForegroundWindowPost(r).getImg();

		Window.outPic(image, "img/bb.png");
		BufferedImage wzImage = ImageIO.read(new File("img/wz.png"));
		Set<Integer> exIntegers = new HashSet<>();

		int yb = wzImage.getRGB(28, 8);
		System.out.println(yb);
		int[] rgb = Color.rgb(yb);
		System.out.println(rgb[0]);
		System.out.println(rgb[1]);
		System.out.println(rgb[2]);
		System.out.println(Color.toInt(rgb));

		int[] sb = new int[] { 242, 182, 104 };
		System.out.println(Color.toInt(sb));
		int f = wzImage.getRGB(0, 0);
		for (int i = 0; i < wzImage.getWidth(); i++) {
			for (int j = 0; j < wzImage.getHeight(); j++) {
				int s = wzImage.getRGB(i, j);
				if (i == 28 && j == 8)
					System.out.println("实际" + s);
				// System.out.println(""+i+"\t"+j+"\t"+s);
				exIntegers.add(s);
			}
		}
		System.out.println(exIntegers);
		for (Integer integer : exIntegers) {
			image = ImgUtil.replaceImageColor(image, integer, f);
		}

		Window.outPic(image, "img/aa.png");

	}

	@Test
	public void testRunningWater() throws Exception {
		int i = 0;
		try {
			BufferedImage curImg = ImageIO.read(new File("img/bb.png"));
			List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
			ImgUtil.runningWater(curImg, subImgs);// 流水算法
			for (BufferedImage imgItem : subImgs)
				ImageIO.write(imgItem, "png", new File("img/" + (i++) + "99.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		BufferedImage curImg = ImageIO.read(new File("img/temp.png"));


		// 颜色抠图
		BufferedImage touchRetouch = ImgUtil.touchRetouch(curImg, new int[] { 222, 225, 230 }, 5);
		// 反色
		touchRetouch = ImgUtil.inverseImage(touchRetouch);
		Window.outPic(touchRetouch, "img/aa.png");
		
		ImgAnalyze splitImgX = ImgUtil.shootShadowSimple(touchRetouch);
		
		List<BufferedImage> scap = new ArrayList<>();
		
//		int y1 = 0;
//		int[] yPro = splitImgX.getyPro();
//		for (int i = 0; i < yPro.length; i++) {
//			if(yPro[i]> touchRetouch.getWidth()-10){
//				scap.add(ImgUtil.cat(0, 0, touchRetouch.getWidth(), i, touchRetouch));
//			}
//		}
//		
//		for (int x : ) {
//			if(){
//				
//			}
//			System.out.println(string);
//		}
		System.out.println(touchRetouch.getWidth()+"-----");
	}


	@Test
	public void testTouchRetouch() throws Exception {
		BufferedImage curImg = ImageIO.read(new File("img/temp.png"));

		BufferedImage touchRetouch = ImgUtil.touchRetouch(curImg, new int[] { 222, 225, 230 }, 5);

		Window.outPic(touchRetouch, "img/aa.png");
	}

	@Test
	public void testInverseImage() throws Exception {
		BufferedImage curImg = ImageIO.read(new File("img/aa.png"));

		BufferedImage touchRetouch = ImgUtil.inverseImage(curImg);

		Window.outPic(touchRetouch, "img/bb.png");
	}

}
