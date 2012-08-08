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

    @Override
    public MessageType messageType()
    {
        return MessageType.writeRequest;
    }

    @Override
    public int messageSize()
    {
        return 8 + 4 + block.length + super.messageSize();
    }

    @Override
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = super.encode();
        buffer.writeLong(timestamp.getTime());
        buffer.writeInt(block.length);
        buffer.writeBytes(block);

        return buffer;
    }

    @Override
    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
        timestamp = new Date(buffer.readLong());
        int length = buffer.readInt();
        block = new byte[length];
        try
        {
            buffer.readBytes(block);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public final boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof WriteRequest)
        {
            WriteRequest other = (WriteRequest) obj;

            return other.canEqual(this)
                && super.equals(other)
                && timestamp.equals(other.getTimestamp())
                && Arrays.equals(block, other.getBlock());
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

        for (byte each : block)
        {
            hash = prime*hash + (int) each;
        }

        return hash;
    }

    @Override
    public boolean canEqual(Object other)
    {
        return (other instanceof WriteRequest);
    }
}

