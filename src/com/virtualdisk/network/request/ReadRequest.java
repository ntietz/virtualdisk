package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

public class ReadRequest
extends BlockRequest
{
    public ReadRequest(int requestId, int volumeId, long logicalOffset)
    {
        super(requestId, volumeId, logicalOffset);
    }

    public MessageType messageType()
    {
        return MessageType.readRequest;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof ReadRequest)
        {
            ReadRequest other = (ReadRequest) obj;

            return super.equals(other);
        }
        else
        {
            return false;
        }
    }
}

