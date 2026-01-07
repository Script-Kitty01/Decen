package com.decen.networking;

import com.decen.dht.Contact;
import com.decen.dht.NodeId;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * PeerServer listens for incoming DHT messages.
 */
public class PeerServer implements Runnable {

    private final int port;
    private final NodeId localNodeId;
    private final MessageHandler messageHandler;

    private volatile boolean running = true;

    public PeerServer(int port,
                      NodeId localNodeId,
                      MessageHandler messageHandler) {
        this.port = port;
        this.localNodeId = localNodeId;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[PeerServer] Listening on port " + port);

            while (running) {
                Socket client = serverSocket.accept();
                handleClient(client);
            }
        } catch (Exception e) {
            System.err.println("[PeerServer] Error: " + e.getMessage());
        }
    }

    private void handleClient(Socket client) {
    try {
        ObjectOutputStream out =
                new ObjectOutputStream(client.getOutputStream());
        out.flush(); // 🔴 REQUIRED

        ObjectInputStream in =
                new ObjectInputStream(client.getInputStream());

        /* Read request */
        DHTMessage request = (DHTMessage) in.readObject();

        /* Rebuild sender contact */
        Contact sender = new Contact(
                request.getSenderNodeId(),
                request.getSenderIp(),
                request.getSenderPort()
        );

        /* Handle message */
        DHTMessage response = messageHandler.handle(request);

        /* Attach local identity */
        response.setSenderNodeId(localNodeId);

        /* Send response */
        out.writeObject(response);
        out.flush();

    } catch (Exception e) {
        System.err.println("[PeerServer] Client handling error: " + e);
    } finally {
        try { client.close(); } catch (Exception ignored) {}
    }
}

    public void shutdown() {
        running = false;
    }
}
