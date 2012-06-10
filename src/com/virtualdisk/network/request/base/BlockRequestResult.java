package com.virtualdisk.network.request.base;

public abstract class BlockRequestResult
extends RequestResult
{
    public BlockRequestResult(int requestId, boolean done, boolean success)
    {
        super(requestId, done, success);
    }
}

