package com.decen.crypto;
// https://github.com/Kumar-laxmi/Algorithms/blob/main/Java/Cryptography/SHA256.java taken from this repo without cli componenets . Do not write encryption / decryption here in this file only hashing keep all functions in seperate files which are interlinked . each folder should have minimum access to each other!!
// without it is easily crackable with RE ....etc. 
import java.security.MessageDigest;

public final class HashUtil {

    private static final String HASH_ALGO = "SHA-256";

    private HashUtil() {}

    public static byte[] sha256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGO);
        return digest.digest(data);
    }

    public static String sha256Hex(byte[] data) throws Exception {
        byte[] hash = sha256(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            // converting to 2 digit hexa raw data is not safe !!! 
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
