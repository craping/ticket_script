package ts.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.util.encoders.Hex;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

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
public class Coder {
	
	public static final String KEY_SHA="SHA";
	public static final String KEY_MD5="MD5";
	
	/**
	 * Hex解密
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptHex(String data) throws Exception{
		return Hex.decode(data);
	}
	
	/**
	 * Hex加密
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String encryptHex(byte[] data)throws Exception{
		return new String(Hex.encode(data));
	}
	
	/**
	 * BASE64解密
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptBASE64(String key) throws Exception{
		return (new BASE64Decoder()).decodeBuffer(key);
	}
	
	/**
	 * BASE64加密
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptBASE64(byte[] key)throws Exception{
		return (new BASE64Encoder()).encodeBuffer(key);
	}
	
	/**
	 * MD5加密
	 * @param data
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] encryptMD5(byte[] data) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance(KEY_MD5);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		md5.update(data);
		return md5.digest();
	}
	
	/**
	 * MD5加密
	 * @param str
	 * @return String
	 * @throws Exception
	 */
	public static String encryptMD5(String str) {
		byte[] tmp = encryptMD5(str.getBytes());
		StringBuilder sb = new StringBuilder();
		String word;
        for (byte b:tmp) {
        	word = Integer.toHexString(b&0xff);
        	if(word.length() == 1){
        		sb.append("0").append(word);
        		continue;
        	}
        	sb.append(word);
        		
        }
        return sb.toString();
	}
	
	/**
	 * SHA加密
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptSHA(byte[] data)throws Exception{
		MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
		sha.update(data);
		return sha.digest();
	}
	
	
}
