package com.virtualdisk.network.request.base;

public abstract class VolumeRequest
extends Request
{
    protected int volumeId;

    public VolumeRequest(int requestId, int volumeId)
    {
        super(requestId);
        this.volumeId = volumeId;
    }

    /**
     * @return  the volume id for this request
     */
    public int getVolumeId()
    {
        return volumeId;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof VolumeRequest)
        {
            VolumeRequest other = (VolumeRequest) obj;

            return super.equals(other)
                && volumeId == other.getVolumeId();
        }
        else
        {
            return false;
        }
    }
}

