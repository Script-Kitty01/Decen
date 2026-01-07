package com.decen.networking;

import com.decen.crypto.ECCKeyExchangeUtil;
import com.decen.dht.*;
import com.decen.storage.ChunkStore;
import com.decen.storage.MetadataStore;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

public class MessageHandler {

    private final DHTNode dhtNode;
    private final ChunkStore chunkStore;
    private final MetadataStore metadataStore;
    private final KeyPair localKeyPair;
    private final int k;

    public MessageHandler(
            DHTNode dhtNode,
            ChunkStore chunkStore,
            MetadataStore metadataStore,
            KeyPair localKeyPair,
            int k
    ) {
        this.dhtNode = dhtNode;
        this.chunkStore = chunkStore;
        this.metadataStore = metadataStore;
        this.localKeyPair = localKeyPair;
        this.k = k;
    }

    public DHTMessage handle(DHTMessage request) {

        /* Learn sender */
        if (request.getSenderNodeId() != null) {
            Contact sender = new Contact(
                    request.getSenderNodeId(),
                    request.getSenderIp(),
                    request.getSenderPort()
            );
            dhtNode.store(sender);
        }

        switch (request.getType()) {

            case FIND_NODE:
                return handleFindNode(request);

            case FIND_VALUE:
                return handleFindValue(request);

            case STORE:
                return ack(MessageType.STORE);

            case STORE_CHUNK:
                return handleStoreChunk(request);

            case GET_CHUNK:
                return handleGetChunk(request);

            case KEY_REQUEST:
                return handleKeyRequest(request);

            default:
                return error("Unsupported message type");
        }
    }

    /* ================= HANDLERS ================= */

    private DHTMessage handleFindNode(DHTMessage req) {
        List<Contact> closest =
                dhtNode.findNode(req.getTargetNodeId(), k);

        DHTMessage r = new DHTMessage();
        r.setType(MessageType.FIND_NODE_RESPONSE);
        r.setPayload(closest);
        return r;
    }

    private DHTMessage handleFindValue(DHTMessage req) {
        FindValueResult result =
                dhtNode.findValue(req.getFileId(), k);

        DHTMessage r = new DHTMessage();

        if (result.hasValue()) {
            r.setType(MessageType.FIND_VALUE_RESPONSE);
            r.setChunkIds(result.getChunkIds());
        } else {
            r.setType(MessageType.FIND_NODE_RESPONSE);
            r.setPayload(result.getClosestNodes());
        }
        return r;
    }

    private DHTMessage handleStoreChunk(DHTMessage req) {
        try {
            chunkStore.putChunk(
                    req.getChunkId(),
                    req.getChunkData()
            );
            return ack(MessageType.STORE_CHUNK);
        } catch (Exception e) {
            return error("Failed to store chunk");
        }
    }

    private DHTMessage handleGetChunk(DHTMessage req) {
        try {
            byte[] chunk =
                    chunkStore.getChunk(req.getChunkId());

            DHTMessage r = new DHTMessage();
            r.setType(MessageType.CHUNK_RESPONSE);
            r.setChunkData(chunk); // null allowed = not found
            return r;

        } catch (Exception e) {
            return error("Failed to read chunk");
        }
    }

    private DHTMessage handleKeyRequest(DHTMessage req) {

        try {
            byte[] aesKey =
                    metadataStore.getEncryptedKey(req.getFileId());

            if (aesKey == null) {
                return error("Key not found");
            }

            PublicKey requesterPub =
                    KeyFactory.getInstance("EC")
                            .generatePublic(
                                    new X509EncodedKeySpec(
                                            req.getPublicKey()
                                    )
                            );

            byte[] encrypted =
                    ECCKeyExchangeUtil.encryptAESKey(
                            aesKey,
                            localKeyPair,
                            requesterPub
                    );

            DHTMessage r = new DHTMessage();
            r.setType(MessageType.KEY_RESPONSE);
            r.setEncryptedAesKey(encrypted);
            r.setPublicKey(
                    localKeyPair.getPublic().getEncoded()
            );
            return r;

        } catch (Exception e) {
            return error("Key exchange failed");
        }
    }

    /* ================= HELPERS ================= */

    private DHTMessage ack(MessageType type) {
        DHTMessage r = new DHTMessage();
        r.setType(type);
        return r;
    }

    private DHTMessage error(String msg) {
        DHTMessage r = new DHTMessage();
        r.setType(MessageType.ERROR);
        return r;
    }
}
