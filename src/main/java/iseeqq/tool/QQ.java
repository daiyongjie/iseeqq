package iseeqq.tool;

import java.awt.image.BufferedImage;

import iseeqq.model.QQMsg;

public class QQ {

	/**
	 * 
	 * @param img
	 * @return 0 未登录 1 在线
	 */
	public static int getQQStatus(BufferedImage img) {
		int online = 0; // 在线
		int offline = 0; // 离线

		int w = img.getWidth();
		int h = img.getHeight();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int rgb = img.getRGB(i, j);
				// int[] rgb2 = Color.rgb(rgb);
				if (rgb == -1369049) { // 在线的RGB （235,28,39） 偏红
					online++;
				} else if (rgb == -9211021) { // 离线 （115,115,115） 偏灰
					offline++;
				}
			}
		}
		;
		return online > offline ? 1 : 0;
	}

	public static QQMsg parseQQText(String qqmsg) {
		QQMsg qqmst = new QQMsg();
		qqmsg = qqmsg.replace("<!--StartFragment -->", "");
		qqmsg = qqmsg.replace("<DIV>", "");
		qqmsg = qqmsg.replace("</DIV>", "");
		String[] split = qqmsg.split("<br>");
		for (String line : split) {
			if (line != null)
				qqmst.append(line);
		}
		return qqmst;

	}

}
