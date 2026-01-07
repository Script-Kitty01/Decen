package com.decen.crypto;

/**
 * Interface for chunk-level encryption.
 */
public interface ChunkEncryption {

    byte[] encrypt(byte[] plaintext, byte[] key) throws Exception;

    byte[] decrypt(byte[] ciphertext, byte[] key) throws Exception;
}
