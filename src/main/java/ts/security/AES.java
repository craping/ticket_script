package ts.security;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

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
public class AES {
	
	//public static final String ALGORITHOM = "AES/CBC/NoPadding";
	public static final String ALGORITHOM = "AES/ECB/PKCS5Padding";
	public static byte[] iv = {
		0x38, 0x37, 0x36, 0x35, 
		0x34, 0x33, 0x32, 0x31, 
		0x38, 0x37, 0x36, 0x35, 
		0x34, 0x33, 0x32, 0x31
	};
	
	/**
     * 解密数据。
     * 
     * @param size 密钥大小
     * @return 密钥
     */
	public static byte[] generateKey(int size){
		byte[] key = new byte[size];
		new SecureRandom().nextBytes(key);
		return key;
	}
	
	/**
     * 加密数据
     * 
     * @param plaintext 明文字符串
     * @param key 密钥数据
     * @return 密文数据。
     */
	public static byte[] encrypt(byte[] plainblock, byte[] key) {
		SecretKeySpec spec = new SecretKeySpec(key, "AES");

		try {
			
			Cipher cipher = Cipher.getInstance(ALGORITHOM);
			cipher.init(Cipher.ENCRYPT_MODE, spec);
			
			byte[] byteEnc = cipher.doFinal(plainblock);
			
			return  byteEnc;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	/**
     * 解密数据
     * 
     * @param cipherblock 密文数据
     * @param key 密钥数据
     * @return 明文字符串
     */
	public static byte[] decrypt(byte[] cipherblock, byte[] key) {
		SecretKeySpec spec = new SecretKeySpec(key, "AES");
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHOM);
			cipher.init(Cipher.DECRYPT_MODE, spec);
			
			return cipher.doFinal(cipherblock);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
     * 解密数据
     * 
     * @param cipherblock 密文数据
     * @param key 密钥数据
     * @return 明文字符串
     */
	public static String decryptJS(byte[] cipherblock, byte[] key) {
	    
		SecretKeySpec spec = new SecretKeySpec(key, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding",new BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, spec);
			
			byte[] byteEnc = cipher.doFinal(cipherblock);
			
			return new String(byteEnc).trim();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
	}
}
