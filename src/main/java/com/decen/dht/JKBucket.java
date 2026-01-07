package com.decen.dht;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Simplified Kademlia K-Bucket implementation.
 *
 * Responsibilities:
 * - Store up to k contacts
 * - Maintain LRU (least-recently-seen) ordering
 * - Use NodeId (via Contact) for identity
 *
 * This implementation intentionally omits:
 * - Replacement cache
 * - Stale counters
 * - Background maintenance
 *
 * These are not required for core Kademlia routing semantics.
 */
public class JKBucket implements KBucket {

    /** Depth of this bucket in the routing table */
    private final int depth;

    /** Maximum number of contacts allowed (k) */
    private final int k;

    /** Contacts stored in LRU order (head = least recent, tail = most recent) */
    private final LinkedList<Contact> contacts;

    /**
     * Create a K-Bucket.
     *
     * @param depth bucket depth (distance range)
     * @param k maximum number of contacts
     */
    public JKBucket(int depth, int k) {
        this.depth = depth;
        this.k = k;
        this.contacts = new LinkedList<>();
    }

    /**
     * Insert a contact using LRU semantics.
     *
     * If contact already exists:
     * - Move it to the most-recently-seen position.
     *
     * If contact is new:
     * - Add it if space exists.
     * - Otherwise evict least-recently-seen contact.
     */
    @Override
    public void insert(Contact c) {
        if (contacts.contains(c)) {
            // Seen again → move to most recent
            contacts.remove(c);
            contacts.addLast(c);
        } else {
            if (contacts.size() < k) {
                contacts.addLast(c);
            } else {
                // Bucket full → evict LRU
                contacts.removeFirst();
                contacts.addLast(c);
            }
        }
    }

    /**
     * Check if a contact exists in this bucket.
     */
    @Override
    public boolean containsContact(Contact c) {
        return contacts.contains(c);
    }

    /**
     * Remove a contact from the bucket.
     *
     * @return true if removed, false otherwise
     */
    @Override
    public boolean removeContact(Contact c) {
        return contacts.remove(c);
    }

    /**
     * @return number of contacts currently stored
     */
    @Override
    public int numContacts() {
        return contacts.size();
    }

    /**
     * @return depth of this bucket
     */
    @Override
    public int getDepth() {
        return depth;
    }

    /**
     * @return a copy of the contact list (to preserve encapsulation)
     */
    @Override
    public List<Contact> getContacts() {
        return new ArrayList<>(contacts);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("KBucket(depth=").append(depth).append(", contacts=").append(contacts.size()).append(")\n");
        for (Contact c : contacts) {
            sb.append("  ").append(c).append("\n");
        }
        return sb.toString();
    }
}
