package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import org.jboss.netty.buffer.*;
import static org.jboss.netty.buffer.ChannelBuffers.*;

public class UnsetSegmentRequest
extends VolumeRequest
{
    protected long startingOffset;
    protected long stoppingOffset;

    public UnsetSegmentRequest(int requestId, int volumeId, long startingOffset, long stoppingOffset)
    {
        super(requestId, volumeId);
        this.startingOffset = startingOffset;
        this.stoppingOffset = stoppingOffset;
    }

    public long getStartingOffset()
    {
        return startingOffset;
    }

    public long getStoppingOffset()
    {
        return stoppingOffset;
    }

    @Override
    public MessageType messageType()
    {
        return MessageType.unsetSegmentRequest;
    }

    @Override
    public int messageSize()
    {
        return 16 + super.messageSize();
    }

    @Override
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = super.encode();
        buffer.writeLong(startingOffset);
        buffer.writeLong(stoppingOffset);

        return buffer;
    }

    @Override
    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
        startingOffset = buffer.readLong();
        stoppingOffset = buffer.readLong();
    }

    @Override
    public final boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof UnsetSegmentRequest)
        {
            UnsetSegmentRequest other = (UnsetSegmentRequest) obj;

            return other.canEqual(this)
                && super.equals(other)
                && other.startingOffset == startingOffset
                && other.stoppingOffset == stoppingOffset;
        }
        else
        {
            return false;
        }
    }

    @Override
    public final int hashCode()
    {
        int hash = super.hashCode();

        hash = prime*hash + ((int) (startingOffset ^ (startingOffset >>> 32)));
        hash = prime*hash + ((int) (stoppingOffset ^ (stoppingOffset >>> 32)));

        return hash;
    }

    @Override
    public boolean canEqual(Object other)
    {
        return (other instanceof UnsetSegmentRequest);
    }
}
