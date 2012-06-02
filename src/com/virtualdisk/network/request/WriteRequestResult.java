package com.virtualdisk.network.request;

import com.virtualdisk.network.Sendable;

public class WriteRequestResult
extends RequestResult
implements Sendable
{
    public WriteRequestResult(boolean c, boolean s)
    {
        completed = c;
        successful = s;
    }

    public byte messageType()
    {
        return Sendable.writeRequestResult;
    }

}

