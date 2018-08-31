package ts.win32;

import java.util.function.Predicate;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;

import javafx.collections.transformation.FilteredList;
import ts.entity.Person;
import ts.entity.Train;

public class TicketWin32 {
	
	public static boolean search(Train train) {
		HWND FNWNS380 = User32.INSTANCE.FindWindow("FNWNS380", "证件信息录入窗口");
		if(FNWNS380 != null) {
			User32.INSTANCE.SendMessage(FNWNS380, User32.WM_CLOSE, new WPARAM(0), new LPARAM(0));
		}
		
		HWND FNWND380 = User32.INSTANCE.FindWindow("FNWND380", null);
		if(FNWND380 != null) {
//			FilteredList<Person> undo = train.getPersons().filtered(p -> !p.getStatus().equals("未执行"));
			FilteredList<Person> undo = train.getPersons().filtered(new Predicate<Person>() {
				
				@Override
				public boolean test(Person p) {
					return !p.getStatus().equals("未执行");
				}
			});
			int ticketNum = undo.size() > 10?10:undo.size();
			String[] dates = train.getDate();
			String[] nos = train.getNo();
			
			search:for (String date : dates) {
				
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
					Win32.INSTANCE.SwitchToThisWindow(dateEdit, true);
					Win32.INSTANCE.SendMessage(dateEdit, Win32.WM_SETTEXT, 0, date);
					//回车
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					
					//车次
					Win32.INSTANCE.SwitchToThisWindow(noEdit, true);
					Win32.INSTANCE.SendMessage(noEdit, Win32.WM_SETTEXT, 0, no);
					//回车
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					
					//发站
					Win32.INSTANCE.SwitchToThisWindow(fromEdit, true);
					for (char c : train.getFrom().toCharArray()) {
						User32.INSTANCE.SendMessage(fromEdit, User32.WM_CHAR, new WPARAM((byte)c), new LPARAM(0));
					}
//					Win32.INSTANCE.SendMessage(fromEdit, WM_SETTEXT, 0, train.getFrom());
					//回车
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					
					//到站
					Win32.INSTANCE.SwitchToThisWindow(toEdit, true);
//					Win32.INSTANCE.SendMessage(toEdit, WM_SETTEXT, 0, train.getTo());
					for (char c : train.getTo().toCharArray()) {
						User32.INSTANCE.SendMessage(toEdit, User32.WM_CHAR, new WPARAM((byte)c), new LPARAM(0));
					}
					//回车
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					
					//票数
					Win32.INSTANCE.SwitchToThisWindow(numEdit, true);
					Win32.INSTANCE.SendMessage(numEdit, Win32.WM_SETTEXT, 0, String.valueOf(ticketNum));
					//回车
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					Win32.INSTANCE.keybd_event((byte)13, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					
					Win32.INSTANCE.keybd_event((byte)79, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
					Win32.INSTANCE.keybd_event((byte)79, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
					
					break search;
				}
			}
			
			
//			Win32.INSTANCE.SwitchToThisWindow(FNWND380, true);
//			Win32.INSTANCE.keybd_event((byte)User32.VK_MENU, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
//			Win32.INSTANCE.keybd_event((byte)78, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
//			Win32.INSTANCE.keybd_event((byte)User32.VK_MENU, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
//			Win32.INSTANCE.keybd_event((byte)78, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
		}
		
		return true;
	}
	
	public static String print(Person person) {
		
		HWND FNWNS380 = User32.INSTANCE.FindWindow("FNWNS380", "证件信息录入窗口");
		
		while (FNWNS380 == null || !User32.INSTANCE.IsWindowVisible(FNWNS380)) {
			if(Thread.currentThread().isInterrupted()) {
				return "未执行";
			}
			FNWNS380 = User32.INSTANCE.FindWindow("FNWNS380", "证件信息录入窗口");
		}
		
//		User32.INSTANCE.ShowWindow(FNWNS380, 9);
//		User32.INSTANCE.SetForegroundWindow(FNWNS380);
		
		System.out.println("开始执行....");
		System.out.println("姓名："+person.getName()+"，身份证："+person.getId());
		//设置身份类型下拉
		HWND comboBox = User32.INSTANCE.FindWindowEx(FNWNS380, null, "ComboBox", null);
		char[] cbTextChar = new char[255];
		Win32.INSTANCE.SendMessage(comboBox, Win32.WM_GETTEXT, 255, cbTextChar);
		String cbText = Native.toString(cbTextChar);
		while(comboBox != null && !cbText.equals("ED 二代                                        01")) {
			comboBox = User32.INSTANCE.FindWindowEx(FNWNS380, comboBox, "ComboBox", null);
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
	
	public static void main(String[] args) throws Exception {
		
		HWND Notepad = User32.INSTANCE.FindWindow("Notepad", "无标题 - 记事本");
		Win32.INSTANCE.SwitchToThisWindow(Notepad, true);
		HWND edit = User32.INSTANCE.FindWindowEx(Notepad, null, "Edit", null);
		
		char[] a = "-IOQ".toCharArray();
		for (char c : a) {
			User32.INSTANCE.SendMessage(edit, User32.WM_CHAR, new WPARAM((byte)c), new LPARAM(0));
		}
		
		System.out.println((byte)'-');
		/*String s = "C00185341";
		if(s.matches("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)"))
			System.out.println("身份证");
		if(s.matches("^[a-zA-Z]{5,17}$") || s.matches("^[a-zA-Z0-9]{5,17}$"))
			System.out.println("护照");
		if(s.matches("^[HMChmc]{1}([0-9]{10}|[0-9]{8})$"))
			System.out.println("港澳通行");
		if(s.matches("^[0-9]{8}$") || s.matches("^[0-9]{10}$"))
			System.out.println("台湾通行证");*/
		
		/*boolean visible = false,enabled = false;
		while (true) {
			HWND notify = User32.INSTANCE.FindWindow("#32770", "EditPlus");
			boolean v = User32.INSTANCE.IsWindowVisible(notify);
			if(v != visible) {
				visible = v;
				System.out.println(v);
			}
			boolean e = User32.INSTANCE.IsWindowEnabled(notify);
			if(e != enabled) {
				enabled = e;
				System.out.println(e);
			}
		}*/
//		HWND notify = User32.INSTANCE.FindWindow("#32770", "EditPlus");
//		HWND n1 = User32.INSTANCE.FindWindowEx(notify, null, "Static", null);
//		HWND n2 = User32.INSTANCE.FindWindowEx(notify, n1, "Static", null);
//		char[] staticText = new char[255];
//		Win32.INSTANCE.SendMessage(n2, WM_GETTEXT, 255, staticText);
//		System.out.println(Native.toString(staticText));
//		HWND btn = User32.INSTANCE.FindWindowEx(notify, null, "Button", "取消");
//		User32.INSTANCE.SendMessage(btn, BM_CLICK, new WPARAM(0), new LPARAM(0));
		
		HWND hwnd = User32.INSTANCE.FindWindow("#32770", "Preferences");
		HWND sub1 = User32.INSTANCE.FindWindowEx(hwnd, null, "#32770", null);
		HWND sub2 = User32.INSTANCE.FindWindowEx(sub1, null, "#32770", null);
		if(sub2 != null) {
			HWND sub = User32.INSTANCE.FindWindowEx(sub2, null, "Edit", null);
//			User32.INSTANCE.ShowWindow(hwnd, 9);
//			User32.INSTANCE.SetForegroundWindow(hwnd);
			Win32.INSTANCE.SwitchToThisWindow(sub, true);
//			Win32.INSTANCE.keybd_event((byte)13, (byte)0, KEYEVENTF_KEYDOWN, 0);
//			Win32.INSTANCE.keybd_event((byte)13, (byte)0, KEYEVENTF_KEYUP, 0);
//			Win32.INSTANCE.SendMessage(sub, WM_SETTEXT, 0, Math.random()+"");
			
//			char[] charTitle = new char[255];
//			User32.INSTANCE.GetWindowText(sub, charTitle, 255);
//			System.out.println(Native.toString(charTitle));
//			Win32.INSTANCE.SendMessage(sub, WM_GETTEXT, 255, charTitle);
//			System.out.println(Native.toString(charTitle));
//			
//			sub = User32.INSTANCE.FindWindowEx(sub2, sub, "Edit", null);
//			Win32.INSTANCE.SendMessage(sub, WM_SETTEXT, 0, Math.random()+"");
			
//			sub = User32.INSTANCE.FindWindowEx(sub2, null, "Button", "Open");
//			User32.INSTANCE.SendMessage(sub, BM_CLICK, new WPARAM(0), new LPARAM(0));
		}
		
		
		/*HWND Notepad = User32.INSTANCE.FindWindow("Notepad", "无标题 - 记事本");
//		User32.INSTANCE.ShowWindow(Notepad, 9);
//		User32.INSTANCE.SetForegroundWindow(Notepad);
		HWND edit = User32.INSTANCE.FindWindowEx(Notepad, null, "Edit", null);
		char[] editText = new char[255];
		User32.INSTANCE.SendMessage(edit, User32.WM_CHAR, new WPARAM(65), new LPARAM(0));
//		Win32.INSTANCE.SendMessage(edit, WM_SETTEXT, 0, Math.random()+"");
		Win32.INSTANCE.SendMessage(edit, WM_GETTEXT, 255, editText);
		String text = Native.toString(editText);
		System.out.println(text);*/
	}
}
