 package com.decen.dht;

import java.util.List;

public interface KBucket {
public void insert(Contact c);
public boolean containsContact(Contact c
);
public boolean removeContact(Contact c);
public int numContacts();
public int getDepth();
    public List<Contact> getContacts();
    
}