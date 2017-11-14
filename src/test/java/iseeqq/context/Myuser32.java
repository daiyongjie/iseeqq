package iseeqq.context;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Myuser32 extends StdCallLibrary, WinUser {

	/**************��ȡuser32���ʵ��****************/
	static Myuser32 INSTANCE = (Myuser32) Native.loadLibrary("user32",
			Myuser32.class, W32APIOptions.DEFAULT_OPTIONS);

	/**
	 * �����¼���Ϣ
	 * 
	 * @param hWnd
	 *            �ؼ��ľ��
	 * @param Msg
	 *            �¼�����
	 * @param wParam
	 *            ��0����
	 * @param lParam
	 *            ��Ҫ���͵���Ϣ������ǵ��������null
	 * @return
	 */
	int SendMessage(HWND hWnd, int Msg, int wParam, String lParam);
	

	// public static final int WM_QUERYENDSESSION = 0x11;
//	public static int GWL_WNDPROC = -4;

	
	// http://msdn.microsoft.com/en-us/library/windows/desktop/ms633573(v=vs.85).aspx
	interface WindowProc extends StdCallCallback {
		LRESULT callback(HWND hWnd, int uMsg, WPARAM wParam, LPARAM lParam);
	}
	
	// http://msdn.microsoft.com/en-us/library/windows/desktop/ms633591(v=vs.85).aspx
	WindowProc SetWindowLongPtr(HWND hWnd, int nIndex, WindowProc dwNewLong);
	
	// http://msdn.microsoft.com/en-us/library/windows/desktop/ms633584(v=vs.85).aspx
	WindowProc GetWindowLongPtr(HWND hWnd, int nIndex);

	//û��Ptr��32λ�ģ���������64λ
//	// http://msdn.microsoft.com/en-us/library/windows/desktop/ms633591(v=vs.85).aspx
//	WindowProc SetWindowLong(HWND hWnd, int nIndex, WindowProc dwNewLong);
//	
//	// http://msdn.microsoft.com/en-us/library/windows/desktop/ms633584(v=vs.85).aspx
//	WindowProc GetWindowLong(HWND hWnd, int nIndex);

	//�����Ч
//    long FindWindowA(
//  		  String lpClassName,  // class name ����
//  		  String lpWindowName  // window name ����
//  		);

}