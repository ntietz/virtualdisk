package com.virtualdisk.network.request.base;

import com.virtualdisk.network.util.*;

public abstract class RequestResult
extends Sendable
{
    protected int requestId;
    protected boolean done;
    protected boolean success;

    public RequestResult(int requestId, boolean done, boolean success)
    {
        this.requestId = requestId;
        this.done = done;
        this.success = success;
    }

    public int getRequestId()
    {
        return requestId;
    }

    /**
     * @return  true if the request completed or timed out, false otherwise
     */
    public boolean isDone()
    {
        return done;
    }

    /**
     * @return  true if the request succeeded, false if it failed for any reason
     */
    public boolean wasSuccessful()
    {
        return success;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof RequestResult)
        {
            RequestResult other = (RequestResult) obj;

            return requestId == other.getRequestId()
                && done == other.isDone()
                && success == other.wasSuccessful();
        }
        else
        {
            return false;
        }
    }
}

