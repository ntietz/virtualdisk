package com.virtualdisk.network.request;

import com.virtualdisk.network.*;

public class VolumeExistsRequestResult
extends RequestResult
implements Sendable
{
    public VolumeExistsRequestResult(boolean c, boolean s)
    {
        completed = c;
        successful = s;
    }
        
    public byte messageType()
    {
        return Sendable.volumeExistsRequestResult;
    }
}
