package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import org.jboss.netty.buffer.*;

public class VolumeExistsRequest
extends VolumeRequest
{
    public VolumeExistsRequest(int requestId, int volumeId)
    {
        super(requestId, volumeId);
    }

    public MessageType messageType()
    {
        return MessageType.volumeExistsRequest;
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
        else if (obj instanceof VolumeExistsRequest)
        {
            VolumeExistsRequest other = (VolumeExistsRequest) obj;

            return super.equals(other);
        }
        else
        {
            return false;
        }
    }

}

