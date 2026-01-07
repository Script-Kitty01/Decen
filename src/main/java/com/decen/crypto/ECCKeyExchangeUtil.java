package com.decen.crypto;

import javax.crypto.KeyAgreement;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.spec.ECGenParameterSpec;

public final class ECCKeyExchangeUtil {

    private static final String CURVE = "secp256r1";

    private ECCKeyExchangeUtil() {}

    // Generate EC key pair
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(new ECGenParameterSpec(CURVE));
        return kpg.generateKeyPair();
    }

    // Derive shared AES key
    public static byte[] deriveAESKey(
            KeyPair ownKeyPair,
            PublicKey peerPublicKey
    ) throws Exception {

        KeyAgreement ka = KeyAgreement.getInstance("ECDH");
        ka.init(ownKeyPair.getPrivate());
        ka.doPhase(peerPublicKey, true);

        byte[] sharedSecret = ka.generateSecret();

        // Hash → 256-bit AES key
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        return sha256.digest(sharedSecret);
    }
    public static byte[] encryptAESKey(
        byte[] aesKey,
        PrivateKey ownPrivate,
        PublicKey peerPublic
) throws Exception {

    byte[] sharedSecret =
            deriveAESKey(new KeyPair(peerPublic, ownPrivate), peerPublic);

    // XOR encryption (safe here because key is random)
    byte[] encrypted = new byte[aesKey.length];
    for (int i = 0; i < aesKey.length; i++) {
        encrypted[i] = (byte) (aesKey[i] ^ sharedSecret[i]);
    }
    return encrypted;
}
public static byte[] decryptAESKey(
        byte[] encryptedKey,
        PrivateKey ownPrivate,
        PublicKey peerPublic
) throws Exception {

    byte[] sharedSecret =
            deriveAESKey(new KeyPair(peerPublic, ownPrivate), peerPublic);

    byte[] decrypted = new byte[encryptedKey.length];
    for (int i = 0; i < encryptedKey.length; i++) {
        decrypted[i] = (byte) (encryptedKey[i] ^ sharedSecret[i]);
    }
    return decrypted;
}
public static byte[] encryptAESKey(
        byte[] aesKey,
        KeyPair senderKeyPair,
        PublicKey receiverPublicKey
) throws Exception {

    byte[] sharedSecret =
            deriveAESKey(senderKeyPair, receiverPublicKey);

    // XOR is fine for prototype; replace with AES-GCM later
    byte[] encrypted = new byte[aesKey.length];
    for (int i = 0; i < aesKey.length; i++) {
        encrypted[i] = (byte) (aesKey[i] ^ sharedSecret[i % sharedSecret.length]);
    }
    return encrypted;
}

public static byte[] decryptAESKey(
        byte[] encryptedAesKey,
        KeyPair receiverKeyPair,
        PublicKey senderPublicKey
) throws Exception {

    byte[] sharedSecret =
            deriveAESKey(receiverKeyPair, senderPublicKey);

    byte[] aesKey = new byte[encryptedAesKey.length];
    for (int i = 0; i < encryptedAesKey.length; i++) {
        aesKey[i] = (byte) (encryptedAesKey[i] ^ sharedSecret[i % sharedSecret.length]);
    }
    return aesKey;
}

}
