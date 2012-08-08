package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import org.jboss.netty.buffer.*;
import static org.jboss.netty.buffer.ChannelBuffers.*;

public class DeleteVolumeRequest
extends VolumeRequest
{
    public DeleteVolumeRequest(int requestId, int volumeId)
    {
        super(requestId, volumeId);
    }

    @Override
    public MessageType messageType()
    {
        return MessageType.deleteVolumeRequest;
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
        else if (obj instanceof DeleteVolumeRequest)
        {
            DeleteVolumeRequest other = (DeleteVolumeRequest) obj;

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
        return (other instanceof DeleteVolumeRequest);
    }
}

