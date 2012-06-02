package com.virtualdisk.network.request;

import com.virtualdisk.network.Sendable;

public class CreateVolumeRequest
extends Request
implements Sendable
{
    private int volumeId;
 
    public CreateVolumeRequest(int vid)
    {
        volumeId = vid;
    }

    public int volumeId()
    {
        return volumeId;
    }

    public byte messageType()
    {
        return Sendable.createVolumeRequest;
    }
}
