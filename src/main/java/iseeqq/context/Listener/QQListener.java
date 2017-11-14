package iseeqq.context.Listener;

import iseeqq.model.QQMsg;

/**
 * 
 * 
 * @author 戴永杰
 *
 * @date 2017年11月9日 下午4:41:28 
 * @version V1.0   
 *
 */
public interface QQListener {

	/**
	 * 接收到的消息
	 * @param name 窗口
	 * @param context 内容
	 */
	void onMessage(String window,QQMsg context);
	
	/**
	 * 接收到红包
	 * @param name 窗口
	 * @param context 内容
	 */
	void onRedEnvelope(String window,QQMsg context);
}
