package ts.win32;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinUser.INPUT;

public class Win132 {
	
	public static final long MOUSEEVENTF_MOVE = 0x0001L;

	public static final long MOUSEEVENTF_LEFTDOWN = 0x0002L;
	
	public static final long MOUSEEVENTF_LEFTUP = 0x0004L;
	
	public static final long MOUSEEVENTF_VIRTUALDESK = 0x4000L;
	
	public static final long MOUSEEVENTF_ABSOLUTE = 0x8000L;

	public static void sendKey(int key){
		sendKeyDown(key);
		sendKeyUp(key);
	}
	public static void sendKeyDown(int key) {
		INPUT input = new INPUT();
		input.type = new DWORD(INPUT.INPUT_KEYBOARD);
		input.input.setType("ki");
		input.input.ki.wVk = new WORD(key);
		input.input.ki.time = new DWORD(Kernel32.INSTANCE.GetTickCount());
		INPUT[] inputs = {input};
		User32.INSTANCE.SendInput(new DWORD(inputs.length), inputs, input.size());
	}

	public static void sendKeyUp(int key) {
		INPUT input = new INPUT();
		input.type = new DWORD(INPUT.INPUT_KEYBOARD);
		input.input.setType("ki");
		input.input.ki.wVk = new WORD(key);
		input.input.ki.dwFlags = new DWORD(0x0002);
		input.input.ki.time = new DWORD(Kernel32.INSTANCE.GetTickCount());
		INPUT[] inputs = {input};
		User32.INSTANCE.SendInput(new DWORD(inputs.length), inputs, input.size());
	}
	
	public static void sendMouseDown(HWND hwnd) {
		INPUT input = new INPUT();
		input.type = new DWORD(INPUT.INPUT_MOUSE);
		input.input.setType("mi");
	    input.input.mi.dwFlags = new DWORD(MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_LEFTUP);
		
		INPUT[] inputs = {input};
		User32.INSTANCE.SendInput(new DWORD(inputs.length), inputs, input.size());
	}
	
	public static void sendClick(HWND hwnd, int x, int y) {
		RECT rect = new RECT();
		User32.INSTANCE.GetWindowRect(hwnd, rect);
		
		INPUT input = new INPUT();
		input.type = new DWORD(INPUT.INPUT_MOUSE);
		input.input.setType("mi");
		input.input.mi.dx = new LONG((x + rect.left) * 65536 / User32.INSTANCE.GetSystemMetrics(User32.SM_CXSCREEN));
	    input.input.mi.dy = new LONG((y+ rect.top) * 65536 / User32.INSTANCE.GetSystemMetrics(User32.SM_CYSCREEN));
	    input.input.mi.dwFlags = new DWORD(MOUSEEVENTF_ABSOLUTE | MOUSEEVENTF_MOVE | MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_LEFTUP);
	    input.input.mi.time = new DWORD(0);
		
		INPUT[] inputs = {input};
		User32.INSTANCE.SendInput(new DWORD(inputs.length), inputs, input.size());
	}
}
