package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

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

