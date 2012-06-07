package com.virtualdisk.network.request;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class DeleteVolumeRequest
extends Request
{
    private int volumeId;

    public DeleteVolumeRequest(int vid)
    {
        volumeId = vid;
    }
    
    public int volumeId()
    {
        return volumeId;
    }
    
    public MessageType messageType()
    {
        return MessageType.deleteVolumeRequest;
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
