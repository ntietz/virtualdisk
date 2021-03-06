package com.virtualdisk.network.request.base;

import com.virtualdisk.network.util.*;

import org.jboss.netty.buffer.*;
import static org.jboss.netty.buffer.ChannelBuffers.*;

import java.util.*;

public abstract class Request
extends Sendable
{
    protected int requestId;

    public Request(int requestId)
    {
        this.requestId = requestId;
    }

    /**
     * @return  the request id for this request
     */
    public final int getRequestId()
    {
        return requestId;
    }

    @Override
    public int messageSize()
    {
        return 4;
    }

    @Override
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = dynamicBuffer();
        buffer.writeInt(requestId);

        return buffer;
    }

    @Override
    public void decode(ChannelBuffer buffer)
    {
        requestId = buffer.readInt();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof Request)
        {
            Request other = (Request) obj;

            return other.canEqual(this)
                && requestId == other.getRequestId();
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        int hash = super.hashCode();

        hash = prime*hash + requestId;

        return hash;
    }

    public boolean canEqual(Object other)
    {
        return (other instanceof Request);
    }
}

