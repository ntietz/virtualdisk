package com.virtualdisk.network.request.base;

public abstract class VolumeRequestResult
extends RequestResult
{
    public VolumeRequestResult(int requestId, boolean done, boolean success)
    {
        super(requestId, done, success);
    }
}

