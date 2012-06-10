package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

public class DeleteVolumeRequestResult
extends VolumeRequestResult
{
    public DeleteVolumeRequestResult(int requestId, boolean done, boolean success)
    {
        super(requestId, done, success);
    }

    public MessageType messageType()
    {
        return MessageType.deleteVolumeRequestResult;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof DeleteVolumeRequestResult)
        {
            DeleteVolumeRequestResult other = (DeleteVolumeRequestResult) obj;

            return super.equals(other);
        }
        else
        {
            return false;
        }
    }

}

