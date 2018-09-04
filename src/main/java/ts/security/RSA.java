package ts.security;

import java.io.ByteArrayOutputStream;
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
        Cipher cipher = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        byte[] cipherblock = plaintext.getBytes("utf-8");
        int inputLen = cipherblock.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;

        for(int i = 0; inputLen - offSet > 0; offSet = i * 116) {
            byte[] cache;
            if(inputLen - offSet > 116) {
                cache = cipher.doFinal(cipherblock, offSet, 116);
            } else {
                cache = cipher.doFinal(cipherblock, offSet, inputLen - offSet);
            }

            out.write(cache, 0, cache.length);
            ++i;
        }

        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 加密数据
     * 
     * @param key 密钥
     * @param plaintext 明文字符串
     * @return 密文数据
     */
    public static byte[] encrypt(Key key, byte[] cipherblock) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        int inputLen = cipherblock.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;

        for(int i = 0; inputLen - offSet > 0; offSet = i * 116) {
            byte[] cache;
            if(inputLen - offSet > 116) {
                cache = cipher.doFinal(cipherblock, offSet, 116);
            } else {
                cache = cipher.doFinal(cipherblock, offSet, inputLen - offSet);
            }

            out.write(cache, 0, cache.length);
            ++i;
        }

        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
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
     * 加密数据。
     * 
     * @param keyStr 密钥字符串
     * @param plaintext 明文字符串
     * @param keyType 密钥类型
     * @return 密文数据
     */
	public static byte[] encrypt(String keyStr, byte[] cipherblock, Class<? extends Key> keyType) throws Exception{
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
		return encrypt(key, cipherblock);
	}
	
	
	
    
    /**
     * 解密数据
     * 
     * @param key 密钥(公钥加密则传私钥饥解密,私钥加密则传公钥解密)
     * @param cipherblock 密文的数据
     * @return 明文字符串
     */
    public static byte[] decrypt(Key key, byte[] cipherblock) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, key);
        
        int inputLen = cipherblock.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;

        for(int i = 0; inputLen - offSet > 0; offSet = i * 128) {
            byte[] cache;
            if(inputLen - offSet > 128) {
                cache = cipher.doFinal(cipherblock, offSet, 128);
            } else {
                cache = cipher.doFinal(cipherblock, offSet, inputLen - offSet);
            }

            out.write(cache, 0, cache.length);
            ++i;
        }

        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
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
}
