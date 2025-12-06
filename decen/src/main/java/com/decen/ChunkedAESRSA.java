package com.decen;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.spec.MGF1ParameterSpec;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;
import java.util.Locale;

public class ChunkedAESRSA {
    public static KeyPair savedRSAKeys;


    public static final int CHUNK_SIZE = 1024 * 1024;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private static final String RSA_OAEP_TRANSFORM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final String AES_GCM_TRANSFORM = "AES/GCM/NoPadding";

    public static String storedValue; // <-- THIS IS WHAT YOU WILL SET FROM CONTROLLER

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        return kg.generateKey();
    }

    public static KeyPair generateRSAKeyPair(int bits) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(bits);
        return kpg.generateKeyPair();
    }

    public static byte[] encryptAESKeyWithRSA(SecretKey aesKey, PublicKey rsaPub) throws Exception {
        Cipher rsaCipher = Cipher.getInstance(RSA_OAEP_TRANSFORM);
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                new MGF1ParameterSpec("SHA-256"),
                PSource.PSpecified.DEFAULT
        );
        rsaCipher.init(Cipher.ENCRYPT_MODE, rsaPub, oaepParams);
        return rsaCipher.doFinal(aesKey.getEncoded());
    }

    public static SecretKey decryptAESKeyWithRSA(byte[] encryptedKey, PrivateKey rsaPriv) throws Exception {
        Cipher rsaCipher = Cipher.getInstance(RSA_OAEP_TRANSFORM);
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                new MGF1ParameterSpec("SHA-256"),
                PSource.PSpecified.DEFAULT
        );
        rsaCipher.init(Cipher.DECRYPT_MODE, rsaPriv, oaepParams);
        byte[] aesKeyBytes = rsaCipher.doFinal(encryptedKey);
        return new javax.crypto.spec.SecretKeySpec(aesKeyBytes, "AES");
    }

    public static void encryptFileToChunks(Path inputFile, PublicKey rsaPublic) throws Exception {
        SecretKey aesKey = generateAESKey();

        byte[] encryptedAesKey = encryptAESKeyWithRSA(aesKey, rsaPublic);

        Path aesKeyOut = inputFile.resolveSibling(inputFile.getFileName().toString() + ".aeskey.enc");
        Files.write(aesKeyOut, encryptedAesKey);

        try (InputStream fis = Files.newInputStream(inputFile);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            byte[] buffer = new byte[CHUNK_SIZE];
            int read;
            int chunkIndex = 0;

            while ((read = bis.read(buffer)) > 0) {
                chunkIndex++;

                byte[] realData = Arrays.copyOf(buffer, read);

                byte[] iv = new byte[GCM_IV_LENGTH];
                new SecureRandom().nextBytes(iv);

                Cipher aesCipher = Cipher.getInstance(AES_GCM_TRANSFORM);
                aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

                byte[] cipherText = aesCipher.doFinal(realData);

                String chunkFileName = String.format("%s.chunk%04d.enc", inputFile.getFileName().toString(), chunkIndex);
                Path chunkPath = inputFile.resolveSibling(chunkFileName);

                try (OutputStream os = Files.newOutputStream(chunkPath)) {
                    os.write(iv);
                    os.write(cipherText);
                }
            }
        }
    }

    public static void decryptChunksToFile(Path chunkFileExample, PrivateKey rsaPrivate, Path outputFile) throws Exception {
        String fileName = chunkFileExample.getFileName().toString();
        int idx = fileName.indexOf(".chunk");
        String baseName = fileName.substring(0, idx);

        Path aesKeyPath = chunkFileExample.resolveSibling(baseName + ".aeskey.enc");
        byte[] encryptedAESKey = Files.readAllBytes(aesKeyPath);

        SecretKey aesKey = decryptAESKeyWithRSA(encryptedAESKey, rsaPrivate);

        try (OutputStream fos = Files.newOutputStream(outputFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            int chunkIndex = 1;
            while (true) {
                String chunkName = String.format("%s.chunk%04d.enc", baseName, chunkIndex);
                Path chunkPath = chunkFileExample.resolveSibling(chunkName);

                if (!Files.exists(chunkPath)) break;

                byte[] allBytes = Files.readAllBytes(chunkPath);

                byte[] iv = Arrays.copyOfRange(allBytes, 0, GCM_IV_LENGTH);
                byte[] cipherData = Arrays.copyOfRange(allBytes, GCM_IV_LENGTH, allBytes.length);

                Cipher aesCipher = Cipher.getInstance(AES_GCM_TRANSFORM);
                aesCipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
                byte[] plain = aesCipher.doFinal(cipherData);

                bos.write(plain);

                chunkIndex++;
            }
        }
    }

    // ⭐⭐⭐ USE storedValue HERE ⭐⭐⭐
    public static void main(String[] args) throws Exception {

        if (storedValue == null || storedValue.isEmpty()) {
            System.err.println("ERROR: storedValue not set!");
            System.exit(1);
        }

        Path inputFile = Paths.get(storedValue);

        if (!Files.exists(inputFile)) {
            System.err.println("ERROR: File not found: " + storedValue);
            System.exit(2);
        }

        KeyPair rsaPair = generateRSAKeyPair(2048);

        encryptFileToChunks(inputFile, rsaPair.getPublic());

        Path chunkExample = inputFile.resolveSibling(inputFile.getFileName().toString() + ".chunk0001.enc");
        Path restored = inputFile.resolveSibling(inputFile.getFileName().toString() + ".restored");

        decryptChunksToFile(chunkExample, rsaPair.getPrivate(), restored);

        System.out.println("File encrypted + decrypted successfully.");
    }
}
