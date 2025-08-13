package com.example.bankcards.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptoUtil {

    private static final String AES = "AES";
    private static final String AES_GCM_NOPADDING = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;

    private static final byte[] KEY_BYTES = "0123456789abcdef".getBytes(); // обязательно меняй на свой секрет

    private static final SecretKeySpec SECRET_KEY = new SecretKeySpec(KEY_BYTES, AES);

    public static String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
        byte[] iv = new byte[12]; // в реале — генерируй случайный и храни с шифротекстом
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, spec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
        byte[] iv = new byte[12]; // должен совпадать с тем, что в encrypt
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, spec);
        byte[] decoded = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }
}
