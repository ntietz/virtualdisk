package com.virtualdisk.network.request;

import com.virtualdisk.network.*;

public class VolumeExistsRequestResult
extends RequestResult
implements Sendable
{
    private boolean exists;
    
    public VolumeExistsRequestResult(boolean c, boolean s, boolean e)
    {
        completed = c;
        successful = s;
        exists = e;
    }
    
    public boolean volumeExists()
    {
        return exists;
    }
    
    public byte messageType()
    {
        return Sendable.volumeExistsRequestResult;
    }
}
