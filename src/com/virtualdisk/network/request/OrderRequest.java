package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import org.jboss.netty.buffer.*;
import static org.jboss.netty.buffer.ChannelBuffers.*;

import java.util.*;

public class OrderRequest
extends BlockRequest
{
    protected Date timestamp;

    public OrderRequest(int requestId, int volumeId, long logicalOffset, Date timestamp)
    {
        super(requestId, volumeId, logicalOffset);
        this.timestamp = timestamp;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    @Override
    public MessageType messageType()
    {
        return MessageType.orderRequest;
    }

    @Override
    public int messageSize()
    {
        return 8 + super.messageSize();
    }

    @Override
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = super.encode();
        buffer.writeLong(timestamp.getTime());

        return buffer;
    }

    @Override
    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
        timestamp = new Date(buffer.readLong());
    }

    public final boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof OrderRequest)
        {
            OrderRequest other = (OrderRequest) obj;

            return other.canEqual(this)
                && super.equals(other)
                && timestamp.equals(other.getTimestamp());
        }
        else
        {
            return false;
        }
    }

    @Override
    public final int hashCode()
    {
        int hash = super.hashCode();

        long t = timestamp.getTime();
        hash = prime*hash + ((int) (t ^ (t >>> 32)));

        return hash;
    }

    @Override
    public boolean canEqual(Object other)
    {
        return (other instanceof OrderRequest);
    }
}

