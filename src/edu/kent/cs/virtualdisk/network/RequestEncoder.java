package edu.kent.cs.virtualdisk.network;

import org.jboss.netty.channel.*;
import org.jboss.netty.buffer.*;
import org.jboss.netty.handler.codec.oneone.*;

import java.util.*;

public class RequestEncoder
extends OneToOneEncoder
{
    protected Object encode( ChannelHandlerContext context
                           , Channel channel
                           , Object messageObject
                           )
    throws Exception
    {
        if (!(messageObject instanceof Sendable))
        {
            return messageObject;
        }
        
        Sendable sendableObject = (Sendable) messageObject;

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(20); //an OK guess... will resize.

        byte messageType = buffer.readByte();

        buffer.writeByte(messageType);

        switch (messageType)
        {
            case Sendable.orderRequestResult :
                buffer = encodeOrderRequestResult(buffer, (OrderRequestResult) sendableObject);
                break;

            case Sendable.readRequestResult :
                buffer = encodeReadRequestResult(buffer, (ReadRequestResult) sendableObject);
                break;

            case Sendable.writeRequestResult :
                buffer = encodeWriteRequestResult(buffer, (WriteRequestResult) sendableObject);
                break;

            case Sendable.orderRequest :
                buffer = encodeOrderRequest(buffer, (OrderRequest) sendableObject);
                break;

            case Sendable.readRequest :
                buffer = encodeReadRequest(buffer, (ReadRequest) sendableObject);
                break;

            case Sendable.writeRequest :
                buffer = encodeWriteRequest(buffer, (WriteRequest) sendableObject);
                break;
        }

        return buffer;
    }

    protected ChannelBuffer encodeOrderRequestResult(ChannelBuffer buffer, OrderRequestResult result)
    {
        buffer.writeByte((result.completed()) ? 1 : 0);
        buffer.writeByte((result.successful()) ? 1 : 0);

        return buffer;
    }

    protected ChannelBuffer encodeReadRequestResult(ChannelBuffer buffer, ReadRequestResult result)
    {
        buffer.writeInt(result.getResult().length);
        buffer.writeByte((result.completed()) ? 1 : 0);
        buffer.writeLong(result.getTimestamp().getTime());
        buffer.writeBytes(result.getResult());

        return buffer;
    }

    protected ChannelBuffer encodeWriteRequestResult(ChannelBuffer buffer, WriteRequestResult result)
    {
        buffer.writeByte((result.completed()) ? 1 : 0);
        buffer.writeByte((result.successful()) ? 1 : 0);

        return buffer;
    }

    protected ChannelBuffer encodeOrderRequest(ChannelBuffer buffer, OrderRequest request)
    {
        buffer.writeInt(request.getVolumeId());
        buffer.writeInt(request.getLogicalOffset());
        buffer.writeLong(request.getTimestamp().getTime());

        return buffer;
    }

    protected ChannelBuffer encodeReadRequest(ChannelBuffer buffer, ReadRequest request)
    {
        buffer.writeInt(request.getVolumeId());
        buffer.writeInt(request.getLogicalOffset());

        return buffer;
    }

    protected ChannelBuffer encodeWriteRequest(ChannelBuffer buffer, WriteRequest request)
    {
        buffer.writeInt(request.getBlock().length);
        buffer.writeInt(request.getVolumeId());
        buffer.writeInt(request.getLogicalOffset());
        buffer.writeLong(request.getTimestamp().getTime());
        buffer.writeBytes(request.getBlock());

        return buffer;
    }

}

