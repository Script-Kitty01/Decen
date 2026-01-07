package com.decen.dht;

import java.util.List;

/**
 * Result wrapper for FIND_VALUE operation in DHT.
 *
 * Either:
 *  - contains the value (chunk IDs)
 *  - OR contains closest nodes
 */
public final class FindValueResult {

    private final boolean hasValue;
    private final List<String> chunkIds;
    private final List<Contact> closestNodes;

    private FindValueResult(boolean hasValue,
                            List<String> chunkIds,
                            List<Contact> closestNodes) {
        this.hasValue = hasValue;
        this.chunkIds = chunkIds;
        this.closestNodes = closestNodes;
    }

    /* =========================
       FACTORY METHODS
       ========================= */

    public static FindValueResult foundValue(List<String> chunkIds) {
        return new FindValueResult(true, chunkIds, null);
    }

    public static FindValueResult foundNodes(List<Contact> contacts) {
        return new FindValueResult(false, null, contacts);
    }

    /* =========================
       ACCESSORS
       ========================= */

    public boolean hasValue() {
        return hasValue;
    }

    public List<String> getChunkIds() {
        return chunkIds;
    }

    public List<Contact> getClosestNodes() {
        return closestNodes;
    }
}
