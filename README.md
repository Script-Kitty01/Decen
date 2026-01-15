# 📁 Decentralized Peer-to-Peer Cloud Drive (Java + DHT)

*A Secure, Decentralized, Peer-to-Peer File Storage System Built Using Java, Kademlia-Inspired DHT, and Socket-Based Networking*

---

## 📝 Overview

This project implements a **fully decentralized cloud storage system** where every participant acts as an equal **peer** in a distributed network.

Unlike centralized services (Google Drive, Dropbox), this system:
- Has **no central file server**
- Uses **peer-to-peer communication**
- Indexes files using a **Kademlia-style Distributed Hash Table (DHT)**
- Encrypts data **locally before sharing**

Files are:
- Split into chunks
- Encrypted using symmetric cryptography
- Addressed using **SHA-256 content hashes**
- Located and retrieved via the DHT

The design is inspired by **IPFS**, **BitTorrent DHT**, and **Amazon Dynamo**, with a focus on correctness, security, and distributed-systems principles.

---

## 🚀 Key Features (Implemented)

### 1. Fully Decentralized Architecture
- No central storage server
- Each peer can:
  - Store chunks
  - Route DHT queries
  - Serve data to other peers
- Bootstrap node is used **only for peer discovery**

### 2. Kademlia-Inspired DHT
- 256-bit keyspace (`NodeId`)
- XOR distance metric
- K-bucket routing table
- `FIND_NODE` and `FIND_VALUE` semantics
- Logarithmic lookup behavior

### 3. Secure File Storage
- Files split into fixed-size chunks
- Each chunk encrypted **locally**
- Encrypted chunks hashed using **SHA-256**
- Encryption keys are **never stored in the DHT**

### 4. Content-Addressed Storage
- File ID = SHA-256 hash
- Chunk ID = SHA-256 hash of encrypted chunk
- Ensures integrity and deterministic lookup

### 5. Peer-to-Peer Networking
- Raw TCP sockets
- Custom message protocol
- Supported message types:
  - STORE
  - FIND_NODE
  - FIND_VALUE
  - GET_CHUNK
  - KEY_REQUEST

### 6. Secure Key Exchange
- AES key stored **only on the file owner**
- Other peers must explicitly request the key
- ECC-based secure key exchange
- No plaintext key transmission

### 7. CLI-Based Interface
- Interactive terminal shell
- Commands:
  - `store <filePath>`
  - `get <fileId> <outputPath>`
  - `routes`
  - `exit`

---

## 🧱 System Architecture
```
┌──────────────────────────────┐
│        CLI Interface         │
│  (store / get / routes)      │
└───────────────┬──────────────┘
                │
                ▼
┌──────────────────────────────┐
│         FileManager          │
│ (Chunking + Encryption)      │
└───────────────┬──────────────┘
                │
                ▼
┌──────────────────────────────────────────┐
│               Core Layer                  │
│                                          │
│  ┌───────────────┐   ┌────────────────┐ │
│  │ Networking    │◄─►│ DHT Engine     │ │
│  │ (TCP Sockets) │   │ (Kademlia)     │ │
│  └───────────────┘   └────────────────┘ │
│                                          │
│  ┌────────────────────────────────────┐ │
│  │          Storage Layer              │ │
│  │  - ChunkStore (encrypted chunks)    │ │
│  │  - MetadataStore (file → chunks)    │ │
│  └────────────────────────────────────┘ │
└──────────────────────────────────────────┘
```

---

## 🧩 Module Breakdown

### client/
- FileManager  
  - Chunking  
  - Encryption / Decryption  
  - Upload & download orchestration  
  - AES key handling  

- PeerController  
  - Bootstrap logic  
  - Peer coordination  

### networking/
- PeerServer – listens for incoming connections  
- PeerClient – sends messages to peers  
- MessageHandler – processes DHT & storage messages  

### dht/
- NodeId – immutable 256-bit identifiers  
- RoutingTable – K-bucket routing  
- DHTNode – lookup and routing logic  

### storage/
- ChunkStore – encrypted chunk persistence  
- MetadataStore – file → chunk mapping  
- FileChunker – split and reassemble logic  

### crypto/
- AESUtil – symmetric encryption  
- ECCKeyExchangeUtil – secure key exchange  
- HashUtil – SHA-256 hashing  

---

## 🔄 How It Works

### Upload Flow
1. User runs `store <file>`
2. File is split into chunks
3. Chunks encrypted locally
4. Encrypted chunks hashed (SHA-256)
5. Metadata stored locally
6. Metadata announced to DHT
7. Encrypted chunks stored across peers

### Download Flow
1. User runs `get <fileId>`
2. DHT queried using FIND_VALUE
3. Chunk IDs returned
4. Chunks fetched from peers
5. AES key obtained securely
6. File decrypted and reassembled

---

## 🛠 Tech Stack

| Component | Technology |
|---------|------------|
| Language | Java 17 |
| Networking | TCP Sockets |
| DHT | Custom Kademlia-Inspired |
| Encryption | AES + ECC |
| Hashing | SHA-256 |
| Build Tool | Maven |
| Interface | CLI |

---

## 📂 Project Structure
```
src/
├── client/
│ ├── FileManager.java
│ └── PeerController.java
├── networking/
│ ├── PeerServer.java
│ ├── PeerClient.java
│ └── MessageHandler.java
├── dht/
│ ├── NodeId.java
│ ├── RoutingTable.java
│ └── DHTNode.java
├── storage/
│ ├── ChunkStore.java
│ ├── MetadataStore.java
│ └── FileChunker.java
└── crypto/
├── AESUtil.java
├── ECCKeyExchangeUtil.java
└── HashUtil.java
```


---

## ▶️ How to Run

### Prerequisites
- JDK 17+
- Maven

### Steps

git clone https://github.com/Script-Kitty01/Decen.git
cd Decen
mvn clean package

powershell
Copy code

Start peers:

java -cp target/classes com.decen.Main 5001
java -cp target/classes com.decen.Main 5002 localhost 5001
java -cp target/classes com.decen.Main 5003 localhost 5001

yaml
Copy code

---

## 🎯 What This Project Demonstrates

- Distributed systems fundamentals  
- Correct DHT implementation  
- Secure peer-to-peer networking  
- Practical cryptography  
- Fault-aware architecture  
- Non-trivial systems engineering  

Ideal for:
- Backend / systems interviews
- Research & higher studies

---

## 📜 Future Enhancements
- Metadata replication (K-factor)
- Owner discovery in metadata
- Automatic re-replication
- JavaFX / Web UI
- NAT traversal (STUN / UPnP)
- Incentive layer (Filecoin-style)

---

## 👨‍💻 Author

**Aamira Bushra M.**  
Developer & System Architect  

⭐ If you like this project, consider starring the repository!
