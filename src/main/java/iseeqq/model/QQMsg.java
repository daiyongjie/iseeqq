package iseeqq.model;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * 目前支持 文字和图片
 * 
 * @author 戴永杰
 *
 * @date 2017年11月8日 下午4:16:51 
 * @version V1.0   
 *
 */
public class QQMsg {
	
	private List<Object> message = new ArrayList<>();

	public QQMsg append(String str){
		message.add(str);
		return this;
	}
	
	public QQMsg append(File file){
		message.add(file);
		return this;
	}

	
	public List<Object> getMessage(){
		return message;
	}
	
	public List<String> getMessageStr(){
		List<String> result = new ArrayList<>();
		message.forEach(x->{
			if(x instanceof String)
				result.add((String) x);
		});
		return result;
	}
	/**
	 * 这是一张图片 <IMG src="file:///C:\Users\ADMINI~1\AppData\Local\Temp\FZIETLV9DWB9R}D5S16G2VO.gif" sysface=212>
	 * @param str
	 * @return file:///C:\Users\ADMINI~1\AppData\Local\Temp\FZIETLV9DWB9R}D5S16G2VO.gif
	 */
	public String pockIMGSrc(String str){
		int indexOf = str.indexOf("<IMG");
		if(indexOf!=-1){
		  String imgStr = str.substring(indexOf,str.indexOf(">", indexOf)+1);
		  indexOf = imgStr.indexOf("src=")+5;
		  return imgStr.substring(indexOf,imgStr.indexOf("\"",indexOf)).replace("file:///", "");
		}
		return "";
		
	}
	
	public static void main(String[] args) throws URISyntaxException {
		QQMsg qq = new QQMsg();
		String s = "NODEMCU过个继电器就好了<IMG src=\"file:///C:\\Users\\ADMINI~1\\AppData\\Local\\Temp\\FZIETLV9DWB9R}D5S16G2VO.gif\" sysface=212>1111";
		String pockIMGSrc = qq.pockIMGSrc(s);
		System.out.println(pockIMGSrc);
		System.out.println(new File(pockIMGSrc).exists());
		
		
	}
}
