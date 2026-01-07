package com.decen.dht;

import com.decen.storage.MetadataStore;

import java.util.List;

/**
 * DHTNode represents the local node's DHT logic.
 *
 * Responsibilities:
 * - Maintain routing table
 * - Answer DHT queries (STORE, FIND_NODE, FIND_VALUE)
 *
 * It does NOT handle networking or storage of chunk bytes.
 */
public class DHTNode {

    private final NodeId localNodeId;
    private final RoutingTable routingTable;
    private final MetadataStore metadataStore;

    public DHTNode(NodeId localNodeId,
                   RoutingTable routingTable,
                   MetadataStore metadataStore) {

        this.localNodeId = localNodeId;
        this.routingTable = routingTable;
        this.metadataStore = metadataStore;
    }

    /* ============================
       DHT OPERATIONS
       ============================ */

    /**
     * FIND_NODE operation (Kademlia)
     * Returns closest known contacts to a target NodeId.
     */
    public List<Contact> findNode(NodeId target, int k) {
        return routingTable.findClosest(target, k);
    }

    /**
     * FIND_VALUE operation (Kademlia)
     *
     * Key is treated as a FILE ID.
     *
     * If metadata exists locally → return chunk IDs
     * Else → return closest nodes
     */
    public FindValueResult findValue(String fileId, int k) {
        try {
            if (metadataStore.hasFile(fileId)) {
                List<String> chunkIds = metadataStore.getChunks(fileId);
                return FindValueResult.foundValue(chunkIds);
            }
        } catch (Exception e) {
            // Metadata read failure → behave like value not found
        }

       NodeId keyId = NodeId.fromHex(fileId);
    List<Contact> closest = routingTable.findClosest(keyId, k);

    return FindValueResult.foundNodes(closest);
    }

    /**
     * STORE operation (logical)
     * Actual chunk storage happens elsewhere.
     */
    public void store(Contact contact) {
        routingTable.insert(contact);
    }

    public NodeId getLocalNodeId() {
        return localNodeId;
    }
    public void printRoutingTable() {
    System.out.println("==== ROUTING TABLE ====");
    System.out.println(routingTable);
}
public DHTNode(NodeId localNodeId, MetadataStore metadataStore) {
    this.localNodeId = localNodeId;
    this.routingTable = new RoutingTable(localNodeId, 20);

    this.metadataStore = metadataStore;
}
/**
 * STORE operation
 * Stores file metadata locally.
 */
public void storeFile(String fileId, List<String> chunkIds) {
    try {
        metadataStore.putFile(fileId, chunkIds);
        System.out.println("[DHTNode] Stored metadata for file " + fileId);
    } catch (Exception e) {
        System.err.println("[DHTNode] Failed to store metadata: " + e.getMessage());
    }
}


}
