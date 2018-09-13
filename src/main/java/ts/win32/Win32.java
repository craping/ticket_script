package ts.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Win32 extends StdCallLibrary {
	
	int KEYEVENTF_KEYUP = 0x0002;
	
	int KEYEVENTF_KEYDOWN = 0x0;
	
	int WM_SETTEXT = 0x000C;
	
	int WM_GETTEXT = 0x000D;
	
	int CB_SETCURSEL = 0x014E;
	
	int BM_CLICK = 0xF5;
	
	int CB_SHOWDROPDOWN = 0x014F;
	
	Win32 INSTANCE = (Win32) Native.loadLibrary("user32", Win32.class, W32APIOptions.DEFAULT_OPTIONS);
    
	int SendMessage(HWND hWnd, int msg, int wParam, String lParam);
    
	int SendMessage(HWND hWnd, int msg, int wParam, HWND hWnd1);
	
	int SendMessage(HWND hWnd, int msg, int nMaxCount, char[] lpString);
	
	void SwitchToThisWindow(HWND hWnd, boolean fAltTab);
	
	int SetWindowText(HWND hWnd, String lParam);
	
	HWND GetParent(HWND hWnd);
	
	boolean GetClientRect(HWND hWnd, RECT lpRect);
	
	void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);
	
	short GetKeyState(int nVirtKey);
	
}
