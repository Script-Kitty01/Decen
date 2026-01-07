package com.decen.networking;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Streamable
{

    /**
     * 
     *
     * @param out
     *
     * @throws java.io.IOException
     */
    public void toStream(DataOutputStream out) throws IOException;

    /**
     * 
     *
     * @param out
     *
     * @throws java.io.IOException
     */
    public void fromStream(DataInputStream out) throws IOException;
}