package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

public class VolumeExistsRequestResult
extends VolumeRequestResult
{
    public VolumeExistsRequestResult(int requestId, boolean done, boolean success)
    {
        super(requestId, done, success);
    }

    public MessageType messageType()
    {
        return MessageType.volumeExistsRequestResult;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof VolumeExistsRequestResult)
        {
            VolumeExistsRequestResult other = (VolumeExistsRequestResult) obj;

            return super.equals(other);
        }
        else
        {
            return false;
        }
    }

}

