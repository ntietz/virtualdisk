package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import org.jboss.netty.buffer.*;
import static org.jboss.netty.buffer.ChannelBuffers.*;

public class IdentifyRequest
extends Request
{
    public IdentifyRequest(int requestId)
    {
        super(requestId);
    }

    public MessageType messageType()
    {
        return MessageType.identifyRequest;
    }

    public int messageSize()
    {
        return super.messageSize();
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
        else if (obj instanceof IdentifyRequest)
        {
            IdentifyRequest other = (IdentifyRequest) obj;

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
        return (other instanceof IdentifyRequest);
    }
}

