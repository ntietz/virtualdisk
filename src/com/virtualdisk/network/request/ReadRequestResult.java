package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import org.jboss.netty.buffer.*;
import static org.jboss.netty.buffer.ChannelBuffers.*;

import java.util.*;

public class ReadRequestResult
extends BlockRequestResult
{
    protected Date timestamp;
    protected byte[] block;

    public ReadRequestResult( int requestId
                            , boolean done
                            , boolean success
                            , Date timestamp
                            , byte[] block
                            )
    {
        super(requestId, done, success);
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
        return MessageType.readRequestResult;
    }

    @Override
    public int messageSize()
    {
        int blockSize = (block != null) ? block.length : 0;
        return 8 + 4 + blockSize + super.messageSize();
    }

    @Override
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = super.encode();
        buffer.writeLong(timestamp.getTime());
        if (block != null && block.length > 0)
        {
            buffer.writeInt(block.length);
            buffer.writeBytes(block);
        }
        else
        {
            buffer.writeInt(0);
        }

        return buffer;
    }

    @Override
    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
        timestamp = new Date(buffer.readLong());
        int length = buffer.readInt();
        if (length > 0)
        {
            block = new byte[length];
            buffer.readBytes(block);
        }
        else
        {
            block = null;
        }
    }

    @Override
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

    @Override
    public ChannelBuffer encodeWithHeader()
    {
        return addHeader(encode());
    }

    @Override
    public final boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof ReadRequestResult)
        {
            ReadRequestResult other = (ReadRequestResult) obj;

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
        return (other instanceof ReadRequestResult);
    }
}

