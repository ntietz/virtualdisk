package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

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

}

