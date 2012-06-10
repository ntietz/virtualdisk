package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

public class CreateVolumeRequest
extends VolumeRequest
{
    public CreateVolumeRequest(int requestId, int volumeId)
    {
        super(requestId, volumeId);
    }

    public MessageType messageType()
    {
        return MessageType.createVolumeRequest;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof CreateVolumeRequest)
        {
            CreateVolumeRequest other = (CreateVolumeRequest) obj;

            return super.equals(other);
        }
        else
        {
            return false;
        }
    }
}

