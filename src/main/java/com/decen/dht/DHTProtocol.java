package com.decen.dht;

import java.util.List;

/**
 * Logical DHT protocol operations.
 *
 * This interface represents the behavior implemented implicitly
 * across message handlers in the reference Kademlia repo.
 *
 * Networking layer calls these methods.
 */
public interface DHTProtocol {

    /**
     * Equivalent to PingMessage handling.
     */
    void onPing(Contact sender);

    /**
     * Equivalent to FindNodeMessage handling.
     */
    List<Contact> onFindNode(Contact sender, NodeId target, int k);

    /**
     * Equivalent to FindValueMessage handling.
     * Returns either the value OR closest contacts.
     */
    FindValueResult onFindValue(Contact sender, String key);

    /**
     * Equivalent to StoreMessage handling.
     */
    void onStore(Contact sender, String key, byte[] value);
}
