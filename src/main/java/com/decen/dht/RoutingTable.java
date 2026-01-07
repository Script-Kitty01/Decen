package com.decen.dht;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simplified Kademlia Routing Table.
 *
 * - Maintains k-buckets indexed by XOR distance
 * - Inserts contacts into correct buckets
 * - Finds closest contacts to a target NodeId
 *
 * This implementation intentionally avoids:
 * - KadConfiguration
 * - Node / networking logic
 * - Replacement cache
 * - Stale contact handling
 */
public class RoutingTable {

    /** Total number of bits in NodeId (256) */
    private static final int ID_BITS = NodeId.ID_LENGTH_BITS;

    /** Local node identity */
    private final NodeId localNodeId;

    /** Array of k-buckets */
    private final KBucket[] buckets;

    /**
     * Create a routing table.
     *
     * @param localNodeId NodeId of this node
     * @param k maximum contacts per bucket
     */
    public RoutingTable(NodeId localNodeId, int k) {
        this.localNodeId = localNodeId;
        this.buckets = new KBucket[ID_BITS];

        for (int i = 0; i < ID_BITS; i++) {
            buckets[i] = new JKBucket(i, k);
        }
    }

    /**
     * Insert a contact into the routing table.
     */
    public void insert(Contact contact) {
        if (contact == null) return;

        int bucketId = getBucketId(contact.getNodeId());
        buckets[bucketId].insert(contact);
    }

    /**
     * Compute bucket index using XOR distance.
     *
     * Bucket index = position of first differing bit.
     */
    public int getBucketId(NodeId target) {
        if (target.equals(localNodeId)) {
            return 0;
        }

        byte[] distance = localNodeId.xor(target);

        for (int byteIndex = 0; byteIndex < distance.length; byteIndex++) {
            int value = distance[byteIndex] & 0xFF;
            if (value != 0) {
                int bitIndex = Integer.numberOfLeadingZeros(value) - 24;
                return byteIndex * 8 + bitIndex;
            }
        }

        // Should never happen unless IDs are identical
        return ID_BITS - 1;
    }

    /**
     * Find the closest contacts to a target NodeId.
     *
     * @param target NodeId to compare against
     * @param count max number of contacts to return
     */
    public List<Contact> findClosest(NodeId target, int count) {
        List<Contact> allContacts = getAllContacts();

        Collections.sort(allContacts, (c1, c2) ->
                compareDistance(
                        localNodeId.xor(c1.getNodeId()),
                        localNodeId.xor(c2.getNodeId())
                )
        );

        if (allContacts.size() <= count) {
            return allContacts;
        }
        return new ArrayList<>(allContacts.subList(0, count));
    }

    /**
     * Collect all contacts from all buckets.
     */
    public List<Contact> getAllContacts() {
        List<Contact> result = new ArrayList<>();

        for (KBucket bucket : buckets) {
            result.addAll(bucket.getContacts());
        }

        return result;
    }

    /**
     * @return all k-buckets
     */
    public KBucket[] getBuckets() {
        return buckets;
    }

    /**
     * Compare two XOR distances represented as byte arrays.
     * Comparison is unsigned and lexicographical (big-endian).
     */
    private static int compareDistance(byte[] a, byte[] b) {
        for (int i = 0; i < a.length; i++) {
            int ai = a[i] & 0xFF;
            int bi = b[i] & 0xFF;
            if (ai != bi) {
                return Integer.compare(ai, bi);
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RoutingTable\n");
        for (KBucket bucket : buckets) {
            if (bucket.numContacts() > 0) {
                sb.append("Bucket ")
                  .append(bucket.getDepth())
                  .append(": ")
                  .append(bucket.numContacts())
                  .append(" contacts\n");
            }
        }
        return sb.toString();
    }
}
