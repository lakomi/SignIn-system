package com.example.SignInsystem.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;


/**
 * @ClassName DESUtil
 * @Description TODO
 * @Author q
 * @Date 18-9-5 下午7:21
 */
public class DESUtil {

    private static final String data = "00-05-5S-6D-BC-RF";
    private static final String key = "12345678";
    private static final String KEY_ALGORITHM = "DES";
    private static final String MODEL = "DES/CBC/PKCS5Padding";

    /**
     * 加密
     * @param content
     * @param keyBytes
     * @return
     */
    public static byte[] DES_CBC_Encrypt(byte[] content, byte[] keyBytes) {
        try {
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance(MODEL);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(keySpec.getKey()));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            System.out.println("exception:" + e.toString());
        }
        return null;
    }

    /**
     * 解密
     * @param content
     * @param keyBytes
     * @return
     */
    private static byte[] DES_CBC_Decrypt(String content, byte[] keyBytes) {
        try {
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance(MODEL);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(keyBytes));
            byte[] result = cipher.doFinal(stringToByteArray(content));
            return result;
        } catch (Exception e) {
            System.out.println("exception:" + e.toString());
        }
        return null;
    }

    /**
     * byte[] 转成 string
     * @param bytes
     * @return
     */
    private static String byteToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTemp;
        for (int i = 0; i < bytes.length; i++) {
            sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * string 转成 byte[]
     * @param s
     * @return
     */
    private static byte[] stringToByteArray(String s){
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 解密并验证mac地址
     * @param encodeMac
     * @param localMac
     * @return
     */
    public static Boolean checkMac(String encodeMac,String localMac){
        /* 解密得到byte[] */
        byte[] decMac = DES_CBC_Decrypt(encodeMac,key.getBytes());
        /* 转成string */
        String decodeMac = new String(decMac);
        System.out.println("解密："+decodeMac);
        if (localMac.equals(decodeMac)){
            return true;
        }else{
            return false;
        }
    }




//    public static void main(String[] args) throws Exception {
//        String str = "bdd22e185842d70339ccf698716c21e3dd02c4a186134bfa";
//        byte[] dec = DES_CBC_Decrypt(str,key.getBytes());
//        System.out.println(new String(dec));
//    }

//    public static void main(String[] args) {

//        byte[] enc = DES_CBC_Encrypt(data.getBytes(),key.getBytes());
//        System.out.println("密文： "+byteToHexString(enc));
//        byte[] dec = DES_CBC_Decrypt(byteToHexString(enc),key.getBytes());
//        System.out.println("明文： "+new String(dec));
//    }





}
