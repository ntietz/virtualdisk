package com.virtualdisk.network.request;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class CreateVolumeRequest
extends Request
{
    private int volumeId;
 
    public CreateVolumeRequest(int vid)
    {
        volumeId = vid;
    }

    public int volumeId()
    {
        return volumeId;
    }

    public MessageType messageType()
    {
        return MessageType.createVolumeRequest;
    }
    
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        
        buffer.writeInt(volumeId);
        
        return buffer;
    }
    
    public boolean decode(ChannelBuffer buffer)
    {
        if (buffer.readableBytes() < 4)
        {
            return false;
        }
        else
        {
            volumeId = buffer.readInt();
            return true;
        }
    }
}
