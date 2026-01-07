package com.decen;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import com.decen.storage.MetadataStore;

/**
 * Simple test class for MetadataStore.
 * Run this as a normal Java application.
 */
public class MetadataStoreTest {

    public static void main(String[] args) {
        try {
            runAllTests();
            System.out.println("\n✅ ALL MetadataStore tests PASSED");
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED");
            e.printStackTrace();
        }
    }

    private static void runAllTests() throws Exception {
        testPutAndGet();
        testHasFile();
        testRemoveFile();
    }

    /**
     * Test storing and retrieving metadata.
     */
    private static void testPutAndGet() throws Exception {
        System.out.println("Running testPutAndGet...");

        Path baseDir = Path.of("test_node_data");
        MetadataStore metadataStore = new MetadataStore(baseDir);

        String fileId = "file123";
        List<String> chunks = Arrays.asList("chunkA", "chunkB", "chunkC");

        metadataStore.putFile(fileId, chunks);

        List<String> loadedChunks = metadataStore.getChunks(fileId);

        if (!chunks.equals(loadedChunks)) {
            throw new AssertionError("Chunk list mismatch");
        }

        System.out.println("✔ testPutAndGet passed");
    }

    /**
     * Test checking if metadata exists.
     */
    private static void testHasFile() throws Exception {
        System.out.println("Running testHasFile...");

        Path baseDir = Path.of("test_node_data");
        MetadataStore metadataStore = new MetadataStore(baseDir);

        String fileId = "fileExists";
        List<String> chunks = Arrays.asList("c1", "c2");

        metadataStore.putFile(fileId, chunks);

        if (!metadataStore.hasFile(fileId)) {
            throw new AssertionError("hasFile() returned false for existing file");
        }

        System.out.println("✔ testHasFile passed");
    }

    /**
     * Test removing metadata.
     */
    private static void testRemoveFile() throws Exception {
        System.out.println("Running testRemoveFile...");

        Path baseDir = Path.of("test_node_data");
        MetadataStore metadataStore = new MetadataStore(baseDir);

        String fileId = "fileToRemove";
        List<String> chunks = Arrays.asList("x1", "x2", "x3");

        metadataStore.putFile(fileId, chunks);
        metadataStore.removeFile(fileId);

        if (metadataStore.hasFile(fileId)) {
            throw new AssertionError("File metadata was not removed");
        }

        System.out.println("✔ testRemoveFile passed");
    }
}
