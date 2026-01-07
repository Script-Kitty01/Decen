package com.decen.client;

import com.decen.crypto.AESUtil;
import com.decen.crypto.ECCKeyExchangeUtil;
import com.decen.crypto.HashUtil;
import com.decen.dht.Contact;
import com.decen.networking.DHTMessage;
import com.decen.networking.MessageType;
import com.decen.networking.PeerClient;
import com.decen.storage.ChunkStore;
import com.decen.storage.FileChunker;
import com.decen.storage.MetadataStore;

import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

/**
 * FileManager
 *  - Encrypts files using AES-GCM
 *  - Stores encrypted chunks locally
 *  - Announces metadata to DHT
 *  - Retrieves files using DHT + secure ECC key exchange
 */
public class FileManager {

    private final ChunkStore chunkStore;
    private final MetadataStore metadataStore;
    private final PeerClient peerClient;
    private final Contact bootstrapContact;

    /* Crypto */
    private final AESUtil encryptor = new AESUtil();
    private final KeyPair myKeyPair;

    public FileManager(ChunkStore chunkStore,
                       MetadataStore metadataStore,
                       PeerClient peerClient,
                       Contact bootstrapContact,
                       KeyPair myKeyPair) {

        this.chunkStore = chunkStore;
        this.metadataStore = metadataStore;
        this.peerClient = peerClient;
        this.bootstrapContact = bootstrapContact;
        this.myKeyPair = myKeyPair;
    }

    /* =========================
       STORE FILE
       ========================= */
    public void storeFile(Path filePath) throws Exception {

        /* 1. Chunk file */
        List<byte[]> chunks = FileChunker.chunkFile(filePath);
        List<String> chunkIds = new ArrayList<>();

        /* 2. Generate AES key */
        byte[] aesKey = AESUtil.generateKey();

        /* 3. Encrypt & store chunks */
        for (byte[] chunk : chunks) {

            byte[] encrypted =
                    encryptor.encrypt(chunk, aesKey);

            String chunkId =
                    HashUtil.sha256Hex(encrypted);

            chunkStore.putChunk(chunkId, encrypted);
            chunkIds.add(chunkId);
        }

        /* 4. Generate fileId */
        String fileId =
                HashUtil.sha256Hex(
                        filePath.getFileName()
                                .toString()
                                .getBytes()
                );

        /* 5. Store metadata locally */
        metadataStore.putFile(fileId, chunkIds);
        metadataStore.putEncryptedKey(fileId, aesKey); // owner only

        /* 6. Announce STORE to DHT */
        DHTMessage store = new DHTMessage();
        store.setType(MessageType.STORE);
        store.setFileId(fileId);
        store.setChunkIds(chunkIds);

        peerClient.send(bootstrapContact, store);

        System.out.println("[FileManager] Stored file");
        System.out.println("  fileId = " + fileId);
    }

    /* =========================
       GET FILE
       ========================= */
    public void getFile(String fileId, Path outputPath) throws Exception {

        /* 1. Query DHT */
        DHTMessage find = new DHTMessage();
        find.setType(MessageType.FIND_VALUE);
        find.setFileId(fileId);

        DHTMessage response =
                peerClient.send(bootstrapContact, find);

        if (response.getType() != MessageType.FIND_VALUE_RESPONSE) {
            throw new IllegalStateException(
                    "File not found in DHT"
            );
        }

        List<String> chunkIds =
                response.getChunkIds();

        /* 2. Fetch encrypted chunks */
        List<byte[]> encryptedChunks = new ArrayList<>();

        for (String chunkId : chunkIds) {

            byte[] encrypted =
                    chunkStore.getChunk(chunkId);

            if (encrypted == null) {
                encrypted = fetchChunkFromNetwork(chunkId);
                if (encrypted == null) {
                    throw new IllegalStateException(
                            "Missing chunk: " + chunkId
                    );
                }
                chunkStore.putChunk(chunkId, encrypted);
            }

            encryptedChunks.add(encrypted);
        }

        /* 3. Obtain AES key securely */
        byte[] aesKey = requestAESKeyFromOwner(fileId);

        /* 4. Decrypt chunks */
        List<byte[]> plaintext = new ArrayList<>();
        for (byte[] enc : encryptedChunks) {
            plaintext.add(
                    encryptor.decrypt(enc, aesKey)
            );
        }

        /* 5. Reassemble */
        FileChunker.reassembleFile(
                plaintext,
                outputPath
        );

        System.out.println("[FileManager] Restored file → " + outputPath);
    }

    /* =========================
       NETWORK HELPERS
       ========================= */

    private byte[] fetchChunkFromNetwork(String chunkId) throws Exception {

        DHTMessage findNode = new DHTMessage();
        findNode.setType(MessageType.FIND_NODE);

        DHTMessage response =
                peerClient.send(bootstrapContact, findNode);

        @SuppressWarnings("unchecked")
        List<Contact> peers =
                (List<Contact>) response.getPayload();

        for (Contact peer : peers) {
            try {
                DHTMessage get = new DHTMessage();
                get.setType(MessageType.GET_CHUNK);
                get.setChunkId(chunkId);

                DHTMessage r =
                        peerClient.send(peer, get);

                if (r.getType() == MessageType.CHUNK_RESPONSE &&
                    r.getChunkData() != null) {
                    return r.getChunkData();
                }

            } catch (Exception ignored) {}
        }

        return null;
    }

    private byte[] requestAESKeyFromOwner(String fileId)
            throws Exception {

        DHTMessage req = new DHTMessage();
        req.setType(MessageType.KEY_REQUEST);
        req.setFileId(fileId);
        req.setPublicKey(
                myKeyPair.getPublic().getEncoded()
        );

        DHTMessage resp =
                peerClient.send(bootstrapContact, req);

        PublicKey ownerPub =
                KeyFactory.getInstance("EC")
                        .generatePublic(
                                new X509EncodedKeySpec(
                                        resp.getPublicKey()
                                )
                        );

        return ECCKeyExchangeUtil.decryptAESKey(
                resp.getEncryptedAesKey(),
                myKeyPair,
                ownerPub
        );
    }
}
