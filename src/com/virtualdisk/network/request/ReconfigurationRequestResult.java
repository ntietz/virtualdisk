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

    @Override
    public MessageType messageType()
    {
        return MessageType.reconfigurationRequestResult;
    }

    @Override
    public int messageSize()
    {
        return 0 + super.messageSize();
    }

    @Override
    public ChannelBuffer encode()
    {
        return super.encode();
    }

    @Override
    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
    }

    @Override
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

    @Override
    public ChannelBuffer encodeWithHeader()
    {
        return addHeader(encode());
    }

    @Override
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

    @Override
    public final int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public boolean canEqual(Object other)
    {
        return (other instanceof ReconfigurationRequestResult);
    }

}

