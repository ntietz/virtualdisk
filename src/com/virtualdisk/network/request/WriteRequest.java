package com.virtualdisk.network.request;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.util.Arrays;
import java.util.Date;

public class WriteRequest
extends Request
{
    private int volumeId;
    private int logicalOffset;
    private byte[] block;
    private Date timestamp;

    public WriteRequest(int v, int l, byte[] b, Date t)
    {
        volumeId = v;
        logicalOffset = l;
        block = b;
        timestamp = t;
    }

    public MessageType messageType()
    {
        return MessageType.writeRequest;
    }

    public int getVolumeId()
    {
        return volumeId;
    }

    public int getLogicalOffset()
    {
        return logicalOffset;
    }

    public byte[] getBlock()
    {
        return block;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }
    
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        
        buffer.writeInt(block.length);
        buffer.writeBytes(block);
        buffer.writeInt(volumeId);
        buffer.writeInt(logicalOffset);
        buffer.writeLong(timestamp.getTime());
        
        return buffer;
    }
    
    public boolean decode(ChannelBuffer buffer)
    {
        if (buffer.readableBytes() < 4)
        {
            return false;
        }
        
        int length = buffer.readInt();
        
        if (buffer.readableBytes() < length + 16)
        {
            buffer.resetReaderIndex();
            return false;
        }
        else
        {
            block = new byte[length];
            buffer.readBytes(block);
            volumeId = buffer.readInt();
            logicalOffset = buffer.readInt();
            timestamp = new Date(buffer.readLong());
            return true;
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(block);
        result = prime * result + logicalOffset;
        result = prime * result
                + ((timestamp == null) ? 0 : timestamp.hashCode());
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
        if (!(obj instanceof WriteRequest))
        {
            return false;
        }
        WriteRequest other = (WriteRequest) obj;
        if (!Arrays.equals(block, other.block))
        {
            return false;
        }
        if (logicalOffset != other.logicalOffset)
        {
            return false;
        }
        if (timestamp == null)
        {
            if (other.timestamp != null)
            {
                return false;
            }
        } else if (!timestamp.equals(other.timestamp))
        {
            return false;
        }
        if (volumeId != other.volumeId)
        {
            return false;
        }
        return true;
    }
}

