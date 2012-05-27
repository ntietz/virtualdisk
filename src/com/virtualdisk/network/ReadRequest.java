package com.virtualdisk.network;

public class ReadRequest
implements Request, Sendable
{
    private int volumeId;
    private int logicalOffset;

    public ReadRequest(int v, int l)
    {
        volumeId = v;
        logicalOffset = l;
    }

    public byte messageType()
    {
        return Sendable.readRequest;
    }

    public int getVolumeId()
    {
        return volumeId;
    }

    public int getLogicalOffset()
    {
        return logicalOffset;
    }
}

