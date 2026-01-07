package com.decen.networking;

import com.decen.dht.Contact;
import com.decen.dht.NodeId;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PeerClient {

    private final NodeId localNodeId;
    private final String localIp;
    private final int localPort;

    public PeerClient(NodeId localNodeId, String localIp, int localPort) {
        this.localNodeId = localNodeId;
        this.localIp = localIp;
        this.localPort = localPort;
    }

    public DHTMessage send(Contact remote, DHTMessage request) throws Exception {

        // ✅ FIXED METHOD NAMES
        request.setSenderNodeId(localNodeId);
        request.setSenderIp(localIp);
        request.setSenderPort(localPort);

        try (Socket socket = new Socket(remote.getIp(), remote.getPort());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(request);
            out.flush();

            return (DHTMessage) in.readObject();
        }
    }
}
