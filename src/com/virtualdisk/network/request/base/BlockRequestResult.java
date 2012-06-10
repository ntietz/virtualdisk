package com.virtualdisk.network.request.base;

import org.jboss.netty.buffer.*;

public abstract class BlockRequestResult
extends RequestResult
{
    public BlockRequestResult(int requestId, boolean done, boolean success)
    {
        super(requestId, done, success);
    }

    public int messageSize()
    {
        return 0 + super.messageSize();
    }

    public ChannelBuffer encode()
    {
        return super.encode();
    }

    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
    }
}

