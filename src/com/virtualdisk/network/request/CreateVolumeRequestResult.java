package com.virtualdisk.network.request;

import com.virtualdisk.network.Sendable;

public class CreateVolumeRequestResult
extends RequestResult
implements Sendable
{
    public CreateVolumeRequestResult(boolean c, boolean s)
    {
        completed = c;
        successful = s;
    }
    
    public byte messageType()
    {
        return Sendable.createVolumeRequestResult;
    }
}
