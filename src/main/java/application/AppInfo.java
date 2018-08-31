package application;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

import ts.security.Coder;

public interface AppInfo extends Serializable {
	
	String sn = mac();
	
	String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYz+JDH/j833ZZEeSJUmiKpGR1Ib5fyszikmgIp35K9utQWRqOje8nAFpYjsWPfjqAOPql7PWkw1XigHv/j20pHCgfVKSiyliCLLvgh7Q6iNtZ6C6LKuSajmVCYzJuOWTG2Bqt/c+Xndj4/opS9L66bB5n7/+roO6vFaGvxMJCAwIDAQAB";
	
	static String mac() {
		
		try {
			byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
			StringBuffer sb = new StringBuffer("");
			for(int i=0; i<mac.length; i++) {
				/*if(i!=0) {
					sb.append("-");
				}*/
				//字节转换为整数
				int temp = mac[i]&0xff;
				String str = Integer.toHexString(temp);
				if(str.length()==1) {
					sb.append("0"+str);
				}else {
					sb.append(str);
				}
			}
			return sb.toString().toUpperCase();
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static void main(String[] args) {
		String sn = AppInfo.mac() + UUID.randomUUID().toString().replaceAll("-", "");
		System.out.println(Coder.encryptMD5(sn).toUpperCase());
	}
}
