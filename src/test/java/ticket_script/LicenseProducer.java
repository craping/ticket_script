package ticket_script;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;

import application.AppInfo;
import ts.entity.License;
import ts.security.AES;
import ts.security.Coder;
import ts.security.RSA;

public class LicenseProducer {
	
	public static void main(String[] args) throws Exception {
		String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJjP4kMf+PzfdlkR5IlSaIqkZHUhvl/KzOKSaAinfkr261BZGo6N7ycAWliOxY9+OoA4+qXs9aTDVeKAe/+PbSkcKB9UpKLKWIIsu+CHtDqI21noLosq5JqOZUJjMm45ZMbYGq39z5ed2Pj+ilL0vrpsHmfv/6ug7q8Voa/EwkIDAgMBAAECgYA5VOVC4d/8n6o7SKdBjhWCzqKR/5L5RZERAHTfqleLsJAgmQ2Jpl6vd0ZuK0Kbz3QLX1VKo1iV9GNok2PyQdFmIj/985TVJFu+PuR0W75y8dBBKgEWMmkpObzrcVX4JiYzWY0pk9NVhWPE96uouT50v0rSTbuxm6L6QPU1fWMb4QJBANVPkfmGxBXv7D9vHASQjG4SX29Hw2Vruifqegbrn6UgXPQ4UG1+NH0P2T2iGwroKmOsgRw/GGcHUahbT8fgzg0CQQC3ZNCeg1rBueTRvlhQIpG9oWzPQKaq8zy933igOowm69D3qTGzKpm4CBxC1k5vSBHeOFRvPSVpaof9dcjeLlxPAkEAkH2yysyf/KAlbp6r4uerSFBHtxBsakH7ulgZSqDG5kqyzHds+442rdRTem1ZVh0HwbazSlfvBrMtfBZGR4We7QJADuMYLEXtcBxu/re844Rq4EHiW1CcNvRX0fH6EMev6Njrta7+YdTrdYzy/ln3NAFdsfRQplcIWm5Ta7fL/n008QJAUb+zhUWp173HUMlpvjjQtvbAl5Wj+//knRD0DdLFtYFLFchttM+Ym1r8Q2AGIgrpIqs/MQB0keEOZztQWv9UDA==";
//		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYz+JDH/j833ZZEeSJUmiKpGR1Ib5fyszikmgIp35K9utQWRqOje8nAFpYjsWPfjqAOPql7PWkw1XigHv/j20pHCgfVKSiyliCLLvgh7Q6iNtZ6C6LKuSajmVCYzJuOWTG2Bqt/c+Xndj4/opS9L66bB5n7/+roO6vFaGvxMJCAwIDAQAB";
		
		License lic = new License();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		lic.setTime(cal.getTime());
		
		cal.add(Calendar.DATE, 30);
		
		System.out.printf("%1$tF %1$tT\n", cal.getTime());
//		lic.setSn("E6901E23B7E046E882120C36D42D73BF");
//		lic.setSn("8DD9A9899D80DE172B25E2EE87FBD942");
		lic.setSn(AppInfo.sn());
		
//		lic.setExpire(cal.getTime());
		lic.setKey(Coder.encryptBASE64(AES.generateKey(16)));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(lic);
		oos.flush();
		
		byte[] bytes = baos.toByteArray();
		
		oos.close();
		baos.close();
		byte[] cipherblock = RSA.encrypt(privateKey, bytes, PrivateKey.class);
		String license64 = Coder.encryptBASE64(cipherblock);
		
		baos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(baos);
		oos.writeObject(lic.getTime());
		oos.flush();
		
		bytes = baos.toByteArray();
		oos.close();
		baos.close();
		cipherblock = AES.encrypt(bytes, Coder.decryptBASE64(lic.getKey()));
		String time64 = Coder.encryptBASE64(cipherblock);
		
    	FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir")+"/License");
    	OutputStreamWriter osw = new OutputStreamWriter(fos);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write(license64);
		bw.newLine();
		bw.write(time64);
		
		bw.close();
		osw.close();
        fos.close();
        
        
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"/License");
        
        InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		license64 = br.readLine();
		time64 = br.readLine();
		
		br.close();
		isr.close();
		fis.close();
		
		cipherblock = Coder.decryptBASE64(license64);
        byte[] plainblock =  RSA.decrypt(AppInfo.publicKey, cipherblock, PublicKey.class);
        
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(plainblock);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        lic = (License) objectInputStream.readObject();
        System.out.println(lic.getKey());
        System.out.println();
        
        byteArrayInputStream.close();
        objectInputStream.close();
	}
}
