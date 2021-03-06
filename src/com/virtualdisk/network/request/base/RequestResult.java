package com.virtualdisk.network.request.base;

import com.virtualdisk.network.util.*;

import org.jboss.netty.buffer.*;
import static org.jboss.netty.buffer.ChannelBuffers.*;

public abstract class RequestResult
extends Sendable
{
    protected int requestId;
    protected boolean done;
    protected boolean success;

    public RequestResult(int requestId, boolean done, boolean success)
    {
        this.requestId = requestId;
        this.done = done;
        this.success = success;
    }

    public final void setRequestId(int requestId)
    {
        this.requestId = requestId;
    }

    public final int getRequestId()
    {
        return requestId;
    }

    /**
     * @return  true if the request completed or timed out, false otherwise
     */
    public final boolean isDone()
    {
        return done;
    }

    public final void setDone(boolean done)
    {
        this.done = done;
    }

    /**
     * @return  true if the request succeeded, false if it failed for any reason
     */
    public final boolean wasSuccessful()
    {
        return success;
    }

    public final void setSuccess(boolean success)
    {
        this.success = success;
    }

    @Override
    public int messageSize()
    {
        return 4 + 1 + 1;
    }

    @Override
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = dynamicBuffer();
        buffer.writeInt(requestId);
        buffer.writeByte(done ? 1 : 0);
        buffer.writeByte(success ? 1 : 0);

        return buffer;
    }

    @Override
    public void decode(ChannelBuffer buffer)
    {
        requestId = buffer.readInt();
        done = (buffer.readByte() == 1);
        success = (buffer.readByte() == 1);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof RequestResult)
        {
            RequestResult other = (RequestResult) obj;

            return other.canEqual(this)
                && requestId == other.getRequestId()
                && done == other.isDone()
                && success == other.wasSuccessful();
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
        hash = prime*hash + (done ? 1 : 0);
        hash = prime*hash + (success ? 1 : 0);

        return hash;
    }

    public boolean canEqual(Object other)
    {
        return (other instanceof RequestResult);
    }
}

