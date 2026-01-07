package com.decen.networking;

import com.decen.dht.NodeId;

import java.io.Serializable;
import java.util.List;

/**
 * Unified DHT message container.
 * Pure DTO (no logic).
 */
public class DHTMessage implements Serializable {
    


    /* ================= MESSAGE TYPE ================= */
    private MessageType type;

    /* ================= SENDER ================= */
    private NodeId senderNodeId;
    private String senderIp;
    private int senderPort;

    /* ================= ROUTING ================= */
    private NodeId targetNodeId;        // FIND_NODE

    /* ================= METADATA ================= */
    private String fileId;              // FIND_VALUE / STORE
    private List<String> chunkIds;      // metadata response

    /* ================= CHUNK TRANSFER ================= */
    private String chunkId;             // GET_CHUNK
    private byte[] chunkData;           // CHUNK_RESPONSE

    /* ================= GENERIC PAYLOAD ================= */
    private List<?> payload;            // List<Contact>

    /* ================= KEY EXCHANGE (FUTURE) ================= */
    private byte[] publicKey;
    private byte[] encryptedAesKey;

    /* ================= GETTERS / SETTERS ================= */

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public NodeId getSenderNodeId() { return senderNodeId; }
    public void setSenderNodeId(NodeId senderNodeId) {
        this.senderNodeId = senderNodeId;
    }

    public String getSenderIp() { return senderIp; }
    public void setSenderIp(String senderIp) {
        this.senderIp = senderIp;
    }

    public int getSenderPort() { return senderPort; }
    public void setSenderPort(int senderPort) {
        this.senderPort = senderPort;
    }

    public NodeId getTargetNodeId() { return targetNodeId; }
    public void setTargetNodeId(NodeId targetNodeId) {
        this.targetNodeId = targetNodeId;
    }

    public String getFileId() { return fileId; }
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public List<String> getChunkIds() { return chunkIds; }
    public void setChunkIds(List<String> chunkIds) {
        this.chunkIds = chunkIds;
    }

    public String getChunkId() { return chunkId; }
    public void setChunkId(String chunkId) {
        this.chunkId = chunkId;
    }

    public byte[] getChunkData() { return chunkData; }
    public void setChunkData(byte[] chunkData) {
        this.chunkData = chunkData;
    }

    public List<?> getPayload() { return payload; }
    public void setPayload(List<?> payload) {
        this.payload = payload;
    }

    public byte[] getPublicKey() { return publicKey; }
    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getEncryptedAesKey() { return encryptedAesKey; }
    public void setEncryptedAesKey(byte[] encryptedAesKey) {
        this.encryptedAesKey = encryptedAesKey;
    }
}
