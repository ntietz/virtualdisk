package com.virtualdisk.network.request;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class VolumeExistsRequest
extends Request
{
    private int volumeId;
    
    public VolumeExistsRequest(int vid)
    {
        volumeId = vid;
    }
    
    public MessageType messageType()
    {
        return MessageType.volumeExistsRequest;
    }

    public int getVolumeId()
    {
        return volumeId;
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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + volumeId;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof VolumeExistsRequest))
        {
            return false;
        }
        VolumeExistsRequest other = (VolumeExistsRequest) obj;
        if (volumeId != other.volumeId)
        {
            return false;
        }
        return true;
    }
}
