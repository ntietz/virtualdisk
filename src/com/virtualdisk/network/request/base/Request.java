package com.virtualdisk.network.request.base;

import com.virtualdisk.network.util.*;

import java.util.*;

public abstract class Request
extends Sendable
{
    protected int requestId;

    public Request(int requestId)
    {
        this.requestId = requestId;
    }

    /**
     * @return  the request id for this request
     */
    public int getRequestId()
    {
        return requestId;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof Request)
        {
            Request other = (Request) obj;

            return requestId == other.getRequestId();
        }
        else
        {
            return false;
        }
    }
}

