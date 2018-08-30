package ts.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/** 
 * @project Crap
 * 
 * @author Crap
 * 
 * @Copyright 2013 - 2014 All rights reserved. 
 * 
 * @email 422655094@qq.com
 * 
 */
public class RSA {
	public static final String ALGORITHOM="RSA/None/PKCS1Padding";
    
	private static KeyPairGenerator keyPairGenerator;
	
	private static KeyFactory keyFactory;
	/** 默认的安全服务提供者 */
    private static final Provider DEFAULT_PROVIDER = new BouncyCastleProvider();
	static {
        try {
        	keyPairGenerator = KeyPairGenerator.getInstance("RSA", DEFAULT_PROVIDER);
            keyFactory = KeyFactory.getInstance("RSA", DEFAULT_PROVIDER);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }
	
	/**
	 * 生成密钥
	 * @return
	 * @throws Exception
	 */
	public static KeyPair generateKeyPair() {
		keyPairGenerator.initialize(1024, new SecureRandom());
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		
		//generateKeyPairFile(keyPair, "web");
		return keyPair;
	}
	
	/**
     * 返回生成/读取的密钥对文件的路径。
     */
	private static File createFile(String folderName, String fileName) {
        String path = RSA.class.getResource("").getPath() + folderName + "/" + fileName;
        File file = new File(path);
        if (!file.getParentFile().exists()){
        	file.getParentFile().mkdirs();
        	try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return file;
    }
	
	/**
     * 将指定的RSA密钥对以文件形式保存。
     * 
     * @param keyPair 要保存的密钥对。
     */
	public static void generateKeyPairFile(KeyPair keyPair, String folderName){
		try {
			generatePublicKeyFile(keyPair, folderName);
			
			File file = createFile(folderName, "keyPair-rsa");
			FileOutputStream outStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
			
			objectOutputStream.writeObject(keyPair);
			outStream.close();
			objectOutputStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
     * 将指定的RSA密钥对以文件形式保存。
     * 
     * @param keyPair 要保存的密钥对。
     */
	public static void generatePublicKeyFile(KeyPair keyPair, String folderName){
		try {
			String privateKeyStr = Coder.encryptBASE64(keyPair.getPrivate().getEncoded()).replaceAll("\r|\n", "");
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			String publicKeyStr = Coder.encryptBASE64(publicKey.getEncoded()).replaceAll("\r|\n", "");
			String n = publicKey.getModulus().toString();
			String e = publicKey.getPublicExponent().toString();
			
			File file = createFile(folderName, "public-rsa.txt");
			FileOutputStream outStream = new FileOutputStream(file);
			
			outStream.write(("privateKeyEncoded="+privateKeyStr+"\r\n").getBytes());
			outStream.write(("publicKeyEncoded="+publicKeyStr+"\r\n").getBytes());
			outStream.write(("n="+n+"\r\n").getBytes());
			outStream.write(("e="+e+"\r\n").getBytes());
			outStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
     * 加密数据
     * 
     * @param key 密钥
     * @param plaintext 明文字符串
     * @return 密文数据
     */
    public static byte[] encrypt(Key key, String plaintext) throws Exception {
        Cipher ci = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        ci.init(Cipher.ENCRYPT_MODE, key);
        return ci.doFinal(plaintext.getBytes("utf-8"));
    }

    /**
     * 加密数据。
     * 
     * @param keyStr 密钥字符串
     * @param plaintext 明文字符串
     * @param keyType 密钥类型
     * @return 密文数据
     */
	public static byte[] encrypt(String keyStr, String plaintext, Class<? extends Key> keyType) throws Exception{
		//解密密钥
		byte[] keyBytes = Coder.decryptBASE64(keyStr);
		
		Key key = null;
		if(PrivateKey.class.equals(keyType)){
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
			key = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
		}else{
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
			key = keyFactory.generatePublic(x509EncodedKeySpec);
		}
		return encrypt(key, plaintext);
	}	
	
    
	
	
	
    
    /**
     * 解密数据
     * 
     * @param key 密钥(公钥加密则传私钥饥解密,私钥加密则传公钥解密)
     * @param cipherblock 密文的数据
     * @return 明文字符串
     */
    public static byte[] decrypt(Key key, byte[] cipherblock) throws Exception {
        Cipher ci = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        ci.init(Cipher.DECRYPT_MODE, key);
        return ci.doFinal(cipherblock);
    }

    /**
     * 解密数据
     * 
     * @param keyStr 密钥字符串(公钥加密则传私钥饥解密,私钥加密则传公钥解密)
     * @param cipherblock 密文数据
     * @param keyType 密钥类型。
     * @return 明文字符串
     */
	public static byte[] decrypt(String keyStr, byte[] cipherblock, Class<? extends Key> keyType) throws Exception{
		//解密密钥
		byte[] keyBytes = Coder.decryptBASE64(keyStr);
		
		Key key = null;
		if(PrivateKey.class.equals(keyType)){
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
			key = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
		}else{
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
			key = keyFactory.generatePublic(x509EncodedKeySpec);
		}
		return decrypt(key, cipherblock);
	}
	
	public static void main(String[] args) throws Exception {
//		generatePublicKeyFile(generateKeyPair(), "key");
		
		String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJjP4kMf+PzfdlkR5IlSaIqkZHUhvl/KzOKSaAinfkr261BZGo6N7ycAWliOxY9+OoA4+qXs9aTDVeKAe/+PbSkcKB9UpKLKWIIsu+CHtDqI21noLosq5JqOZUJjMm45ZMbYGq39z5ed2Pj+ilL0vrpsHmfv/6ug7q8Voa/EwkIDAgMBAAECgYA5VOVC4d/8n6o7SKdBjhWCzqKR/5L5RZERAHTfqleLsJAgmQ2Jpl6vd0ZuK0Kbz3QLX1VKo1iV9GNok2PyQdFmIj/985TVJFu+PuR0W75y8dBBKgEWMmkpObzrcVX4JiYzWY0pk9NVhWPE96uouT50v0rSTbuxm6L6QPU1fWMb4QJBANVPkfmGxBXv7D9vHASQjG4SX29Hw2Vruifqegbrn6UgXPQ4UG1+NH0P2T2iGwroKmOsgRw/GGcHUahbT8fgzg0CQQC3ZNCeg1rBueTRvlhQIpG9oWzPQKaq8zy933igOowm69D3qTGzKpm4CBxC1k5vSBHeOFRvPSVpaof9dcjeLlxPAkEAkH2yysyf/KAlbp6r4uerSFBHtxBsakH7ulgZSqDG5kqyzHds+442rdRTem1ZVh0HwbazSlfvBrMtfBZGR4We7QJADuMYLEXtcBxu/re844Rq4EHiW1CcNvRX0fH6EMev6Njrta7+YdTrdYzy/ln3NAFdsfRQplcIWm5Ta7fL/n008QJAUb+zhUWp173HUMlpvjjQtvbAl5Wj+//knRD0DdLFtYFLFchttM+Ym1r8Q2AGIgrpIqs/MQB0keEOZztQWv9UDA==";
		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYz+JDH/j833ZZEeSJUmiKpGR1Ib5fyszikmgIp35K9utQWRqOje8nAFpYjsWPfjqAOPql7PWkw1XigHv/j20pHCgfVKSiyliCLLvgh7Q6iNtZ6C6LKuSajmVCYzJuOWTG2Bqt/c+Xndj4/opS9L66bB5n7/+roO6vFaGvxMJCAwIDAQAB";
		byte[] cipherblock = encrypt(privateKey, "傻逼", PrivateKey.class);
		
		
		byte[] plainblock =  decrypt(publicKey, cipherblock, PublicKey.class);
		System.out.println(new String(plainblock, "UTF-8"));
	}
}
