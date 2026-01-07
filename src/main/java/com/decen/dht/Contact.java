package com.decen.dht;

import java.io.Serializable;

//adv java ref: repo and mcgraw hill java complete ref. v13
public class Contact implements Comparable<Contact>, Serializable{
     private final NodeId nodeId;
    private final String ip;
    private final int port;
     private int staleCount;
private long lastSeen;
     public Contact(NodeId nodeId, String ip, int port) {
        this.nodeId = nodeId;
        this.ip = ip;
        this.port = port;
        this.lastSeen = System.currentTimeMillis() / 1000L;
    }
    public NodeId getNodeId(){
        return this.nodeId;
    }
    public void setSeenNow()
    {
        this.lastSeen = System.currentTimeMillis() / 1000L;
    }
    public long lastSeen()
    {
        return this.lastSeen;
    }
    @Override
    public boolean equals(Object c)
    {
        if (c instanceof Contact)
        {
            return ((Contact) c).getNodeId().equals(this.getNodeId());
        }

        return false;
    }
     public void incrementStaleCount()
    {
        
        staleCount++;
    }
     public int staleCount()
    {
        return this.staleCount;
    }
    public void resetStaleCount()
    {
        this.staleCount = 0;
    }
    @Override
    public int compareTo(Contact o)
    {
        if (this.getNodeId().equals(o.getNodeId()))
        {
            return 0;
        }

        return (this.lastSeen() > o.lastSeen()) ? 1 : -1;
    }
    @Override
    public int hashCode()
    {
        return this.getNodeId().hashCode();
    }
    public String getIp() {
    return ip;
}

public int getPort() {
    return port;
}






}
