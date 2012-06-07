package com.virtualdisk.network.request;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.util.Date;

public class OrderRequest
extends Request
{
    private int volumeId;
    private int logicalOffset;
    private Date timestamp;

    public OrderRequest(int v, int l, Date t)
    {
        volumeId = v;
        logicalOffset = l;
        timestamp = t;
    }

    public MessageType messageType()
    {
        return MessageType.orderRequest;
    }

    public int getVolumeId()
    {
        return volumeId;
    }

    public int getLogicalOffset()
    {
        return logicalOffset;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }
    
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();

        buffer.writeInt(volumeId);
        buffer.writeInt(logicalOffset);
        buffer.writeLong(timestamp.getTime());
        
        return buffer;
    }
    
    public boolean decode(ChannelBuffer buffer)
    {
        if (buffer.readableBytes() < 16)
        {
            return false;
        }
        else
        {
            volumeId = buffer.readInt();
            logicalOffset = buffer.readInt();
            timestamp = new Date(buffer.readLong());
            return true;
        }
    }
}

