package com.kanshushenqi;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * 域
 */
class Domain {
    public static final String URL = decode("0azCR7lR92r42u83Zge8rO48bmQzD+F2K/TEgWm6RfM=");

    private static final String KEY = "rRDB457kgPxW1zMsVnscHDgG";
    private static final String IV = "GM8LtuBQ";

    /**
     * 解码
     */
    private static String decode(String src) {
        String domain = null;

        try {
            domain = new String(decrypt(Base64.getDecoder().decode(src)), "UTF-8");
        }
        catch (GeneralSecurityException e) {
            /**
             * ignore
             */
        }
        catch (UnsupportedEncodingException e) {
            /**
             * ignore
             */
        }

        return domain;
    }

    /**
     * 解密
     */
    private static byte[] decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException,
            BadPaddingException, IllegalBlockSizeException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
        SecretKey secretKey = factory.generateSecret(new DESedeKeySpec(KEY.getBytes()));

        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV.getBytes()));

        return cipher.doFinal(data);
    }
}
