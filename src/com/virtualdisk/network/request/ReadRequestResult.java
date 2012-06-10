package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import org.jboss.netty.buffer.*;

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

    public MessageType messageType()
    {
        return MessageType.readRequestResult;
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

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof ReadRequestResult)
        {
            ReadRequestResult other = (ReadRequestResult) obj;

            return super.equals(other)
                && timestamp.equals(other.getTimestamp())
                && Arrays.equals(block, other.getBlock());
        }
        else
        {
            return false;
        }
    }
}

