package com.virtualdisk.network.request.base;

public abstract class BlockRequest
extends Request
{
    protected int volumeId;
    protected long logicalOffset;

    public BlockRequest(int requestId, int volumeId, long logicalOffset)
    {
        super(requestId);
        this.volumeId = volumeId;
        this.logicalOffset = logicalOffset;
    }

    /**
     * @return  the volume id for this request
     */
    public int getVolumeId()
    {
        return volumeId;
    }

    /**
     * @return  the logical offset for this request
     */
    public long getLogicalOffset()
    {
        return logicalOffset;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof BlockRequest)
        {
            BlockRequest other = (BlockRequest) obj;

            return super.equals(other)
                && volumeId == other.getVolumeId()
                && logicalOffset == other.getLogicalOffset();
        }
        else
        {
            return false;
        }
    }
}

