package com.decen.dht;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Unit tests for JKBucket.
 * Focuses on LRU behavior and bucket size enforcement.
 */
public class JKBucketTest {

    private Contact contact(String idSuffix) {
        // Deterministic NodeId for testing
        byte[] bytes = new byte[NodeId.ID_LENGTH_BYTES];
        bytes[0] = (byte) idSuffix.charAt(0);

        NodeId nodeId = new NodeId(bytes);
        InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 8000 + idSuffix.charAt(0));

        return new Contact(nodeId, "127.0.0.1", 8000 + idSuffix.charAt(0));

    }

    @Test
    public void testInsertUntilFull() {
        JKBucket bucket = new JKBucket(0, 3);

        bucket.insert(contact("A"));
        bucket.insert(contact("B"));
        bucket.insert(contact("C"));

        assertEquals(3, bucket.numContacts());
    }

    @Test
    public void testLRUReorderingOnReinsert() {
        JKBucket bucket = new JKBucket(0, 3);

        Contact a = contact("A");
        Contact b = contact("B");
        Contact c = contact("C");

        bucket.insert(a);
        bucket.insert(b);
        bucket.insert(c);

        // Reinsert A (should become most recent)
        bucket.insert(a);

        List<Contact> contacts = bucket.getContacts();

        assertEquals(b, contacts.get(0)); // LRU
        assertEquals(c, contacts.get(1));
        assertEquals(a, contacts.get(2)); // MRU
    }

    @Test
    public void testEvictionWhenFull() {
        JKBucket bucket = new JKBucket(0, 3);

        Contact a = contact("A");
        Contact b = contact("B");
        Contact c = contact("C");
        Contact d = contact("D");

        bucket.insert(a);
        bucket.insert(b);
        bucket.insert(c);

        // Insert D → evict A
        bucket.insert(d);

        List<Contact> contacts = bucket.getContacts();

        assertEquals(3, contacts.size());
        assertFalse(contacts.contains(a));
        assertTrue(contacts.contains(b));
        assertTrue(contacts.contains(c));
        assertTrue(contacts.contains(d));
    }

    @Test
    public void testRemoveContact() {
        JKBucket bucket = new JKBucket(0, 3);

        Contact a = contact("A");
        Contact b = contact("B");

        bucket.insert(a);
        bucket.insert(b);

        boolean removed = bucket.removeContact(a);

        assertTrue(removed);
        assertEquals(1, bucket.numContacts());
        assertFalse(bucket.containsContact(a));
    }

    @Test
    public void testRemoveNonExistingContact() {
        JKBucket bucket = new JKBucket(0, 3);

        Contact a = contact("A");
        Contact b = contact("B");

        bucket.insert(a);

        boolean removed = bucket.removeContact(b);

        assertFalse(removed);
        assertEquals(1, bucket.numContacts());
    }
}
