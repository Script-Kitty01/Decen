package com.decen.dht;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.decen.dht.NodeId;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.Assert.*;

public class FindValueResultTest {

    @Test
    public void testFoundValue() {
        List<String> chunks = Arrays.asList("chunk1", "chunk2", "chunk3");

        FindValueResult result = FindValueResult.foundValue(chunks);

        assertTrue(result.hasValue());
        assertEquals(chunks, result.getChunkIds());
        assertNull(result.getClosestNodes());
    }

    @Test
    public void testFoundNodes() {
        Contact c1 = new Contact(new NodeId(), null, 0);
        Contact c2 = new Contact(new NodeId(), null, 0);

        List<Contact> contacts = Arrays.asList(c1, c2);

        FindValueResult result = FindValueResult.foundNodes(contacts);

        assertFalse(result.hasValue());
        assertEquals(contacts, result.getClosestNodes());
        assertNull(result.getChunkIds());
    }
}
