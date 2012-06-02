package com.virtualdisk.network.request;

import com.virtualdisk.network.Sendable;

public class DeleteVolumeRequest
extends Request
implements Sendable
{
    private int volumeId;

    public DeleteVolumeRequest(int vid)
    {
        volumeId = vid;
    }
    
    public int volumeId()
    {
        return volumeId;
    }
    
    public byte messageType()
    {
        return Sendable.deleteVolumeRequest;
    }
}
