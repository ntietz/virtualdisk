package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import org.jboss.netty.buffer.*;
import static org.jboss.netty.buffer.ChannelBuffers.*;

import java.util.*;

public class WriteRequest
extends BlockRequest
{
    protected Date timestamp;
    protected byte[] block;

    public WriteRequest(int requestId, int volumeId, long logicalOffset, Date timestamp, byte[] block)
    {
        super(requestId, volumeId, logicalOffset);
        this.timestamp = timestamp;
        this.block = block;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public byte[] getBlock()
    {
        return block;
    }

    public MessageType messageType()
    {
        return MessageType.writeRequest;
    }

    public int messageSize()
    {
        return 8 + 4 + block.length + super.messageSize();
    }

    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = super.encode();
        buffer.writeLong(timestamp.getTime());
        buffer.writeInt(block.length);
        buffer.writeBytes(block);

        return buffer;
    }

    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
        timestamp = new Date(buffer.readLong());
        int length = buffer.readInt();
        block = new byte[length];
        buffer.readBytes(block);
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
        else if (obj instanceof WriteRequest)
        {
            WriteRequest other = (WriteRequest) obj;

            return super.equals(other)
                && timestamp.equals(other.getTimestamp())
                && Arrays.equals(block, other.getBlock());
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

        for (byte each : block)
        {
            hash = prime*hash + (int) each;
        }

        return hash;
    }
}

