package iseeqq.tool;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.junit.Test;

import iseeqq.context.Window;
import iseeqq.img.ImgAnalyze;
import iseeqq.model.ImgCut;

/**
 * 
 * 
 * @author 戴永杰
 *
 * @date 2017年11月14日 下午5:37:27 
 * @version V1.0   
 *
 */
public class ImgUtil {

	/**
	 * 流水算法 参考:http://blog.csdn.net/zhtqmima/article/details/53289653
	 * 
	 * 精华部分.有空隙的图彻底拆开(比如"回"字会被拆成大小俩"口"字)
	 * 
	 * @param img
	 *            需要拆解的图(java.awt.image.BufferedImage)
	 * @param subImgs
	 *            用于装填拆解后的图片碎块
	 */
	public static void runningWater(BufferedImage img, List<BufferedImage> subImgs) {
		int iii=0;
		
		long startTime = System.currentTimeMillis();//用于查看算法耗时
    	int minPix = 50;//单个图片最小的像素数,下面会抛弃小于这个像素数的小图块
    	//获取图片宽高
    	int width = img.getWidth();  
        int height = img.getHeight();  
        //用于装填每个图块的点数据
        List<HashMap<Point,Integer>> pointList = new ArrayList<HashMap<Point,Integer>>(); 
        //根据宽高轮询图片中的所有点进行计算
        for(int x=0;x<width;++x){        	
        	for(int y=0;y<height;++y)
        	{
        		//找到一个点
        		label:if(isBlack(img.getRGB(x, y)))
        		{
        			Point point = new Point(x, y);//java.awt.Point
        			for(HashMap<Point,Integer> pointMap:pointList){
        				if(pointMap.get(point)!=null){
        					break label;//跳到标签处,此时不会再执行下面的内容.
        				}
        			}
        			
        			HashMap<Point,Integer> pointMap = new HashMap<Point,Integer>();
        			//这个用法很关键,根据Map的KEY值不能重复的特点避免重复填充point
        			pointMap.put(point,1);
        			//这里就是在流水啦...
        			get4Point(x, y, img, pointMap);
        			pointList.add(pointMap);
        			break;
        		}
        	}
        }
    	//根据提取出来的point创建各个碎图块
        for(int i=0;i<pointList.size();++i)
        {
        	HashMap<Point,Integer> pointMap = pointList.get(i);
        	//图片的左,上,右,下边界以及宽,高
        	int l=0,t=0,r=0,b=0,w=0,h=0,index=0;
        	for(Point p:pointMap.keySet())
        	{
        		if(index == 0){
        			//用第一个点来初始化碎图的四个边界
        			l=p.x;t=p.y;r=p.x;b=p.y;
        		}else{
        			//再根据每个点与原有的点进行比较取舍四个边界的值
        			l=Math.min(l, p.x);
        			t=Math.min(t, p.y);
        			r=Math.max(r, p.x);
        			b=Math.max(b, p.y);
        		}
        		index++;
        	}
        	w=r-l+1;h=b-t+1;
        	//去除杂点(小于50像素数量的点集不要)
        	if(w * h<minPix)continue;
        	//创建个图片空壳子(里面的所有点值都是0,即黑色)
        	BufferedImage imgCell = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        	//先将所有点替换成白色(反正我没有找到new BufferedImage的时候可以初始化像素色值的)
        	for(int x=0;x<w;++x){
        		for(int y=0;y<h;++y){
        			imgCell.setRGB(x, y, 0xffffffff);//图片换成白底
        		}
        	}
        	//对应点换成黑色(如果上面换成白底的一步不做的话下面这里可以替换成白色,就变成了黑底白色的碎图了)
        	for(Point p:pointMap.keySet())
        	{
        		imgCell.setRGB(p.x-l, p.y-t, 0);
        	}
        	//将切好的图放入上文传入的容器中(不传入容器的话这里可以用于返回)
        	
        	Window.outPic(imgCell, "img/"+(iii++)+".png");
        	subImgs.add(imgCell);
        }
        //耗时
        System.out.println("耗时:"+(System.currentTimeMillis() - startTime));
	}

	 /**
	  * 填进来上下左右中不是白色的点
     * 递归
     * @return
     */
    private static void get4Point(int x,int y,BufferedImage img,HashMap<Point,Integer> pointMap)
    {
    	Map<Point,Integer> result = new HashMap<Point,Integer>();
    	
        //左边
        Point pl = new Point(x-1, y);
        if(x-1>=0 && isBlack(img.getRGB(x-1, y)) && pointMap.get(pl)==null){
        	pointMap.put(pl,1);
        	get4Point(x-1, y,img,pointMap);
        }
        
        //右边
        Point pr = new Point(x+1, y);
        if(x+1 < img.getWidth() && isBlack(img.getRGB(x+1, y)) && pointMap.get(pr)==null){
        	pointMap.put(pr,1);
        	get4Point(x+1, y,img,pointMap);
        }
        //上边
        Point pt = new Point(x, y-1);
        if(y-1>=0 && isBlack(img.getRGB(x, y-1)) && pointMap.get(pt)==null){
        	pointMap.put(pt,1);
        	get4Point(x, y-1,img,pointMap);
        }
        //下边
        Point pb = new Point(x, y+1);
        if(y+1 < img.getHeight() && isBlack(img.getRGB(x, y+1)) && pointMap.get(pb)==null){
        	pointMap.put(pb,1);
        	get4Point(x, y+1,img,pointMap);
        }
    }
    
   
	/**
	 * 颜色 相反的
	 * 
	 * @param im
	 * @return
	 */
	public static BufferedImage inverseImage(BufferedImage img) {
		int p, a, r, g, b;
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {

				p = img.getRGB(i, j);
				a = (p >> 24) & 0xff;
				r = (p >> 16) & 0xff;
				g = (p >> 8) & 0xff;
				b = p & 0xff;

				r = 255 - r;
				g = 255 - g;
				b = 255 - b;

				p = (a << 24) | (r << 16) | (g << 8) | b;
				img.setRGB(i, j, p);
			}
		}
		return img;

	}


	/**
	 * 判断是不是黑色[这里的黑色指暗色],实际上本程序处理过的颜色,黑就是纯黑,值=0
	 * 
	 * @param colorInt
	 * @return
	 */
	public static boolean isBlack(int colorInt) {
		int[] rgb = iseeqq.tool.Color.rgb(colorInt);
		// System.out.println(rgb[0]+"\t"+rgb[1]+"\t"+rgb[2]);

		int threshold = 150;// 色域,用于界定多少范围的色值是噪色
		// Color color = new Color(colorInt);
		return rgb[0] + rgb[1] + rgb[2] <= threshold * 3;
	}

	/**
	 * 图片的灰度化
	 * 
	 * @param img
	 */
	public static void huiDuHua(BufferedImage img) {
		int sWight = img.getWidth();
		int sHight = img.getHeight();
		BufferedImage newImage = new BufferedImage(sWight, sHight, BufferedImage.TYPE_BYTE_GRAY);
		for (int x = 0; x < sWight; x++) {
			for (int y = 0; y < sHight; y++) {
				int rgb = img.getRGB(x, y);
				newImage.setRGB(x, y, rgb);
			}
		}
		try {
			ImageIO.write(newImage, "jpg", new File("aa.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 图片二值化
	 * 
	 * @param img
	 * @return
	 */
	public static BufferedImage binaryImg(BufferedImage img) {
		int sWight = img.getWidth();
		int sHight = img.getHeight();
		BufferedImage newImage = new BufferedImage(sWight, sHight, BufferedImage.TYPE_BYTE_BINARY);
		for (int x = 0; x < sWight; x++) {
			for (int y = 0; y < sHight; y++) {
				int rgb = img.getRGB(x, y);
				newImage.setRGB(x, y, rgb);
			}
		}
		return newImage;

	}

	/**
	 * 
	 * @param patchImg
	 *            碎图片
	 * @param sourceImg
	 *            源图片
	 * @return
	 */
	public static int[] checkImagePatch(BufferedImage patchImg, BufferedImage sourceImg) {
		return checkImagePatch(patchImg, sourceImg, null);
	}

	/**
	 * 
	 * @param image
	 * @param source
	 *            Color.getRGB()
	 * @param target
	 *            Color.getRGB()
	 * @return
	 */
	public static BufferedImage replaceImageColor(BufferedImage image, int source, int target) {
		int w = image.getWidth();
		int h = image.getHeight();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (image.getRGB(i, j) == source) {
					image.setRGB(i, j, target);
				}
			}
		}
		return image;
	}

	/**
	 * 
	 * @param patchImg
	 *            碎图片
	 * @param sourceImg
	 *            源图片
	 * @return
	 */
	public static int[] checkImagePatch(BufferedImage patchImg, BufferedImage sourceImg, Set<Integer> excludeRGB) {
		int[] res = null;
		try {
			int width = sourceImg.getWidth();
			int height = sourceImg.getHeight();

			boolean fg = true;
			for (int x = 0; x < width - 1; ++x) {
				for (int y = 0; y < height - 1; ++y) {

					if (excludeRGB != null) {
						if (excludeRGB.contains(sourceImg.getRGB(x, y)))
							System.out.println("排除点:" + x + "\t" + y);
					}

					if (fg && isEqual(x, y, sourceImg, patchImg)) {
						res = new int[] { x, y };
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static void main(String[] args) throws Exception {
		Thread.sleep(3000);
		System.out.println("---");
		BufferedImage image = ImageIO.read(new File("img/hb.png"));

		Set<Integer> result = new HashSet<>();
		for (int i = 28; i < 88; i++) {
			for (int j = 66; j < 84; j++) {
				result.add(image.getRGB(i, j));
			}
		}

		System.out.println(result);
		// System.exit(-1);
		Robot r = new Robot();

		ImgCut foregroundWindowPost = Window.getForegroundWindowPost(r);
		/**
		 * 242,182,104 -870808 225,163,104 -1989784 231,104,75 -3053473
		 * 209,104,95
		 * 
		 */
		// int [] rgb = new int[result.size()];
		// for (int i = 0; i < result.size(); i++) {
		// rgb[i] = result.
		// }
		int[] checkImagePatch = ImgUtil.checkImagePatch(image, foregroundWindowPost.getImg(), result);
		if (checkImagePatch != null) {
			System.out.println(checkImagePatch[0]);
			System.out.println(checkImagePatch[1]);
		} else {
			System.out.println("无");
		}

	}

	private static boolean isEqual(int x, int y, BufferedImage image, BufferedImage point) {
		int pointW = point.getWidth();
		int pointY = point.getHeight();

		for (int m = 0; m < pointW; m++)
			for (int n = 0; n < pointY; n++) {
				if (image.getRGB(x + m, y + n) != point.getRGB(m, n)) {
					return false;
				}
			}

		return true;
	}

	/**
	 * 图片切割
	 */
	public static BufferedImage cat(int x, int y, int width, int height, BufferedImage img) {
		BufferedImage subimage = img.getSubimage(x, y, width, height);
		return subimage;
	}

	/**
	 * 投影操作，灰度化->二值化
	 * 
	 * @return
	 */
	public static ImgAnalyze shootShadow(BufferedImage img) {
		int h = img.getHeight();
		int w = img.getWidth();

		// 灰度化
		int[][] gray = new int[w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int argb = img.getRGB(x, y);
				// 图像加亮（调整亮度识别率非常高）
				int r = (int) (((argb >> 16) & 0xFF) * 1.1 + 30);
				int g = (int) (((argb >> 8) & 0xFF) * 1.1 + 30);
				int b = (int) (((argb >> 0) & 0xFF) * 1.1 + 30);
				if (r >= 255) {
					r = 255;
				}
				if (g >= 255) {
					g = 255;
				}
				if (b >= 255) {
					b = 255;
				}
				gray[x][y] = (int) Math.pow(
						(Math.pow(r, 2.2) * 0.2973 + Math.pow(g, 2.2) * 0.6274 + Math.pow(b, 2.2) * 0.0753), 1 / 2.2);
			}
		}

		// 二值化
		int threshold = ostu(gray, w, h);
		BufferedImage binaryBufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (gray[x][y] > threshold) {
					gray[x][y] |= 0x00FFFF;
				} else {
					gray[x][y] &= 0xFF0000;
				}
				binaryBufferedImage.setRGB(x, y, gray[x][y]);
			}
		}

		try {
			ImageIO.write(binaryBufferedImage, "jpg", new File("temp.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		ImgAnalyze imgAnalyze = new ImgAnalyze(img);

		int[] ypro = new int[h];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (isBlack(binaryBufferedImage.getRGB(j, i))) {
					ypro[i]++;
				}
			}
		}

		imgAnalyze.setyPro(ypro);

		int[] xpro = new int[w];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (isBlack(binaryBufferedImage.getRGB(i, j))) {
					xpro[i]++;
				}
			}
		}

		imgAnalyze.setxPro(xpro);
		return imgAnalyze;

	}

	/**
	 * 简单投影操作（黑白操作）
	 * 
	 * @param img
	 * @return
	 */
	public static ImgAnalyze shootShadowSimple(BufferedImage img) {
		int h = img.getHeight();
		int w = img.getWidth();

		ImgAnalyze imgAnalyze = new ImgAnalyze(img);

		int[] ypro = new int[h];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (isBlack(img.getRGB(j, i))) {
					ypro[i]++;
				}
			}
		}

		imgAnalyze.setyPro(ypro);

		int[] xpro = new int[w];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (isBlack(img.getRGB(i, j))) {
					xpro[i]++;
				}
			}
		}

		imgAnalyze.setxPro(xpro);
		return imgAnalyze;

	}

	public static int ostu(int[][] gray, int w, int h) {
		int[] histData = new int[w * h];
		// Calculate histogram
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int red = 0xFF & gray[x][y];
				histData[red]++;
			}
		}

		// Total number of pixels
		int total = w * h;

		float sum = 0;
		for (int t = 0; t < 256; t++)
			sum += t * histData[t];

		float sumB = 0;
		int wB = 0;
		int wF = 0;

		float varMax = 0;
		int threshold = 0;

		for (int t = 0; t < 256; t++) {
			wB += histData[t]; // Weight Background
			if (wB == 0)
				continue;

			wF = total - wB; // Weight Foreground
			if (wF == 0)
				break;

			sumB += (float) (t * histData[t]);

			float mB = sumB / wB; // Mean Background
			float mF = (sum - sumB) / wF; // Mean Foreground

			// Calculate Between Class Variance
			float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

			// Check if new maximum found
			if (varBetween > varMax) {
				varMax = varBetween;
				threshold = t;
			}
		}

		return threshold;
	}

	/**
	 * 感知哈希算法
	 */
	public static void ganZhi(BufferedImage img) {

	}

	@Test
	public void bianYuanTest() throws IOException {
		BufferedImage img = ImageIO.read(new File("img/2.png"));
		bianYuan(img);
	}

	/**
	 * 边缘检测 http://blog.csdn.net/u013253810/article/details/24325317
	 * 
	 * @throws IOException
	 */
	public static void bianYuan(BufferedImage img) throws IOException {
		BufferedImage picEdge = getPicEdge(img);
		ImageIO.write(picEdge, "png", new File("img/temp.png"));

	}

	public static BufferedImage getPicEdge(BufferedImage originalPic) {
		int imageWidth = originalPic.getWidth();
		int imageHeight = originalPic.getHeight();

		BufferedImage newPic = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);

		float[] elements = { 0.0f, -1.0f, 0.0f, -1.0f, 4.0f, -1.0f, 0.0f, -1.0f, 0.0f };

		// AffineTransform at = new AffineTransform();
		Kernel kernel = new Kernel(3, 3, elements);
		ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
		cop.filter(originalPic, newPic);
		return newPic;
	}

	/**
	 * 通过颜色进行抠图
	 * 
	 * @param imgSour
	 * @param sprgb
	 * @param threshold
	 * @return
	 */
	public static BufferedImage touchRetouch(BufferedImage imgSour, int[] sprgb, int threshold) {
		int[] rgbArr = null;

		BufferedImage img = copy(imgSour);
		int h = img.getHeight();
		int w = img.getWidth();

		int cthreshold = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				rgbArr = iseeqq.tool.Color.rgb(img.getRGB(i, j));

				cthreshold = rgbArr[0] - sprgb[0] + rgbArr[1] - sprgb[1] + rgbArr[2] - sprgb[2];

				if (cthreshold > threshold) {
					img.setRGB(i, j, 0xff000000);
				} else {
					img.setRGB(i, j, 0xffffffff);
				}
			}
		}
		return img;
	}

	public static BufferedImage copy(BufferedImage img) {
		BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int rgb = img.getRGB(x, y);
				newImage.setRGB(x, y, rgb);
			}
		}
		return newImage;
	}

	// start 比较两图片的相似度
	public static int[] getData(BufferedImage img) throws Exception {
		img = binaryImg(img); // 二值化
		BufferedImage slt = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		slt.getGraphics().drawImage(img, 0, 0, 100, 100, null);
		int[] data = new int[256];
		for (int x = 0; x < slt.getWidth(); x++) {
			for (int y = 0; y < slt.getHeight(); y++) {
				int rgb = slt.getRGB(x, y);
				Color myColor = new Color(rgb);
				int r = myColor.getRed();
				int g = myColor.getGreen();
				int b = myColor.getBlue();
				data[(r + g + b) / 3]++;
			}
		}
		return data;
	}

	public static float compare(int[] s, int[] t) {
		float result = 0F;
		for (int i = 0; i < 256; i++) {
			int abs = Math.abs(s[i] - t[i]);
			int max = Math.max(s[i], t[i]);
			result += (1 - ((float) abs / (max == 0 ? 1 : max)));
		}
		return (result / 256) * 100;
	}

	/**
	 * 改变图片尺寸
	 * 
	 * @param srcFileName
	 *            源图片路径
	 * @param tagFileName
	 *            目的图片路径
	 * @param width
	 *            修改后的宽度
	 * @param height
	 *            修改后的高度
	 * @return
	 */
	public static BufferedImage zoomImage(BufferedImage srcFileName, int width, int height) {
		BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		tag.getGraphics().drawImage(srcFileName, 0, 0, width, height, null);
		return tag;

	}
	// end

}
