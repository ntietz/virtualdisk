package com.virtualdisk.network.codec;

import com.virtualdisk.network.request.*;
import com.virtualdisk.network.util.Sendable.MessageType;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.frame.*;
import org.jboss.netty.buffer.*;

public class RequestDecoder
extends FrameDecoder
{
    protected Object decode( ChannelHandlerContext context
                           , Channel channel
                           , ChannelBuffer buffer
                           )
    throws Exception
    {
        if (buffer.readableBytes() < 5)
        {
            System.out.println("Not enough data to determine type.");
            return null;
        }

        MessageType type = MessageType.fromByte(buffer.readByte());
        int length = buffer.readInt();

        if (buffer.readableBytes() < length)
        {
            System.out.println("Not enough data to read request.");
            buffer.resetReaderIndex();
            return null;
        }

        System.out.println("Decoding.");
        
        switch (type)
        {
            case emptyBuffer:
                System.out.println("Empty buffer.");
                return null;

            case unknownRequest:
                System.out.println("Unkown request");
                return null;

            case orderRequest:
                OrderRequest orderRequest = new OrderRequest(0, 0, 0, null);
                orderRequest.decode(buffer);
                System.out.println("Order request");
                return orderRequest;

            case orderRequestResult:
                OrderRequestResult orderRequestResult = new OrderRequestResult(0, false, false);
                orderRequestResult.decode(buffer);
                System.out.println("Order request result");
                return orderRequestResult;

            case readRequest:
                ReadRequest readRequest = new ReadRequest(0, 0, 0);
                readRequest.decode(buffer);
                System.out.println("Read request");
                return readRequest;

            case readRequestResult:
                ReadRequestResult readRequestResult = new ReadRequestResult(0, false, false, null, null);
                readRequestResult.decode(buffer);
                System.out.println("Read request result");
                return readRequestResult;

            case writeRequest:
                WriteRequest writeRequest = new WriteRequest(0, 0, 0, null, null);
                writeRequest.decode(buffer);
                System.out.println("Write request");
                return writeRequest;

            case writeRequestResult:
                WriteRequestResult writeRequestResult = new WriteRequestResult(0, false, false);
                writeRequestResult.decode(buffer);
                System.out.println("Write request result");
                return writeRequestResult;

            case volumeExistsRequest:
                VolumeExistsRequest volumeExistsRequest = new VolumeExistsRequest(0, 0);
                volumeExistsRequest.decode(buffer);
                System.out.println("VE request");
                return volumeExistsRequest;

            case volumeExistsRequestResult:
                VolumeExistsRequestResult volumeExistsRequestResult = new VolumeExistsRequestResult(0, false, false, false);
                volumeExistsRequestResult.decode(buffer);
                System.out.println("VE request result");
                return volumeExistsRequestResult;

            case createVolumeRequest:
                CreateVolumeRequest createVolumeRequest = new CreateVolumeRequest(0, 0);
                createVolumeRequest.decode(buffer);
                System.out.println("CV request");
                return createVolumeRequest;

            case createVolumeRequestResult:
                CreateVolumeRequestResult createVolumeRequestResult = new CreateVolumeRequestResult(0, false, false);
                createVolumeRequestResult.decode(buffer);
                System.out.println("CV request result : " + (createVolumeRequestResult.isDone() ? "done" : "undone"));
                return createVolumeRequestResult;

            case deleteVolumeRequest:
                DeleteVolumeRequest deleteVolumeRequest = new DeleteVolumeRequest(0, 0);
                deleteVolumeRequest.decode(buffer);
                System.out.println("DV request");
                return deleteVolumeRequest;

            case deleteVolumeRequestResult:
                DeleteVolumeRequestResult deleteVolumeRequestResult = new DeleteVolumeRequestResult(0, false, false);
                deleteVolumeRequestResult.decode(buffer);
                System.out.println("DV request result");
                return deleteVolumeRequestResult;

            default:
                return null;
        }
    }
}

