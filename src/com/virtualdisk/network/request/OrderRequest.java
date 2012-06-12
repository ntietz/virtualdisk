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

    public MessageType messageType()
    {
        return MessageType.orderRequest;
    }

    public int messageSize()
    {
        return 8 + super.messageSize();
    }

    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = super.encode();
        buffer.writeLong(timestamp.getTime());

        return buffer;
    }

    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
        timestamp = new Date(buffer.readLong());
    }

    public ChannelBuffer addHeader(ChannelBuffer buffer)
    {
        byte type = messageType().byteValue();
        int length = messageSize();

        ChannelBuffer header = buffer(5);
        header.writeByte(type);
        header.writeInt(length);

        ChannelBuffer message = copiedBuffer(header, buffer);
        return message;
    }

    public ChannelBuffer encodeWithHeader()
    {
        return addHeader(encode());
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof OrderRequest)
        {
            OrderRequest other = (OrderRequest) obj;

            return super.equals(other)
                && timestamp.equals(other.getTimestamp());
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        int hash = super.hashCode();

        long t = timestamp.getTime();
        hash = prime*hash + ((int) (t ^ (t >>> 32)));

        return hash;
    }
}

