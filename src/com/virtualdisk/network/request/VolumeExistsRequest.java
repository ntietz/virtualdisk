package com.virtualdisk.network.request;

import com.virtualdisk.network.Sendable;

public class VolumeExistsRequest
extends Request
implements Sendable
{
    private int volumeId;
    
    public VolumeExistsRequest(int vid)
    {
        volumeId = vid;
    }
    
    public byte messageType()
    {
        return Sendable.volumeExistsRequest;
    }

    public int getVolumeId()
    {
        return volumeId;
    }
}
