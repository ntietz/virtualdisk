package com.virtualdisk.network.request;

import com.virtualdisk.network.Sendable;

public class DeleteVolumeRequestResult
extends RequestResult
implements Sendable
{
    public DeleteVolumeRequestResult(boolean c, boolean s)
    {
        completed = c;
        successful = s;
    }

    public byte messageType()
    {
        return Sendable.deleteVolumeRequestResult;
    }
}
