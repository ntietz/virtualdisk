package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

public class WriteRequestResult
extends BlockRequestResult
{
    public WriteRequestResult(int requestId, boolean done, boolean success)
    {
        super(requestId, done, success);
    }

    public MessageType messageType()
    {
        return MessageType.writeRequestResult;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof WriteRequestResult)
        {
            WriteRequestResult other = (WriteRequestResult) obj;

            return super.equals(other);
        }
        else
        {
            return false;
        }
    }

}

