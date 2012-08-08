package com.virtualdisk.network.request.base;

import org.jboss.netty.buffer.*;

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

    @Override
    public int messageSize()
    {
        return 4 + super.messageSize();
    }

    @Override
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = super.encode();
        buffer.writeInt(volumeId);

        return buffer;
    }

    @Override
    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
        volumeId = buffer.readInt();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof VolumeRequest)
        {
            VolumeRequest other = (VolumeRequest) obj;

            return other.canEqual(this)
                && super.equals(other)
                && volumeId == other.getVolumeId();
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

        hash = prime*hash + volumeId;

        return hash;
    }

    @Override
    public boolean canEqual(Object other)
    {
        return (other instanceof VolumeRequest);
    }
}

