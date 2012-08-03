package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import org.jboss.netty.buffer.*;
import static org.jboss.netty.buffer.ChannelBuffers.*;

public class ReconfigurationRequestResult
extends RequestResult
{
    public ReconfigurationRequestResult(int requestId, boolean done, boolean success)
    {
        super(requestId, done, success);
    }

    public MessageType messageType()
    {
        return MessageType.reconfigurationRequestResult;
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

    public ChannelBuffer encodeWithHeader()
    {
        return addHeader(encode());
    }

    public final boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof ReconfigurationRequestResult)
        {
            ReconfigurationRequestResult other = (ReconfigurationRequestResult) obj;

            return other.canEqual(this)
                && super.equals(other);
        }
        else
        {
            return false;
        }
    }

    public final int hashCode()
    {
        return super.hashCode();
    }

    public boolean canEqual(Object other)
    {
        return (other instanceof ReconfigurationRequestResult);
    }

}

