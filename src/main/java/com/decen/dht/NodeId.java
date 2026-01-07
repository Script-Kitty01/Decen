package com.decen.dht;
import java.io.Serializable;
//in this file nodeid is not using serealizer as it would be unsafe and break the code at many points as observed. in the refressed repo serializer is used as it is very out dated and is more of a very flexible system for apps to incorporate. This file's implimentation eases the load on Node id and insures the principle of least privilage which is being followed to build this whole project . MessageSerealizer.java is in the /networking folder . 
import java.security.SecureRandom;
import java.util.Arrays;

public final class NodeId implements Serializable{
 private static final long serialVersionUID = 1L;
    public static final int ID_LENGTH_BITS = 256;
    public static final int ID_LENGTH_BYTES = ID_LENGTH_BITS / 8;

    private final byte[] keyBytes;

   
    public NodeId() {
        this.keyBytes = new byte[ID_LENGTH_BYTES];
        new SecureRandom().nextBytes(this.keyBytes);
    }

    // Construct NodeId from raw bytes (used by networking layer)
    public NodeId(byte[] bytes) {
        if (bytes.length != ID_LENGTH_BYTES) {
            throw new IllegalArgumentException(
                "NodeId must be exactly " + ID_LENGTH_BYTES + " bytes"
            );
        }
        this.keyBytes = Arrays.copyOf(bytes, bytes.length);
    }

    // Safe accessor
    public byte[] getBytes() {
        return Arrays.copyOf(keyBytes, keyBytes.length);
    }

    // XOR distance (Kademlia core) taken from ref repo.
    public byte[] xor(NodeId other) {
        byte[] result = new byte[ID_LENGTH_BYTES];
        for (int i = 0; i < ID_LENGTH_BYTES; i++) {
            result[i] = (byte) (this.keyBytes[i] ^ other.keyBytes[i]);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NodeId)) return false;
        return Arrays.equals(this.keyBytes, ((NodeId) o).keyBytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(keyBytes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (byte b : keyBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    public static NodeId fromHex(String hex) {

    if (hex.length() != ID_LENGTH_BYTES * 2) {
        throw new IllegalArgumentException(
                "Invalid NodeId hex length: " + hex.length()
        );
    }

    byte[] bytes = new byte[ID_LENGTH_BYTES];

    for (int i = 0; i < bytes.length; i++) {
        int index = i * 2;
        bytes[i] = (byte) Integer.parseInt(
                hex.substring(index, index + 2),
                16
        );
    }

    return new NodeId(bytes);
}

}
