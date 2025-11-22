
---

# 📁 **Decentralized Peer-to-Peer Cloud Drive (Java + DHT)**

*A Distributed, Fault-Tolerant, Encrypted File Storage System Built Using Java, Kademlia DHT, and P2P Networking*

---

## 📝 **Overview**

This project implements a **fully decentralized cloud storage system** where every user acts as a **peer** in a distributed network.
Instead of using a central server like Google Drive or Dropbox, files are:

* Split into **chunks**
* **Encrypted** locally
* Distributed across peers using a **Distributed Hash Table (Kademlia DHT)**
* Retrieved efficiently using content addressing (SHA-256 hashes)

The system is fault-tolerant, privacy-preserving, and inspired by real-world technologies like **IPFS**, **BitTorrent**, **Amazon Dynamo**, and **Ethereum’s DHT**.

---

## 🚀 **Key Features**

### 🔹 **1. Decentralized Architecture (No Central Server)**

* Peers communicate directly with each other
* No single point of failure
* Uses Kademlia DHT for distributed storage and lookup

### 🔹 **2. Secure File Storage**

* Files divided into 1MB encrypted chunks
* AES-256 encryption for data
* RSA for key exchange
* SHA-256 hashing for content addressing

### 🔹 **3. Fault Tolerance & Replication**

* Each chunk replicated across multiple nodes
* Automatic re-replication if peers go offline

### 🔹 **4. Efficient Lookup and Retrieval**

* DHT ensures O(log N) lookup time
* Parallel chunk downloading from multiple peers
* Resilient routing and fast reads

### 🔹 **5. Peer Discovery**

* Bootstrap node system
* Heartbeats & liveness detection
* Dynamic routing table maintenance

### 🔹 **6. Clean User Interface**

* JavaFX desktop UI (or React dashboard)
* Upload / Download files
* Visualize peers, chunks, and network status

---

## 🧱 **Architecture**

```
                ┌──────────────────────────────┐
                │        User Interface         │
                │   (JavaFX / Web Dashboard)    │
                └───────────────┬──────────────┘
                                │
                ┌───────────────▼──────────────┐
                │      Client Application       │
                └───────────────┬──────────────┘
                                │
      ┌─────────────────────────▼──────────────────────────┐
      │                  Core Modules                      │
      │  ┌──────────────┬──────────────┬────────────────┐  │
      │  │ Networking    │  DHT Engine  │ Storage Engine │  │
      │  │  (Netty/NIO)  │ (Kademlia)   │ (Chunks + DB)  │  │
      │  └──────────────┴──────────────┴────────────────┘  │
      └─────────────────────────┬──────────────────────────┘
                                │
                ┌───────────────▼──────────────┐
                │         Crypto Module         │
                └──────────────────────────────┘
```

---

## 🛠 **Tech Stack**

### **Programming Language**

* Java 17+

### **Networking**

* Java NIO or Netty (for P2P communication)
* TCP/UDP sockets

### **Distributed Hash Table**

* Custom implementation of **Kademlia DHT**

### **Serialization**

* Protocol Buffers (preferred)
  or
* JSON (Gson / Jackson)

### **Storage**

* Local File System
* H2 / LevelDB (for metadata)

### **Security**

* AES-256 encryption
* RSA-2048 key exchange
* SHA-256 hashing

### **Frontend (Optional)**

* JavaFX
  or
* React.js Dashboard

---

## 📂 **Project Structure**

```
/src
  /networking
      PeerServer.java
      PeerClient.java
      MessageHandler.java

  /dht
      KademliaNode.java
      RoutingTable.java
      KBucket.java
      DHTProtocol.java

  /storage
      FileChunker.java
      ChunkStore.java
      MetadataStore.java

  /crypto
      AESUtil.java
      RSAUtil.java
      HashUtil.java

  /client
      FileManager.java
      PeerController.java

  /ui
      MainUI.java (JavaFX)
```

---

## 🔄 **How It Works**

### **Upload Flow**

1. User selects file
2. File split into 1MB chunks
3. Chunks encrypted using AES
4. Each encrypted chunk hashed (SHA-256)
5. DHT decides which peers will store chunks
6. Chunks sent over P2P network to responsible nodes
7. Metadata saved locally

### **Download Flow**

1. User requests a file
2. DHT queried for chunk locations
3. Chunks downloaded from multiple peers
4. AES decryption applied
5. File reassembled

---

## 💡 **Real World Applications**

* Secure decentralized cloud storage
* Blockchain data networks
* Privacy-preserving file systems
* Peer-to-peer file sharing
* Distributed backup systems
* Offline/mesh network storage

---

## 🎯 **What This Project Demonstrates**

✔ Deep understanding of **distributed systems**
✔ Working **P2P networking** implementation
✔ Efficient implementation of **Kademlia DHT**
✔ Strong knowledge of **cryptography**
✔ End-to-end application development
✔ Fault-tolerant system design
✔ Advanced CS concepts beyond standard college projects

This makes the project *exceptionally strong* for:

* Final Year Project submission
* Job interviews
* GitHub portfolio
* Research or M.Tech applications

---

## ▶️ **How to Run the Project**

### **Prerequisites**

* JDK 17+
* Maven or Gradle
* JavaFX (if using desktop UI)

### **Steps**

```
git clone <repo-url>
cd decentralized-cloud-drive
mvn clean install
java -jar target/peer-node.jar --port=[PORT] --bootstrap=[BOOTSTRAP_IP]
```

To start multiple peers, run:

```
java -jar peer-node.jar --port=5001
java -jar peer-node.jar --port=5002 --bootstrap=localhost:5001
java -jar peer-node.jar --port=5003 --bootstrap=localhost:5001
```

---

## 🧪 **Testing**

* JUnit 5 for module testing
* WireShark for packet inspection
* Docker (optional) for multi-node simulation

---

## 📜 **Future Enhancements**

* File versioning
* NAT traversal (UPnP / STUN)
* Gossip-based replication
* Incentive layer (FileCoin-style)
* WebRTC P2P connections
* Mobile app variant

---

## 👨‍💻 **Contributors**

* **Aamira bushra m.** (Developer & Architect)

---

## ⭐ **If you like this project, give it a star on GitHub!**

---
