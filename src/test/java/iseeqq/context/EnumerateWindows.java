package iseeqq.context;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.WPARAM;

import iseeqq.context.Myuser32.WindowProc;

/**
 * https://github.com/java-native-access/jna
 * 
 * @author 戴永杰
 *
 * @date 2017年11月10日 下午5:20:07
 * @version V1.0
 *
 */
public class EnumerateWindows {
	private static final int MAX_TITLE_LENGTH = 1024;

	private static Myuser32 myUser32 = Myuser32.INSTANCE;

	public static void main(String[] args) throws Exception {

		Thread.sleep(5000);
		char[] buffer = new char[MAX_TITLE_LENGTH * 2];
		HWND hwnd = User32.INSTANCE.GetForegroundWindow();
		User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);

		System.out.println("当前活动窗口:" + Native.toString(buffer));
		RECT rect = new RECT();
		User32.INSTANCE.GetWindowRect(hwnd, rect);// i GetWindowRect(hwnd,
													// rect);
		System.out.println("活动窗口位置" + rect);

		HWND h = User32.INSTANCE.GetForegroundWindow();
        final WindowProc thisFrameEventHander = myUser32.GetWindowLongPtr(h,Myuser32.GWL_WNDPROC);
        WindowProc proc=new WindowProc() {
			@Override
			public LRESULT callback(HWND hWnd, int uMsg, WPARAM wParam, LPARAM lParam) {

				System.out.println(uMsg);
				//截取需要的消息，不要的需要还给原有的时间处理函数
                if (uMsg == 1280) {
                    System.out.println("Custom message 1280 is received.");
                } else {   	
                }
                System.out.println("Custom message 1280 is received.");
                // 为了不影响该Java GUI 窗口，还需要把其他消息继续转发给原有的事件处理函数。
                return thisFrameEventHander.callback(hWnd, uMsg, wParam, lParam);
			}
		};
		
        //需要将此回调函数设置到window中去，设置失败就无法获得windows消息。h:句柄；GWL_WNDPROC = -4；proc：回调函数
        myUser32.SetWindowLongPtr(h, -4, proc);
		Thread.sleep(1000000);
	}
}
