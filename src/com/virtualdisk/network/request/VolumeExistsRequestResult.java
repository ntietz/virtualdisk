package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import org.jboss.netty.buffer.*;
import static org.jboss.netty.buffer.ChannelBuffers.*;

public class VolumeExistsRequestResult
extends VolumeRequestResult
{
    private boolean exists;

    public VolumeExistsRequestResult(int requestId, boolean done, boolean success, boolean exists)
    {
        super(requestId, done, success);
        this.exists = exists;
    }

    public boolean volumeExists()
    {
        return exists;
    }

    @Override
    public MessageType messageType()
    {
        return MessageType.volumeExistsRequestResult;
    }

    @Override
    public int messageSize()
    {
        return 1 + super.messageSize();
    }

    @Override
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = super.encode();
        buffer.writeByte(exists ? 1 : 0);

        return buffer;
    }

    @Override
    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
        exists = (buffer.readByte() == 1);
    }

    @Override
    public final boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof VolumeExistsRequestResult)
        {
            VolumeExistsRequestResult other = (VolumeExistsRequestResult) obj;

            return other.canEqual(this)
                && super.equals(other)
                && exists == other.exists;
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

        hash = prime*hash + (exists ? 1 : 0);

        return hash;
    }

    @Override
    public boolean canEqual(Object other)
    {
        return (other instanceof VolumeExistsRequestResult);
    }
}

