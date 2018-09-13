package ts.win32;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import javafx.scene.control.Alert;
import ts.entity.Person;
import ts.entity.Train;

public class TicketWin32 {
	
	private static final DirectColorModel SCREENSHOT_COLOR_MODEL = new DirectColorModel(24, 0x00FF0000, 0xFF00, 0xFF);
	private static final int[] SCREENSHOT_BAND_MASKS = {
	        SCREENSHOT_COLOR_MODEL.getRedMask(),
            SCREENSHOT_COLOR_MODEL.getGreenMask(),
            SCREENSHOT_COLOR_MODEL.getBlueMask()
	};
	public static final Map<String, Character> SEAT_MAP = new HashMap<>();
	static {
		SEAT_MAP.put("商务座", '9');
		SEAT_MAP.put("一等座", 'M');
		SEAT_MAP.put("二等座", 'O');
		SEAT_MAP.put("特等座", 'P');
		
		SEAT_MAP.put("硬座", '1');
		SEAT_MAP.put("软座", '2');
		SEAT_MAP.put("硬卧", '3');
		SEAT_MAP.put("软卧", '4');
		SEAT_MAP.put("硬座五座", 'W');
		
	}
	
	public static int search(Train train) {
		HWND FNWNS380 = User32.INSTANCE.FindWindow("FNWNS380", "证件信息录入窗口");
		if(FNWNS380 != null) {
			User32.INSTANCE.SendMessage(FNWNS380, User32.WM_CLOSE, new WPARAM(0), new LPARAM(0));
		}
		
		HWND FNWND380 = User32.INSTANCE.FindWindow("FNWND380", null);
		if(FNWND380 != null) {
			Win32.INSTANCE.SwitchToThisWindow(FNWND380, true);
			
			int total = ticketCount(FNWND380);
			String[] dates = train.getDate();
			String[] nos = train.getNo();
			
			for (String date : dates) {
				
				for (String no : nos) {
					
					//票数
					HWND numEdit = User32.INSTANCE.FindWindowEx(FNWND380, null, "Edit", null);
					while(numEdit != null && !User32.INSTANCE.IsWindowVisible(numEdit)) {
						numEdit = User32.INSTANCE.FindWindowEx(FNWNS380, numEdit, "Edit", null);
					}
					//到站
					HWND toEdit = User32.INSTANCE.FindWindowEx(FNWND380, numEdit, "Edit", null);
					while(toEdit != null && !User32.INSTANCE.IsWindowVisible(toEdit)) {
						toEdit = User32.INSTANCE.FindWindowEx(FNWNS380, toEdit, "Edit", null);
					}
					//发站
					HWND fromEdit = User32.INSTANCE.FindWindowEx(FNWND380, toEdit, "Edit", null);
					while(fromEdit != null && !User32.INSTANCE.IsWindowVisible(fromEdit)) {
						fromEdit = User32.INSTANCE.FindWindowEx(FNWNS380, fromEdit, "Edit", null);
					}
					//车次
					HWND noEdit = User32.INSTANCE.FindWindowEx(FNWND380, fromEdit, "Edit", null);
					while(noEdit != null && !User32.INSTANCE.IsWindowVisible(noEdit)) {
						noEdit = User32.INSTANCE.FindWindowEx(FNWNS380, noEdit, "Edit", null);
					}
					//日期
					HWND dateEdit = User32.INSTANCE.FindWindowEx(FNWND380, noEdit, "Edit", null);
					while(dateEdit != null && !User32.INSTANCE.IsWindowVisible(dateEdit)) {
						dateEdit = User32.INSTANCE.FindWindowEx(FNWNS380, dateEdit, "Edit", null);
					}
					//日期
					Win32.INSTANCE.keybd_event((byte)User32.VK_MENU, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					Win32.INSTANCE.keybd_event((byte)'Q', (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					Win32.INSTANCE.keybd_event((byte)User32.VK_MENU, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					Win32.INSTANCE.keybd_event((byte)'Q', (byte)0, Win32.KEYEVENTF_KEYUP, 0);
//					Win32.INSTANCE.SwitchToThisWindow(dateEdit, true);
					Win32.INSTANCE.SendMessage(dateEdit, Win32.WM_SETTEXT, 0, date);
					//回车
//					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
//					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					
					
					//F2
					Win32.INSTANCE.keybd_event((byte)113, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					Win32.INSTANCE.keybd_event((byte)113, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					Win32.INSTANCE.SwitchToThisWindow(noEdit, true);
					//车次
					Win32.INSTANCE.SendMessage(noEdit, Win32.WM_SETTEXT, 0, no);
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					
					//F3
					Win32.INSTANCE.keybd_event((byte)114, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					Win32.INSTANCE.keybd_event((byte)114, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					//发站
					Win32.INSTANCE.SendMessage(fromEdit, Win32.WM_SETTEXT, 0, train.getFrom());
					//回车
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					
					//F4
					Win32.INSTANCE.keybd_event((byte)115, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					Win32.INSTANCE.keybd_event((byte)115, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					//到站
					Win32.INSTANCE.SendMessage(toEdit, Win32.WM_SETTEXT, 0, train.getTo());
					//回车
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					
					//票数
					Win32.INSTANCE.SendMessage(numEdit, Win32.WM_SETTEXT, 0, "1");
					//回车
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					
					//座位
					Win32.INSTANCE.keybd_event((byte)SEAT_MAP.get(train.getSeat()).charValue(), (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					try {Thread.sleep(50);} catch (InterruptedException e) {}
					Win32.INSTANCE.keybd_event((byte)SEAT_MAP.get(train.getSeat()).charValue(), (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					
					while(true){
						if(Thread.currentThread().isInterrupted())
							return 0;
						
						HWND msgBox = User32.INSTANCE.FindWindow("#32770", "提示");
						if(msgBox != null && User32.INSTANCE.IsWindowVisible(msgBox)) {
							HWND s1 = User32.INSTANCE.FindWindowEx(msgBox, null, "Static", null);
							HWND s2 = User32.INSTANCE.FindWindowEx(msgBox, s1, "Static", null);
							char[] staticTextChar = new char[255];
							Win32.INSTANCE.SendMessage(s2, Win32.WM_GETTEXT, 255, staticTextChar);
							String staticText = Native.toString(staticTextChar);
							
							if(staticText.contains("票已售完")) {
								HWND btn = User32.INSTANCE.FindWindowEx(msgBox, null, "Button", "否(&N)");
								User32.INSTANCE.SendMessage(btn, Win32.BM_CLICK, new WPARAM(0), new LPARAM(0));
								break;
							}else if(staticText.contains("本次取票中有无座")) {
								HWND btn = User32.INSTANCE.FindWindowEx(msgBox, null, "Button", "否(&N)");
								User32.INSTANCE.SendMessage(btn, Win32.BM_CLICK, new WPARAM(0), new LPARAM(0));
								break;
							} else {
								HWND btn = User32.INSTANCE.FindWindowEx(msgBox, null, "Button", null);
								User32.INSTANCE.SendMessage(btn, Win32.BM_CLICK, new WPARAM(0), new LPARAM(0));
								break;
							}
							
						} else {
							int num = ticketCount(FNWND380);
							if(num > total)
								return num - total;
						}
					}
				}
			}
		}
		return 1;
	}
	
	public static int searchRepeat() {
		HWND FNWND380 = User32.INSTANCE.FindWindow("FNWND380", null);
		if(FNWND380 != null) {
			Win32.INSTANCE.SwitchToThisWindow(FNWND380, true);
			int total = ticketCount(FNWND380);
			
			//Esc 当前条件取票
			Win32.INSTANCE.keybd_event((byte)27, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
			Win32.INSTANCE.keybd_event((byte)27, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
			
			while(true){
				if(Thread.currentThread().isInterrupted())
					return 0;
				
				HWND msgBox = User32.INSTANCE.FindWindow("#32770", "提示");
				if(msgBox != null && User32.INSTANCE.IsWindowVisible(msgBox)) {
					HWND s1 = User32.INSTANCE.FindWindowEx(msgBox, null, "Static", null);
					HWND s2 = User32.INSTANCE.FindWindowEx(msgBox, s1, "Static", null);
					char[] staticTextChar = new char[255];
					Win32.INSTANCE.SendMessage(s2, Win32.WM_GETTEXT, 255, staticTextChar);
					String staticText = Native.toString(staticTextChar);
					
					if(staticText.contains("票已售完")) {
						HWND btn = User32.INSTANCE.FindWindowEx(msgBox, null, "Button", "否(&N)");
						User32.INSTANCE.SendMessage(btn, Win32.BM_CLICK, new WPARAM(0), new LPARAM(0));
						break;
					}else if(staticText.contains("本次取票中有无座")) {
						HWND btn = User32.INSTANCE.FindWindowEx(msgBox, null, "Button", "否(&N)");
						User32.INSTANCE.SendMessage(btn, Win32.BM_CLICK, new WPARAM(0), new LPARAM(0));
						break;
					} else {
						HWND btn = User32.INSTANCE.FindWindowEx(msgBox, null, "Button", null);
						User32.INSTANCE.SendMessage(btn, Win32.BM_CLICK, new WPARAM(0), new LPARAM(0));
						break;
					}
					
				} else {
					int num = ticketCount(FNWND380);
					if(num > total)
						return num - total;
				}
			}
		}
		return 1;
	}
	
	public static void pick() {
		HWND FNWND380 = User32.INSTANCE.FindWindow("FNWND380", null);
		if(FNWND380 != null) {
			Win32.INSTANCE.SwitchToThisWindow(FNWND380, true);
			
			Win32.INSTANCE.keybd_event((byte)User32.VK_MENU, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
			Win32.INSTANCE.keybd_event((byte)'N', (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
			
			Win32.INSTANCE.keybd_event((byte)User32.VK_MENU, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
			Win32.INSTANCE.keybd_event((byte)'N', (byte)0, Win32.KEYEVENTF_KEYUP, 0);
		}
	}
	
	public static void clear() {
		HWND FNWNS380 = User32.INSTANCE.FindWindow("FNWNS380", "证件信息录入窗口");
		if(FNWNS380 != null) {
			User32.INSTANCE.SendMessage(FNWNS380, User32.WM_CLOSE, new WPARAM(0), new LPARAM(0));
		}
		
		HWND FNWND380 = User32.INSTANCE.FindWindow("FNWND380", null);
		if(FNWND380 != null) {
			Win32.INSTANCE.SwitchToThisWindow(FNWND380, true);
			
			Win32.INSTANCE.keybd_event((byte)User32.VK_MENU, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
			Win32.INSTANCE.keybd_event((byte)'E', (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
			
			Win32.INSTANCE.keybd_event((byte)User32.VK_MENU, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
			Win32.INSTANCE.keybd_event((byte)'E', (byte)0, Win32.KEYEVENTF_KEYUP, 0);
			
			while(ticketCount(FNWND380) != 0) {
				if(Thread.currentThread().isInterrupted())
					return;
			}
		}
	}
	
	public static String print(Person person) {
		
		HWND FNWNS380 = User32.INSTANCE.FindWindow("FNWNS380", "证件信息录入窗口");
		
		while (FNWNS380 == null || !User32.INSTANCE.IsWindowVisible(FNWNS380)) {
			if(Thread.currentThread().isInterrupted()) {
				return "未执行";
			}
			FNWNS380 = User32.INSTANCE.FindWindow("FNWNS380", "证件信息录入窗口");
		}
		
		
		System.out.println("开始执行....");
		System.out.println("姓名："+person.getName()+"，身份证："+person.getId());
		//设置身份类型下拉
		HWND comboBox = User32.INSTANCE.FindWindowEx(FNWNS380, null, "ComboBox", null);
		char[] cbTextChar = new char[255];
		Win32.INSTANCE.SendMessage(comboBox, Win32.WM_GETTEXT, 255, cbTextChar);
		String cbText = Native.toString(cbTextChar);
		while(comboBox != null && !cbText.equals("ED 二代                                        01")) {
			comboBox = User32.INSTANCE.FindWindowEx(FNWNS380, comboBox, "ComboBox", null);
			Win32.INSTANCE.SendMessage(comboBox, Win32.WM_GETTEXT, 255, cbTextChar);
			cbText = Native.toString(cbTextChar);
		}
		HWND phWnd = Win32.INSTANCE.GetParent(comboBox);
		//CB_SETCURSEL
		if(person.getId().trim().length() == 18) {
			User32.INSTANCE.SendMessage(comboBox, Win32.CB_SETCURSEL, new WPARAM(0), new LPARAM(0));
		} else {
			User32.INSTANCE.SendMessage(comboBox, Win32.CB_SETCURSEL, new WPARAM(11), new LPARAM(0));
		}
		Win32.INSTANCE.SendMessage(phWnd, 0x0111, 0x90000, comboBox);
		Win32.INSTANCE.SendMessage(phWnd, 0x0111, 0x10000, comboBox);
		
		//设置身份证文本框
		HWND idEdit = User32.INSTANCE.FindWindowEx(FNWNS380, null, "Edit", null);
		while(idEdit != null && !User32.INSTANCE.IsWindowVisible(idEdit)) {
			idEdit = User32.INSTANCE.FindWindowEx(FNWNS380, idEdit, "Edit", null);
		}
		Win32.INSTANCE.SwitchToThisWindow(idEdit, true);
		Win32.INSTANCE.SendMessage(idEdit, Win32.WM_SETTEXT, 0, person.getId());
		
		//设置姓名文本框
		HWND nameEdit = User32.INSTANCE.FindWindowEx(FNWNS380, idEdit, "Edit", null);
		while(nameEdit != null && !User32.INSTANCE.IsWindowVisible(nameEdit)) {
			nameEdit = User32.INSTANCE.FindWindowEx(FNWNS380, nameEdit, "Edit", null);
		}
		Win32.INSTANCE.SwitchToThisWindow(nameEdit, true);
		Win32.INSTANCE.SendMessage(nameEdit, Win32.WM_SETTEXT, 0, person.getName());
		
		if(Thread.currentThread().isInterrupted())
			return "未执行";
		
		//确认出票
		HWND ok = User32.INSTANCE.FindWindowEx(FNWNS380, null, "Button", "F4 确认");
		User32.INSTANCE.PostMessage(ok, Win32.BM_CLICK, new WPARAM(0), new LPARAM(0));
		System.out.println("确认出票....");
		
		FNWNS380 = User32.INSTANCE.FindWindow("FNWNS380", "证件信息录入窗口");
		boolean lastVisible = User32.INSTANCE.IsWindowVisible(FNWNS380);
		
		while (true) {
			if(Thread.currentThread().isInterrupted()) {
				return "已执行";
			}
			
			HWND msgBox = User32.INSTANCE.FindWindow("#32770", "提示");
			if(msgBox != null && User32.INSTANCE.IsWindowVisible(msgBox)) {
				HWND s1 = User32.INSTANCE.FindWindowEx(msgBox, null, "Static", null);
				HWND s2 = User32.INSTANCE.FindWindowEx(msgBox, s1, "Static", null);
				char[] staticTextChar = new char[255];
				Win32.INSTANCE.SendMessage(s2, Win32.WM_GETTEXT, 255, staticTextChar);
				String staticText = Native.toString(staticTextChar);
				System.out.println("提示："+staticText);
				
				HWND btn = User32.INSTANCE.FindWindowEx(msgBox, null, "Button", null);
				User32.INSTANCE.SendMessage(btn, Win32.BM_CLICK, new WPARAM(0), new LPARAM(0));
				return staticText;
			} else {
				
				FNWNS380 = User32.INSTANCE.FindWindow("FNWNS380", "证件信息录入窗口");
				boolean winVisible = User32.INSTANCE.IsWindowVisible(FNWNS380);
//					System.out.println("winVisible:"+winVisible);
				
				if(lastVisible) {
					System.out.println("录入窗口隐藏");
					lastVisible = winVisible;
				} else if(FNWNS380 != null && winVisible) {
					//隐藏combobox的下拉
					HWND drop = User32.INSTANCE.FindWindowEx(FNWNS380, null, "ComboBox", null);
					while(drop != null) {
						User32.INSTANCE.SendMessage(drop, Win32.CB_SHOWDROPDOWN, new WPARAM(0), new LPARAM(0));
						drop = User32.INSTANCE.FindWindowEx(FNWNS380, drop, "ComboBox", null);
					}
					
					idEdit = User32.INSTANCE.FindWindowEx(FNWNS380, null, "Edit", null);
					while(idEdit != null && !User32.INSTANCE.IsWindowVisible(idEdit)) {
						idEdit = User32.INSTANCE.FindWindowEx(FNWNS380, idEdit, "Edit", null);
					}
					nameEdit = User32.INSTANCE.FindWindowEx(FNWNS380, idEdit, "Edit", null);
					while(nameEdit != null && !User32.INSTANCE.IsWindowVisible(nameEdit)) {
						nameEdit = User32.INSTANCE.FindWindowEx(FNWNS380, nameEdit, "Edit", null);
					}
					
					char[] idTextChar = new char[255];
					Win32.INSTANCE.SendMessage(idEdit, Win32.WM_GETTEXT, 255, idTextChar);
					String idText = Native.toString(idTextChar);
					
					char[] nameTextChar = new char[255];
					Win32.INSTANCE.SendMessage(nameEdit, Win32.WM_GETTEXT, 255, nameTextChar);
					String nameText = Native.toString(nameTextChar);
					
					System.out.println("idEdit:"+idText);
					System.out.println("nameEdit:"+nameText);
					
					if(idText.equals("") && nameText.equals("")) {
						System.out.println("等待打印票据....");
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
						System.out.println("出票成功.");
						return "成功";
					}
				}
			}
			
		}
	}
	
	public static int ticketCount(HWND FNWND380) {
		int num = 0;
		if(FNWND380 != null) {
			HWND pbdw = User32.INSTANCE.FindWindowEx(FNWND380, null, "pbdw80", null);
			while(pbdw != null && !User32.INSTANCE.IsWindowVisible(pbdw)) {
				pbdw = User32.INSTANCE.FindWindowEx(FNWND380, pbdw, "pbdw80", null);
			}
		
			BufferedImage buffImage = getScreenshot(pbdw);
			ColorModel cm = buffImage.getColorModel();
			WritableRaster wr = buffImage.getRaster();
			
			for (int i = 0; i < 10; i++) {
				if(cm.getRed(wr.getDataElements(0, i*36+18, null)) == 172)
					num++;
				else
					break;
			}
		}
		return num;
	}
	
	public static BufferedImage getScreenshot(HWND target) {
		RECT rect = new RECT();
		if (!User32.INSTANCE.GetWindowRect(target, rect)) {
			throw new Win32Exception(Native.getLastError());
		}
		Rectangle jRectangle = rect.toRectangle();
		int windowWidth = 1;
		int windowHeight = jRectangle.height-32;
		
		if (windowWidth == 0 || windowHeight == 0) {
			throw new IllegalStateException("Window width and/or height were 0 even though GetWindowRect did not appear to fail.");
		}
		
		HDC hdcTarget = User32.INSTANCE.GetDC(target);
		if (hdcTarget == null) {
			throw new Win32Exception(Native.getLastError());
		}

		Win32Exception we = null;

		// device context used for drawing
		HDC hdcTargetMem = null;

		// handle to the bitmap to be drawn to
		HBITMAP hBitmap = null;

		// original display surface associated with the device context
		HANDLE hOriginal = null;

		// final java image structure we're returning.
		BufferedImage image = null;
		
		try {
			hdcTargetMem = GDI32.INSTANCE.CreateCompatibleDC(hdcTarget);
			if (hdcTargetMem == null) {
				throw new Win32Exception(Native.getLastError());
			}

			hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcTarget, windowWidth, windowHeight);
			if (hBitmap == null) {
				throw new Win32Exception(Native.getLastError());
			}

			hOriginal = GDI32.INSTANCE.SelectObject(hdcTargetMem, hBitmap);
			if (hOriginal == null) {
				throw new Win32Exception(Native.getLastError());
			}

			// draw to the bitmap
			if (!GDI32.INSTANCE.BitBlt(hdcTargetMem, 0, 0, windowWidth, windowHeight, hdcTarget, 0, 32, GDI32.SRCCOPY)) {
				throw new Win32Exception(Native.getLastError());
			}

			BITMAPINFO bmi = new BITMAPINFO();
			bmi.bmiHeader.biWidth = windowWidth;
			bmi.bmiHeader.biHeight = -windowHeight;
			bmi.bmiHeader.biPlanes = 1;
			bmi.bmiHeader.biBitCount = 32;
			bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

			Memory buffer = new Memory(windowWidth * windowHeight * 4);
			int resultOfDrawing = GDI32.INSTANCE.GetDIBits(hdcTarget, hBitmap, 0, windowHeight, buffer, bmi,
					WinGDI.DIB_RGB_COLORS);
			if (resultOfDrawing == 0 || resultOfDrawing == WinError.ERROR_INVALID_PARAMETER) {
				throw new Win32Exception(Native.getLastError());
			}

			int bufferSize = windowWidth * windowHeight;
			DataBuffer dataBuffer = new DataBufferInt(buffer.getIntArray(0, bufferSize), bufferSize);
			WritableRaster raster = Raster.createPackedRaster(dataBuffer, windowWidth, windowHeight, windowWidth,
                                                              SCREENSHOT_BAND_MASKS, null);
			image = new BufferedImage(SCREENSHOT_COLOR_MODEL, raster, false, null);

		} catch (Win32Exception e) {
			we = e;
		} finally {
			if (hOriginal != null) {
				// per MSDN, set the display surface back when done drawing
				HANDLE result = GDI32.INSTANCE.SelectObject(hdcTargetMem, hOriginal);
				// failure modes are null or equal to HGDI_ERROR
				if (result == null || WinGDI.HGDI_ERROR.equals(result)) {
					Win32Exception ex = new Win32Exception(Native.getLastError());
					if (we != null) {
						ex.addSuppressed(we);
					}
					we = ex;
				}
			}

			if (hBitmap != null) {
				if (!GDI32.INSTANCE.DeleteObject(hBitmap)) {
					Win32Exception ex = new Win32Exception(Native.getLastError());
					if (we != null) {
						ex.addSuppressed(we);
					}
					we = ex;
				}
			}

			if (hdcTargetMem != null) {
				// get rid of the device context when done
				if (!GDI32.INSTANCE.DeleteDC(hdcTargetMem)) {
					Win32Exception ex = new Win32Exception(Native.getLastError());
					if (we != null) {
						ex.addSuppressed(we);
					}
					we = ex;
				}
			}

			if (hdcTarget != null) {
				if (0 == User32.INSTANCE.ReleaseDC(target, hdcTarget)) {
					throw new IllegalStateException("Device context did not release properly.");
				}
			}
		}

		if (we != null) {
			throw we;
		}
		return image;
	}
	
	public static void main(String[] args) throws Exception {
		
//		HWND hwnd = User32.INSTANCE.FindWindow("ImagePreviewWnd", "图片查看");
		HWND hwnd = User32.INSTANCE.FindWindow("Notepad", "无标题 - 记事本");
//		Win32.INSTANCE.SwitchToThisWindow(Notepad, true);
//		File f =new File(System.getProperty("user.dir")+"/test.bmp");
//		f.mkdirs();
//		ImageIO.write(getScreenshot(hwnd),"bmp", f);
		
//		BufferedImage buffImage = ImageIO.read(new File("E:\\桌面\\ticket_script\\0.8105411285599603.bmp"));
		
		if(hwnd != null) {
			BufferedImage buffImage = getScreenshot(hwnd);
			ColorModel cm = buffImage.getColorModel();
			WritableRaster wr = buffImage.getRaster();
			
			File f =new File(System.getProperty("user.dir")+"/test.bmp");
			f.mkdirs();
			ImageIO.write(buffImage,"bmp", f);
			
			int num = 0;
			for (int i = 0; i < 10; i++) {
				if(cm.getRed(wr.getDataElements(0, i*36+18+34, null)) == 172)
					num++;
				else
					break;
			}
			System.out.println(num+"张");
		}
		while(true) {
			Thread.sleep(50);
			System.out.println(User32.INSTANCE.GetAsyncKeyState(User32.VK_CONTROL) & 0x8000);
//			System.out.println(Win32.INSTANCE.GetKeyState(User32.VK_CONTROL));
		}
		/*String s = "C00185341";
		if(s.matches("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)"))
			System.out.println("身份证");
		if(s.matches("^[a-zA-Z]{5,17}$") || s.matches("^[a-zA-Z0-9]{5,17}$"))
			System.out.println("护照");
		if(s.matches("^[HMChmc]{1}([0-9]{10}|[0-9]{8})$"))
			System.out.println("港澳通行");
		if(s.matches("^[0-9]{8}$") || s.matches("^[0-9]{10}$"))
			System.out.println("台湾通行证");*/
		
		
	}
}
