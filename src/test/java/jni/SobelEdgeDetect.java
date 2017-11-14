package jni;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SobelEdgeDetect {
	int width;// 图像宽
	int height;// 图像高
	int[] grayData;// 图像灰度值
	int size; // 图像大小
	int gradientThreshold = -1;// 判断时用到的阈值
	BufferedImage outBinary;// 输出的边缘图像

	public SobelEdgeDetect(int threshold) {
		gradientThreshold = threshold;
	}

	public void readImage(String imageName) throws IOException {
		File imageFile = new File(imageName);
		BufferedImage bufferedImage = ImageIO.read(imageFile);
		width = bufferedImage.getWidth();
		height = bufferedImage.getHeight();
		size = width * height;
		int imageData[] = bufferedImage.getRGB(0, 0, width, height, null, 0, width);// 读进的图像的RGB值
		outBinary = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);// 生成边缘图像
		grayData = new int[width * height];// 开辟内存空间
		for (int i = 0; i < imageData.length; i++) {
			grayData[i] = (imageData[i] & 0xff0000) >> 16;// 由于读的是灰度图，故只考虑一个分量（三分量值相同）
		}
	}

	public void createEdgeImage(String desImageName) {
		float[] gradient = gradientM();// 计算图像各像素点的梯度值
		float maxGradient = gradient[0];
		for (int i = 1; i < gradient.length; ++i)
			if (gradient[i] > maxGradient)
				maxGradient = gradient[i];// 获取梯度最大值
		float scaleFactor = 255.0f / maxGradient;// 比例因子用于调整梯度大小
		if (gradientThreshold >= 0) {
			for (int y = 1; y < height - 1; ++y)
				for (int x = 1; x < width - 1; ++x)
					if (Math.round(scaleFactor * gradient[y * width + x]) >= gradientThreshold)
						outBinary.setRGB(x, y, 0xffffff);// 白色
		} // 对梯度大小进行阈值处理
		else {
			for (int y = 1; y < height - 1; ++y)
				for (int x = 1; x < width - 1; ++x)
					outBinary.setRGB(x, y, 0x000000);// 黑色;
		} // //不对梯度大小进行阈值处理, 直接给出用比例因子调整后的值
		writeImage(outBinary, desImageName);
	}

	// 得到点(x,y)处的灰度值
	public int getGrayPoint(int x, int y) {
		return grayData[y * width + x];
	}

	// 算子计算 图像每个像素点 的 梯度大小
	protected float[] gradientM() {
		float[] mag = new float[size];
		int gx, gy;
		for (int y = 1; y < height - 1; ++y)
			for (int x = 1; x < width - 1; ++x) {
				gx = GradientX(x, y);
				gy = GradientY(x, y);
				// 用公式 g=|gx|+|gy|计算图像每个像素点的梯度大小.原因是避免平方和开方耗费大量时间
				mag[y * width + x] = (float) (Math.abs(gx) + Math.abs(gy));
			}
		return mag;
	}

	// 算子 计算 点(x,y)处的x方向梯度大小
	protected final int GradientX(int x, int y) {
		return getGrayPoint(x - 1, y - 1) + 2 * getGrayPoint(x - 1, y) + getGrayPoint(x - 1, y + 1)
				- getGrayPoint(x + 1, y - 1) - 2 * getGrayPoint(x + 1, y) - getGrayPoint(x + 1, y + 1);
	}// 计算像素点(x,y)X方向上的梯度值
		// 算子 计算 点(x,y)处的y方向梯度大小

	protected final int GradientY(int x, int y) {
		return getGrayPoint(x - 1, y - 1) + 2 * getGrayPoint(x, y - 1) + getGrayPoint(x + 1, y - 1)
				- getGrayPoint(x - 1, y + 1) - 2 * getGrayPoint(x, y + 1) - getGrayPoint(x + 1, y + 1);
	}// 计算像素点(x,y)Y方向上的梯度值

	public void writeImage(BufferedImage bi, String imageName) {
		File skinImageOut = new File(imageName);
		try {
			ImageIO.write(bi, "jpg", skinImageOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SobelEdgeDetect test = new SobelEdgeDetect(50);// 100
		String imageName = "img/yaunshi.png";
		String desImageName = "img/temp2.png";
		try {
			test.readImage(imageName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		test.createEdgeImage(desImageName);
	}
}