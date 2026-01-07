package com.decen.client;

import com.decen.dht.Contact;
import com.decen.dht.DHTNode;
import com.decen.dht.NodeId;
import com.decen.networking.*;

import java.util.ArrayList;
import java.util.List;

public class PeerController {
    private static final int ALPHA = 3; // parallelism (standard Kademlia)


    private final NodeId localNodeId;
    private final DHTNode dhtNode;
    private final PeerClient peerClient;

    public PeerController(NodeId localNodeId,
                          DHTNode dhtNode,
                          PeerClient peerClient) {
        this.localNodeId = localNodeId;
        this.dhtNode = dhtNode;
        this.peerClient = peerClient;
    }

    /**
     * Bootstrap into the DHT using a known peer.
     */
    public void bootstrap(String ip, int port) throws Exception {

        System.out.println("[BOOTSTRAP] Connecting to " + ip + ":" + port);

        // Temporary contact (NodeId will be learned)
        Contact bootstrapContact =
                new Contact(null, ip, port);

        // Build FIND_NODE request
        DHTMessage request = new DHTMessage();
        request.setType(MessageType.FIND_NODE);
        request.setTargetNodeId(localNodeId);

        // Send request
        DHTMessage response =
                peerClient.send(bootstrapContact, request);

        // 1️⃣ Learn bootstrap node identity
        NodeId bootstrapNodeId = response.getSenderNodeId();
        Contact realBootstrapContact =
                new Contact(bootstrapNodeId, ip, port);

        dhtNode.store(realBootstrapContact);

        // 2️⃣ Insert returned contacts
        List<?> payload = response.getPayload();
        if (payload != null) {
            for (Object o : payload) {
                if (o instanceof Contact) {
                    dhtNode.store((Contact) o);
                }
            }
        }

        System.out.println("[BOOTSTRAP] Completed");
        dhtNode.printRoutingTable();
    }
    public List<String> iterativeFindValue(String fileId) throws Exception {

    System.out.println("[LOOKUP] FIND_VALUE " + fileId);

    List<Contact> shortlist =
            dhtNode.findNode(localNodeId, 20);

    List<Contact> queried = new ArrayList<>();

    while (!shortlist.isEmpty()) {

        Contact contact = shortlist.remove(0);

        if (queried.contains(contact)) {
            continue;
        }

        queried.add(contact);

        /* Build FIND_VALUE request */
        DHTMessage request = new DHTMessage();
        request.setType(MessageType.FIND_VALUE);
        request.setFileId(fileId);

        DHTMessage response;

        try {
            response = peerClient.send(contact, request);
        } catch (Exception e) {
            continue; // node unreachable
        }

        /* CASE 1: Value found */
        if (response.getChunkIds() != null &&
            !response.getChunkIds().isEmpty()) {

            System.out.println("[LOOKUP] VALUE FOUND");
            return response.getChunkIds();
        }

        /* CASE 2: Closer nodes returned */
        if (response.getPayload() instanceof List<?>) {

            @SuppressWarnings("unchecked")
            List<Contact> closer =
                    (List<Contact>) response.getPayload();

            for (Contact c : closer) {
                if (!queried.contains(c) && !shortlist.contains(c)) {
                    shortlist.add(c);
                }
            }
        }
    }

    System.out.println("[LOOKUP] VALUE NOT FOUND");
    return null;
}

}
