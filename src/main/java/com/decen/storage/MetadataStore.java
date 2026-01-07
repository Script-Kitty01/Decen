package com.decen.storage;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class MetadataStore {

    private final Path metadataDir;
    private final Path keyDir;

    public MetadataStore(Path baseDir) throws IOException {
        this.metadataDir = baseDir.resolve("metadata");
        this.keyDir = baseDir.resolve("keys");

        Files.createDirectories(metadataDir);
        Files.createDirectories(keyDir);
    }

    /* ================= FILE METADATA ================= */

    public void putFile(String fileId, List<String> chunkIds)
            throws IOException {

        Path file = metadataDir.resolve(fileId + ".meta");
        Files.write(file, chunkIds);
    }

    public List<String> getChunks(String fileId)
            throws IOException {

        Path file = metadataDir.resolve(fileId + ".meta");
        if (!Files.exists(file)) return null;
        return Files.readAllLines(file);
    }

    public boolean hasFile(String fileId) {
        return Files.exists(metadataDir.resolve(fileId + ".meta"));
    }

    /* ================= ENCRYPTED AES KEY ================= */

    public void putEncryptedKey(String fileId, byte[] encryptedKey)
            throws IOException {

        Path keyFile = keyDir.resolve(fileId + ".key");
        Files.write(keyFile, encryptedKey);
    }

    public byte[] getEncryptedKey(String fileId)
            throws IOException {

        Path keyFile = keyDir.resolve(fileId + ".key");
        if (!Files.exists(keyFile)) return null;
        return Files.readAllBytes(keyFile);
    }
}
