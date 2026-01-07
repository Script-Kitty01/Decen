package com.decen;

import com.decen.client.FileManager;
import com.decen.client.PeerController;
import com.decen.crypto.ECCKeyExchangeUtil;
import com.decen.dht.*;
import com.decen.networking.*;
import com.decen.storage.ChunkStore;
import com.decen.storage.MetadataStore;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.KeyPair;
import java.util.Scanner;

public class Main {

    private static final int K = 20;

    public static void main(String[] args) throws Exception {

        /* ---------- Spring Boot Style Banner ---------- */
        printBanner();

        if (args.length < 1) {
            System.out.println(
                "Usage: java Main <port> [bootstrapHost bootstrapPort]"
            );
            return;
        }

        int port = Integer.parseInt(args[0]);
        String localIp = "localhost";

        /* ---------- Identity ---------- */
        NodeId localNodeId = new NodeId();
        KeyPair nodeKeyPair = ECCKeyExchangeUtil.generateKeyPair();

        /* ---------- Storage ---------- */
        Path basePath = Path.of("data_" + port);
        MetadataStore metadataStore = new MetadataStore(basePath);
        ChunkStore chunkStore = new ChunkStore(basePath);

        /* ---------- Routing ---------- */
        RoutingTable routingTable = new RoutingTable(localNodeId, K);

        /* ---------- DHT Core ---------- */
        DHTNode dhtNode =
                new DHTNode(localNodeId, routingTable, metadataStore);

        /* ---------- Message Handling ---------- */
        MessageHandler handler =
                new MessageHandler(
                        dhtNode,
                        chunkStore,
                        metadataStore,
                        nodeKeyPair,
                        K
                );

        /* ---------- Networking ---------- */
        PeerServer server =
                new PeerServer(port, localNodeId, handler);
        new Thread(server).start();

        Thread.sleep(100); // ensure server binds

        PeerClient peerClient =
                new PeerClient(localNodeId, localIp, port);

        PeerController peerController =
                new PeerController(localNodeId, dhtNode, peerClient);

        System.out.println("================================");
        System.out.println("Node started");
        System.out.println("NodeId = " + localNodeId);
        System.out.println("Port   = " + port);
        System.out.println("================================");

        /* ---------- Bootstrap ---------- */
        Contact bootstrapContact = null;

        if (args.length == 3) {
            String bootstrapHost = args[1];
            int bootstrapPort = Integer.parseInt(args[2]);

            peerController.bootstrap(bootstrapHost, bootstrapPort);

            bootstrapContact =
                    new Contact(
                            new NodeId(), // placeholder
                            bootstrapHost,
                            bootstrapPort
                    );
        }

        if (bootstrapContact == null) {
            System.out.println("Waiting as bootstrap node...");
            Thread.currentThread().join();
            return;
        }

        /* ---------- FileManager ---------- */
        FileManager fileManager =
                new FileManager(
                        chunkStore,
                        metadataStore,
                        peerClient,
                        bootstrapContact,
                        nodeKeyPair
                );

        /* ---------- CLI ---------- */
        System.out.println("================================");
        System.out.println("Decen CLI ready");
        System.out.println("Commands:");
        System.out.println(" store <filePath>");
        System.out.println(" get <fileId> <outputPath>");
        System.out.println(" routes");
        System.out.println(" exit");
        System.out.println("================================");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");

            try {
                switch (parts[0].toLowerCase()) {

                    case "store":
                        if (parts.length != 2) {
                            System.out.println("Usage: store <filePath>");
                            break;
                        }
                        fileManager.storeFile(Path.of(parts[1]));
                        break;

                    case "get":
                        if (parts.length != 3) {
                            System.out.println(
                                "Usage: get <fileId> <outputPath>"
                            );
                            break;
                        }
                        fileManager.getFile(
                                parts[1],
                                Path.of(parts[2])
                        );
                        break;

                    case "routes":
                        System.out.println("==== ROUTING TABLE ====");
                        System.out.println(routingTable);
                        break;

                    case "exit":
                        System.out.println("Shutting down...");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Unknown command");
                }

            } catch (Exception e) {
                System.err.println("Error:");
                e.printStackTrace();
            }
        }
    }

    /* ---------- Banner Loader (Spring Boot Style) ---------- */
    private static void printBanner() {
        try (InputStream is =
                     Main.class.getClassLoader()
                             .getResourceAsStream("banner.txt")) {

            if (is != null) {
                String banner =
                        new String(is.readAllBytes(),
                                StandardCharsets.UTF_8);
                System.out.println(banner);
            }
        } catch (Exception ignored) {
        }
    }
}
