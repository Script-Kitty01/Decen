package com.decen.storage;

import java.io.IOException;
import java.nio.file.*;

public class ChunkStore {

    private final Path chunkDir;

    public ChunkStore(Path baseDir) throws IOException {
        this.chunkDir = baseDir.resolve("chunks");
        Files.createDirectories(chunkDir);
    }

    public void putChunk(String chunkId, byte[] data)
            throws IOException {

        Path p = chunkDir.resolve(chunkId);
        Files.write(p, data);
    }

    public byte[] getChunk(String chunkId)
            throws IOException {

        Path p = chunkDir.resolve(chunkId);
        if (!Files.exists(p)) return null;
        return Files.readAllBytes(p);
    }
}
