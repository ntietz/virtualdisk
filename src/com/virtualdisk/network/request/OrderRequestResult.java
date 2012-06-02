package com.virtualdisk.network.request;

import com.virtualdisk.network.Sendable;

public class OrderRequestResult
extends RequestResult
implements Sendable
{

    public OrderRequestResult(boolean c, boolean s)
    {
        completed = c;
        successful = s;
    }

    public byte messageType()
    {
        return Sendable.orderRequestResult;
    }

}

