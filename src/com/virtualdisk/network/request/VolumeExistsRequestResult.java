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

    public MessageType messageType()
    {
        return MessageType.volumeExistsRequestResult;
    }

    public int messageSize()
    {
        return 1 + super.messageSize();
    }

    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = super.encode();
        buffer.writeByte(exists ? 1 : 0);

        return buffer;
    }

    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
        exists = (buffer.readByte() == 1);
    }

    public ChannelBuffer addHeader(ChannelBuffer buffer)
    {
        byte type = messageType().byteValue();
        int length = messageSize();

        ChannelBuffer header = buffer(5);
        header.writeByte(type);
        header.writeInt(length);

        ChannelBuffer message = copiedBuffer(header, buffer);
        return message;
    }

    public ChannelBuffer encodeWithHeader()
    {
        return addHeader(encode());
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof VolumeExistsRequestResult)
        {
            VolumeExistsRequestResult other = (VolumeExistsRequestResult) obj;

            return super.equals(other)
                && exists == other.exists;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        int hash = super.hashCode();

        hash = prime*hash + (exists ? 1 : 0);

        return hash;
    }
}

