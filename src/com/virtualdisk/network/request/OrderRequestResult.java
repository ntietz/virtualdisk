package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

public class OrderRequestResult
extends BlockRequestResult
{
    public OrderRequestResult(int requestId, boolean done, boolean success)
    {
        super(requestId, done, success);
    }

    public MessageType messageType()
    {
        return MessageType.orderRequestResult;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof OrderRequestResult)
        {
            OrderRequestResult other = (OrderRequestResult) obj;

            return super.equals(other);
        }
        else
        {
            return false;
        }
    }
}

