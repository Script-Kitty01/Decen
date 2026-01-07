package com.decen.crypto;

import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AESUtil implements ChunkEncryption {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int KEY_SIZE = 256;

    public AESUtil() {}

    public static byte[] generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(KEY_SIZE);
        SecretKey key = keyGen.generateKey();
        return key.getEncoded();
    }

    @Override
    public byte[] encrypt(byte[] plaintext, byte[] key) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(
            Cipher.ENCRYPT_MODE,
            new SecretKeySpec(key, "AES"),
            new GCMParameterSpec(TAG_LENGTH_BIT, iv)
        );

        byte[] ciphertext = cipher.doFinal(plaintext);

        byte[] output = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, output, 0, iv.length);
        System.arraycopy(ciphertext, 0, output, iv.length, ciphertext.length);

        return output;
    }

    @Override
    public byte[] decrypt(byte[] encryptedChunk, byte[] key) throws Exception {
        byte[] iv = Arrays.copyOfRange(encryptedChunk, 0, IV_LENGTH_BYTE);
        byte[] ciphertext = Arrays.copyOfRange(encryptedChunk, IV_LENGTH_BYTE, encryptedChunk.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(
            Cipher.DECRYPT_MODE,
            new SecretKeySpec(key, "AES"),
            new GCMParameterSpec(TAG_LENGTH_BIT, iv)
        );

        return cipher.doFinal(ciphertext);
    }
}
