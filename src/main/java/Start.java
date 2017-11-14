
import java.io.File;

import iseeqq.context.ISeeqqExecutor;
import iseeqq.model.QQMsg;


/**
 * 注意点:
 * 1、QQ前置窗口可以关闭 
 * 2、QQ发送事件 更改为 enter 
 * 3、支持抢红包了
 * 4、建议多彩气泡功能关闭，方便与时间识别
 * 5、QQ背景建议设置成默，纯色 
 * @author 戴永杰
 *
 * @date 2017年11月7日 下午4:24:11 
 * @version V1.0   
 *
 */
public class Start {

	
	public static void main(String[] args) throws InterruptedException {
		
		ISeeqqExecutor iseeqqExecutor = new ISeeqqExecutor() {
			
			@Override
			public void onMessage(String name, QQMsg context) {
				System.out.println(name);
				for (String string : context.getMessageStr()) {
					System.out.println("\t"+string);
				}
				
				QQMsg msg= new QQMsg();
				if("流浪儿".equals(name)){
					msg.append("笨笨的机器人，自动回复:");
					for (Object string : context.getMessage()) {
						if(string.toString().contains("IMG")){
							String pockIMGSrc = msg.pockIMGSrc(string.toString());
							File file = new File(pockIMGSrc);
							msg.append(file).append("ok");
						}
					}
					writeQQMsg(msg);
				}
			}
		};
		
		
	
	}
}
