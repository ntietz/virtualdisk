package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import org.jboss.netty.buffer.*;
import static org.jboss.netty.buffer.ChannelBuffers.*;

public class DeleteVolumeRequestResult
extends VolumeRequestResult
{
    public DeleteVolumeRequestResult(int requestId, boolean done, boolean success)
    {
        super(requestId, done, success);
    }

    public MessageType messageType()
    {
        return MessageType.deleteVolumeRequestResult;
    }

    public int messageSize()
    {
        return 0 + super.messageSize();
    }

    public ChannelBuffer encode()
    {
        return super.encode();
    }

    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
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

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof DeleteVolumeRequestResult)
        {
            DeleteVolumeRequestResult other = (DeleteVolumeRequestResult) obj;

            return super.equals(other);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode();
    }
}

