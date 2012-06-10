package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

public class CreateVolumeRequestResult
extends VolumeRequestResult
{
    public CreateVolumeRequestResult(int requestId, boolean done, boolean success)
    {
        super(requestId, done, success);
    }

    public MessageType messageType()
    {
        return MessageType.createVolumeRequestResult;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof CreateVolumeRequestResult)
        {
            CreateVolumeRequestResult other = (CreateVolumeRequestResult) obj;

            return super.equals(other);
        }
        else
        {
            return false;
        }
    }
}

