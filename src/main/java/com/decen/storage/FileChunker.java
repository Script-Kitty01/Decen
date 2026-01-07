package com.decen.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public final class FileChunker {

    // 1 MB chunk size only this is the so far only size tested out and works properly without hassle 
    public static final int CHUNK_SIZE = 1024 * 1024;

    private FileChunker() {
        // Prevent instantiation because should be used
    }

    /**
     * Splits a file into fixed-size byte chunks.
     */
    public static List<byte[]> chunkFile(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }

        List<byte[]> chunks = new ArrayList<>();

        try (InputStream in = Files.newInputStream(filePath)) {
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                byte[] chunk = new byte[bytesRead];
                System.arraycopy(buffer, 0, chunk, 0, bytesRead);
                chunks.add(chunk);
            }
        }

        return chunks;
    }

    /**
     * Reassembles chunks back into a file.
     */
    public static void assembleFile(List<byte[]> chunks, Path outputPath)
            throws IOException {

        if (chunks == null || chunks.isEmpty()) {
            throw new IllegalArgumentException("Chunk list is empty");
        }

        try (OutputStream out = Files.newOutputStream(outputPath)) {
            for (byte[] chunk : chunks) {
                out.write(chunk);
            }
        }
    }
    public static void reassembleFile(
        List<byte[]> chunks,
        Path outputPath
) throws IOException {

    Files.createDirectories(outputPath.getParent());

    try (OutputStream out =
                 Files.newOutputStream(
                         outputPath,
                         StandardOpenOption.CREATE,
                         StandardOpenOption.TRUNCATE_EXISTING
                 )) {

        for (byte[] chunk : chunks) {
            out.write(chunk);
        }
    }
}

}
