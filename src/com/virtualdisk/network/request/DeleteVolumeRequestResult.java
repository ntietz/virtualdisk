package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import org.jboss.netty.buffer.*;
import static org.jboss.netty.buffer.ChannelBuffers.*;

public class DeleteVolumeRequestResult
extends VolumeRequestResult
{
    public DeleteVolumeRequestResult(int requestId, boolean done, boolean success)
    {
        super(requestId, done, success);
    }

    @Override
    public MessageType messageType()
    {
        return MessageType.deleteVolumeRequestResult;
    }

    @Override
    public int messageSize()
    {
        return 0 + super.messageSize();
    }

    @Override
    public ChannelBuffer encode()
    {
        return super.encode();
    }

    @Override
    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
    }

    @Override
    public final boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof DeleteVolumeRequestResult)
        {
            DeleteVolumeRequestResult other = (DeleteVolumeRequestResult) obj;

            return other.canEqual(this)
                && super.equals(other);
        }
        else
        {
            return false;
        }
    }

    @Override
    public final int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public boolean canEqual(Object other)
    {
        return (other instanceof DeleteVolumeRequestResult);
    }
}

