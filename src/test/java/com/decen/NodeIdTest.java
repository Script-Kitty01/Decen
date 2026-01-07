package com.decen;

import org.junit.jupiter.api.Test;

import com.decen.dht.NodeId;

import static org.junit.jupiter.api.Assertions.*;

public class NodeIdTest {

    @Test
    void randomConstructorShouldGenerateCorrectLength() {
        NodeId id = new NodeId();
        assertEquals(NodeId.ID_LENGTH_BYTES, id.getBytes().length);
    }

    @Test
    void byteConstructorShouldAcceptCorrectLength() {
        byte[] bytes = new byte[NodeId.ID_LENGTH_BYTES];
        NodeId id = new NodeId(bytes);

        assertArrayEquals(bytes, id.getBytes());
    }

    @Test
    void byteConstructorShouldRejectInvalidLength() {
        byte[] invalid = new byte[10];

        assertThrows(IllegalArgumentException.class, () ->
                new NodeId(invalid)
        );
    }

    @Test
    void getBytesShouldReturnDefensiveCopy() {
        NodeId id = new NodeId();
        byte[] a = id.getBytes();
        byte[] b = id.getBytes();

        a[0] ^= 1; // mutate copy

        assertNotEquals(a[0], b[0]);
    }

    @Test
    void xorWithSelfShouldProduceZeroBytes() {
        NodeId id = new NodeId();
        byte[] xor = id.xor(id);

        for (byte b : xor) {
            assertEquals(0, b);
        }
    }

    @Test
    void xorShouldBeSymmetric() {
        NodeId a = new NodeId();
        NodeId b = new NodeId();

        assertArrayEquals(a.xor(b), b.xor(a));
    }

    @Test
    void equalsAndHashCodeShouldMatch() {
        byte[] bytes = new byte[NodeId.ID_LENGTH_BYTES];
        NodeId a = new NodeId(bytes);
        NodeId b = new NodeId(bytes);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringShouldReturnHexOfCorrectLength() {
        NodeId id = new NodeId();
        String hex = id.toString();

        assertEquals(NodeId.ID_LENGTH_BYTES * 2, hex.length());
        assertTrue(hex.matches("[0-9a-f]+"));
    }
}
