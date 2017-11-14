package iseeqq.tool;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import iseeqq.model.QQMsg;

public class ClipboardUtil {

	public static void clear() {
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(""), null);
	}

	public static void setClipboardImage(final Image image) {
		Transferable trans = new Transferable() {
			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				if (isDataFlavorSupported(flavor)) {
					return image;
				}
				throw new UnsupportedFlavorException(flavor);
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);
			}
		};

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
	}

	public static QQMsg getSystemClipboardQQ() {

		try {
			Clipboard sysClb = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable t = sysClb.getContents(null);

			if ("html".equals(t.getTransferDataFlavors()[0].getSubType())) {
				Reader readerForText = t.getTransferDataFlavors()[0].getReaderForText(t);
				BufferedReader br = new BufferedReader(readerForText);
				StringBuilder sbStr = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					sbStr.append(line);
				}
				return QQ.parseQQText(sbStr.toString());
			}else if("x-java-text-encoding".equals(t.getTransferDataFlavors()[0].getSubType())) {
				Reader readerForText = t.getTransferDataFlavors()[1].getReaderForText(t);
				BufferedReader br = new BufferedReader(readerForText);
				StringBuilder sbStr = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					sbStr.append(line);
				}
			
				return QQ.parseQQText(sbStr.toString());
			} else {
				System.err.println("未识别的类型" + t.getTransferDataFlavors()[0].getSubType());
				Reader readerForText = t.getTransferDataFlavors()[1].getReaderForText(t);
				BufferedReader br = new BufferedReader(readerForText);
				StringBuilder sbStr = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					sbStr.append(line);
				}
				return QQ.parseQQText(sbStr.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	public static String getSystemClipboard() {
		Clipboard sysClb = null;
		sysClb = Toolkit.getDefaultToolkit().getSystemClipboard();
		try {

			Transferable t = sysClb.getContents(null);
			if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String text = (String) t.getTransferData(DataFlavor.stringFlavor);
				return text;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		QQMsg systemClipboard = getSystemClipboardQQ();
		System.out.println("------------");
		for (String string : systemClipboard.getMessageStr()) {
			
			System.out.println(string);
		}
		// clear();
	}

	public static void setSysClipboardText(String msg) {
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable tText = new StringSelection(msg);
		clip.setContents(tText, null);
	}
}
