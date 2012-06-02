package com.virtualdisk.network;

import com.virtualdisk.network.request.OrderRequest;
import com.virtualdisk.network.request.OrderRequestResult;
import com.virtualdisk.network.request.ReadRequest;
import com.virtualdisk.network.request.ReadRequestResult;
import com.virtualdisk.network.request.WriteRequest;
import com.virtualdisk.network.request.WriteRequestResult;

import org.jboss.netty.channel.*;
import org.jboss.netty.buffer.*;
import org.jboss.netty.handler.codec.frame.*;

import java.util.*;

public class RequestDecoder
extends FrameDecoder
{
    protected Object decode( ChannelHandlerContext context
                           , Channel channel
                           , ChannelBuffer buffer
                           )
    throws Exception
    {
        if (buffer.readableBytes() < 1)
        {
            return null;
        }

        byte messageType = buffer.readByte();

        switch (messageType)
        {
            case Sendable.orderRequestResult :
                return decodeOrderRequestResult(buffer);

            case Sendable.readRequestResult :
                return decodeReadRequestResult(buffer);

            case Sendable.writeRequestResult :
                return decodeWriteRequestResult(buffer);

            case Sendable.orderRequest :
                return decodeOrderRequest(buffer);

            case Sendable.readRequest :
                return decodeReadRequest(buffer);

            case Sendable.writeRequest :
                return decodeWriteRequest(buffer);
        }

        buffer.resetReaderIndex();
        return null;
    }

    protected Object decodeOrderRequestResult(ChannelBuffer buffer)
    {
        if (buffer.readableBytes () < 2)
        {
            buffer.resetReaderIndex();
            return null;
        }
        
        boolean completed = (buffer.readByte() != 0);
        boolean success = (buffer.readByte() != 0);

        OrderRequestResult result =
            new OrderRequestResult(completed, success);

        return result;
    }
    
    protected Object decodeReadRequestResult(ChannelBuffer buffer)
    {
        if (buffer.readableBytes() < 4)
        {
            buffer.resetReaderIndex();
            return null;
        }
        int blockSize = buffer.readInt();

        if (buffer.readableBytes() < (1 + 8 + blockSize))
        {
            buffer.resetReaderIndex();
            return null;
        }

        boolean completed = (buffer.readByte() != 0); // 0 for false

        Date timestamp = new Date(buffer.readLong());

        byte[] block = new byte[blockSize];
        buffer.readBytes(block);

        ReadRequestResult result =
            new ReadRequestResult(completed, true, block, timestamp);

        return result;
    }

    protected Object decodeWriteRequestResult(ChannelBuffer buffer)
    {
        if (buffer.readableBytes() < 2)
        {
            buffer.resetReaderIndex();
            return null;
        }
        
        boolean completed = (buffer.readByte() != 0);
        boolean success = (buffer.readByte() != 0);

        WriteRequestResult result =
            new WriteRequestResult(completed, success);

        return result;
    }

    protected Object decodeOrderRequest(ChannelBuffer buffer)
    {
        if (buffer.readableBytes() < 16)
        {
            buffer.resetReaderIndex();
            return null;
        }

        int volumeId = buffer.readInt();
        int logicalOffset = buffer.readInt();
        Date timestamp = new Date(buffer.readLong());

        OrderRequest request = new OrderRequest(volumeId, logicalOffset, timestamp);

        return request;
    }

    protected Object decodeReadRequest(ChannelBuffer buffer)
    {
        if (buffer.readableBytes() < 8)
        {
            buffer.resetReaderIndex();
            return null;
        }

        int volumeId = buffer.readInt();
        int logicalOffset = buffer.readInt();

        ReadRequest request = new ReadRequest(volumeId, logicalOffset);

        return request;
    }

    protected Object decodeWriteRequest(ChannelBuffer buffer)
    {
        if (buffer.readableBytes() < 4)
        {
            buffer.resetReaderIndex();
            return null;
        }

        int blockSize = buffer.readInt();

        if (buffer.readableBytes() < 16 + blockSize)
        {
            buffer.resetReaderIndex();
            return null;
        }

        int volumeId = buffer.readInt();
        int logicalOffset = buffer.readInt();
        Date timestamp = new Date(buffer.readLong());

        byte[] block = new byte[blockSize];
        buffer.readBytes(block);

        WriteRequest request = new WriteRequest(volumeId, logicalOffset, block, timestamp);

        return request;
    }
}

