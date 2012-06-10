package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

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

}

