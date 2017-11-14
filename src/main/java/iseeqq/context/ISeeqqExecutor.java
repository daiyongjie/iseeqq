package iseeqq.context;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import iseeqq.context.Listener.QQListener;
import iseeqq.model.QQMsg;

/**
 * 
 * 
 * @author 戴永杰
 *
 * @date 2017年11月9日 下午4:41:42 
 * @version V1.0   
 *
 */
public abstract class ISeeqqExecutor implements QQListener {
	
	
	private int checkInterval = 3;
	
	private QQContext qqContext ;

	public ISeeqqExecutor() {
		try {
			qqContext = new QQContext();
			Robot robot =  new Robot();
			ISeeQQContext wintask = new ISeeQQContext(qqContext, robot);
			wintask.setQqListener(this);
			Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(wintask, checkInterval, checkInterval,
					TimeUnit.SECONDS);
			
		} catch (AWTException e) {
			e.printStackTrace();
		}
	
	}
	
	/**
	 * 当前窗口进行回复
	 * @param msg
	 */
	public void writeQQMsg(QQMsg msg){
		qqContext.writeQQMsg(msg);
	}
	
	/**
	 * 当前窗口进行回复
	 * @param msg
	 */
	public void writeQQMsg(String msg){
		qqContext.writeQQMsg(new QQMsg().append(msg));
	}
	
	
	public boolean writeQQMsg(String name,QQMsg msg){
		
		if(qqContext.getQqStatus()==1){
			qqContext.writeQQMsg(name, msg);
			return true;
		}else{
			System.out.println(qqContext.getQqStatusMsg());
			return false;
		}
	}

}
