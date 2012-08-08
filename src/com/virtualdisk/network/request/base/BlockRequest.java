package com.virtualdisk.network.request.base;

import org.jboss.netty.buffer.*;

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

    @Override
    public int messageSize()
    {
        return 4 + 8 + super.messageSize();
    }

    @Override
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = super.encode();
        buffer.writeInt(volumeId);
        buffer.writeLong(logicalOffset);

        return buffer;
    }

    @Override
    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
        volumeId = buffer.readInt();
        logicalOffset = buffer.readLong();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof BlockRequest)
        {
            BlockRequest other = (BlockRequest) obj;

            return other.canEqual(this)
                && super.equals(other)
                && volumeId == other.getVolumeId()
                && logicalOffset == other.getLogicalOffset();
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        int hash = super.hashCode();

        hash = hash*prime + volumeId;
        hash = hash*prime + (int) (logicalOffset ^ (logicalOffset >>> 32));

        return hash;
    }

    @Override
    public boolean canEqual(Object other)
    {
        return (other instanceof BlockRequest);
    }
}

