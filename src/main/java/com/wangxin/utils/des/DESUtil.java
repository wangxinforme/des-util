package com.wangxin.utils.des;

import com.wangxin.utils.Base64Util;
import com.wangxin.utils.HexUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

/**
 * DES加密
 */
public class DESUtil {

    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "DES";

    /**
     * NoPadding
     * API或算法本身不对数据进行处理，加密数据由加密双方约定填补算法。例如若对字符串数据进行加解密，可以补充\0或者空格，然后trim
     * PKCS5Padding
     * 加密前：数据字节长度对8取余，余数为m，若m>0,则补足8-m个字节，字节数值为8-m，即差几个字节就补几个字节，字节数值即为补充的字节数，若为0则补充8个字节的8
     */
    private static final String CIPHER_ALGORITHM = "DES/CBC/NoPadding";

    private static Key getKey(String key) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        return secretKeySpec;
    }

    private static AlgorithmParameterSpec getIV(String iv) {
        IvParameterSpec parameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        return parameterSpec;
    }

    //此函数是pkcs7padding填充函数
    public static String pkcs7padding(String data) {
        int bs = 16;
        int padding = bs - (data.length() % bs);
        String padding_text = "";
        for (int i = 0; i < padding; i++) {
            padding_text += (char) padding;
        }
        return data + padding_text;
    }

    /**
     * 加密
     *
     * @param data 字符串
     * @param key  密钥
     * @param iv   IV偏移量
     * @return 密文字符串
     * @throws Exception
     */
    public static String encrypt(String data, String key, String iv) {
        String input = pkcs7padding(data);
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = input.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(key), getIV(iv));

            byte[] encrypted = cipher.doFinal(plaintext);
            String hexStr = HexUtil.byte2hex(encrypted);
            return Base64Util.string2base64(hexStr);
        } catch (Exception e) {
            System.err.println("# DES加密失败");
            return null;
        }
    }

    /**
     * 解密
     *
     * @param data 密文字符串
     * @param key  密钥
     * @param iv   IV偏移量
     * @return 字符串
     * @throws Exception
     */
    public static String decrypt(String data, String key, String iv) {
        try {
            String base64 = Base64Util.base642string(data);
            byte[] hexStr = HexUtil.hex2byte(base64);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKey(key), getIV(iv));
            byte[] original = cipher.doFinal(hexStr);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("# DES解密失败");
            return null;
        }
    }


    public static void main(String[] args) {
        String KEY = "12345678";
        String IV = "12345678";
        String mobile = "13918492887";
        System.out.println(mobile);
        String enString = encrypt(mobile, KEY, IV);
        System.out.println(enString);
        String deString = decrypt(enString, KEY, IV);
        System.out.println(deString);
    }

}
