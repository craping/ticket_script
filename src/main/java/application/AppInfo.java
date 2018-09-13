package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.Date;

import ts.entity.License;
import ts.security.AES;
import ts.security.Coder;
import ts.security.RSA;

public interface AppInfo {
	
	String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYz+JDH/j833ZZEeSJUmiKpGR1Ib5fyszikmgIp35K9utQWRqOje8nAFpYjsWPfjqAOPql7PWkw1XigHv/j20pHCgfVKSiyliCLLvgh7Q6iNtZ6C6LKuSajmVCYzJuOWTG2Bqt/c+Xndj4/opS9L66bB5n7/+roO6vFaGvxMJCAwIDAQAB";
	
	String verson = "1.1.0";
	
	int bit = 32;
	
	static String sn() {
		
		try {
			byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
			StringBuffer sb = new StringBuffer("");
			for(int i=0; i<mac.length; i++) {
				int temp = mac[i]&0xff;
				String str = Integer.toHexString(temp);
				if(str.length()==1) {
					sb.append("0"+str);
				}else {
					sb.append(str);
				}
			}
			return Coder.encryptMD5(sb.toString()).toUpperCase();
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	static void setLicense(License lic) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(lic.getTime());
			oos.flush();
			
			byte[] cipherblock = AES.encrypt(baos.toByteArray(), Coder.decryptBASE64(lic.getKey()));
			String time64 = Coder.encryptBASE64(cipherblock);
			
			fos = new FileOutputStream(System.getProperty("user.dir")+"/License");
			osw = new OutputStreamWriter(fos);
			bw = new BufferedWriter(osw);
			bw.write(lic.getBase64());
			bw.newLine();
			bw.write(time64);
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(bw != null)
					bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if(osw != null)
					osw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if(fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if(oos != null)
					oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if(baos != null)
					baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	static License getLicense() {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		
		License lic = null;
		try {
			fis = new FileInputStream(System.getProperty("user.dir")+"/License");
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
			String license64 = br.readLine();
			String time64 = br.readLine();
			
	        byte[] licenseBlock =  RSA.decrypt(AppInfo.publicKey, Coder.decryptBASE64(license64), PublicKey.class);
	        bais = new ByteArrayInputStream(licenseBlock);
	        ois = new ObjectInputStream(bais);
	        lic = (License) ois.readObject();
	        
	        byte[] timeBlock = AES.decrypt(Coder.decryptBASE64(time64), Coder.decryptBASE64(lic.getKey()));
	        bais = new ByteArrayInputStream(timeBlock);
	        ois = new ObjectInputStream(bais);
	        
	        Date time = (Date) ois.readObject();
	        
	        lic.setTime(time);
	        lic.setBase64(license64);
		} catch (Exception e) {
//			e.printStackTrace();
		}finally {
			try {
				if(br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			try {
				if(isr != null)
					isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if(fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if(bais != null)
					bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if(ois != null)
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lic;
	}
	
	static License getLicense(File file) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		
		License lic = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
			String license64 = br.readLine();
			String time64 = br.readLine();
			
	        byte[] licenseBlock =  RSA.decrypt(AppInfo.publicKey, Coder.decryptBASE64(license64), PublicKey.class);
	        bais = new ByteArrayInputStream(licenseBlock);
	        ois = new ObjectInputStream(bais);
	        lic = (License) ois.readObject();
	        
	        byte[] timeBlock = AES.decrypt(Coder.decryptBASE64(time64), Coder.decryptBASE64(lic.getKey()));
	        bais = new ByteArrayInputStream(timeBlock);
	        ois = new ObjectInputStream(bais);
	        
	        Date time = (Date) ois.readObject();
	        
	        lic.setTime(time);
	        lic.setBase64(license64);
		} catch (Exception e) {
//			e.printStackTrace();
		}finally {
			try {
				if(br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			try {
				if(isr != null)
					isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if(fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if(bais != null)
					bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if(ois != null)
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lic;
	}
}
