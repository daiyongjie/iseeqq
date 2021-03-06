package iseeqq.tool;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


/**
 * 
 * 
 * @author 戴永杰
 *
 * @date 2017年11月14日 下午5:37:41 
 * @version V1.0   
 *
 */
public class Source {

	// 口令红包
	public static BufferedImage klhbImage = null;
	// 普通红包
	public static BufferedImage pthbImage = null;
	
	public static BufferedImage srklImage = null;
	
	static{
		try {
			pthbImage = ImageIO.read(new File("img/pthb.png"));
			klhbImage = ImageIO.read(new File("img/klhb.png"));
			srklImage = ImageIO.read(new File("img/srkl.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		
		
		System.out.println(Source.klhbImage);
	}

}
