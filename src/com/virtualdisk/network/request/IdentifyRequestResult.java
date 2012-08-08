package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import org.jboss.netty.buffer.*;
import static org.jboss.netty.buffer.ChannelBuffers.*;

public class IdentifyRequestResult
extends RequestResult
{
    public final static byte CLIENT = 0;
    public final static byte COORDINATOR = 1;
    public final static byte DATANODE = 2;

    private byte type;

    public IdentifyRequestResult(int requestId, byte type)
    {
        super(requestId, true, true);
        this.type = type;
    }

    public byte getType()
    {
        return type;
    }

    @Override
    public MessageType messageType()
    {
        return MessageType.identifyRequestResult;
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
        buffer.writeByte(type);
        return buffer;
    }

    @Override
    public void decode(ChannelBuffer buffer)
    {
        super.decode(buffer);
        type = buffer.readByte();
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
        else if (obj instanceof IdentifyRequestResult)
        {
            IdentifyRequestResult other = (IdentifyRequestResult) obj;

            return other.canEqual(this)
                && super.equals(other)
                && type == other.type;
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
        hash = prime*hash + (int) type;
        return hash;
    }

    @Override
    public boolean canEqual(Object other)
    {
        return (other instanceof IdentifyRequestResult);
    }
}

