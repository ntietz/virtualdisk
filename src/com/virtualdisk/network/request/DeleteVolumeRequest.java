package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import org.jboss.netty.buffer.*;

public class DeleteVolumeRequest
extends VolumeRequest
{
    public DeleteVolumeRequest(int requestId, int volumeId)
    {
        super(requestId, volumeId);
    }

    public MessageType messageType()
    {
        return MessageType.deleteVolumeRequest;
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

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof DeleteVolumeRequest)
        {
            DeleteVolumeRequest other = (DeleteVolumeRequest) obj;

            return super.equals(other);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode();
    }
}

