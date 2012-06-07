package com.virtualdisk.network.request;

import com.virtualdisk.network.Sendable;

public abstract class RequestResult
extends Sendable
{
    protected boolean completed = false;    // describes whether the request itself had errors
    protected boolean successful = false;      // describes whether the order was successful

    public boolean completed()
    {
        return completed;
    }
    
    public boolean successful()
    {
        return successful;
    }
}

