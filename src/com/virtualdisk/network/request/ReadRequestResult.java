package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

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

