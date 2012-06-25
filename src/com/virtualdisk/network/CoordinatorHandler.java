package com.virtualdisk.network;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.*;
import com.virtualdisk.network.util.Sendable.MessageType;

import org.jboss.netty.channel.*;

import java.util.*;

public class CoordinatorHandler
extends SimpleChannelHandler
{
    Coordinator coordinator = SingletonCoordinator.getCoordinator();

    public void messageReceived( ChannelHandlerContext context
                               , MessageEvent event
                               )
    {
        Sendable result = (Sendable) event.getMessage();
        MessageType type = result.messageType();

        switch (type)
        {
            case orderRequestResult:
            case readRequestResult:
            case writeRequestResult:
                System.out.println("sending result to client");
                SingletonCoordinator.sendToClient(((RequestResult)result).getRequestId(), result);
                break;

            case volumeExistsRequestResult:
            case createVolumeRequestResult:
            case deleteVolumeRequestResult:
                System.out.println("eating volume results");
                break;

            case writeRequest: {
                WriteRequest request = (WriteRequest) result;
                int volumeId = request.getVolumeId();
                long logicalOffset = request.getLogicalOffset();
                byte[] block = request.getBlock();
                int requestId = coordinator.write(volumeId, logicalOffset, block);
                System.out.println("handling write request");

                SingletonCoordinator.registerCallback(requestId, event.getChannel());
                } break;

            case readRequest: {
                ReadRequest request = (ReadRequest) result;
                int volumeId = request.getVolumeId();
                long logicalOffset = request.getLogicalOffset();
                int requestId = coordinator.read(volumeId, logicalOffset);
                System.out.println("handling read request");

                SingletonCoordinator.registerCallback(requestId, event.getChannel());
                } break;

            case createVolumeRequest: {
                CreateVolumeRequest request = (CreateVolumeRequest) result;
                int volumeId = request.getVolumeId();
                coordinator.createVolume(volumeId);
                System.out.println("handling volume request");

                // TODO register a callback to return the results to the user
                //SingletonCoordinator.registerCallback(requestId, event.getChannel());
                } break;

            case deleteVolumeRequest: {
                DeleteVolumeRequest request = (DeleteVolumeRequest) result;
                int volumeId = request.getVolumeId();
                coordinator.deleteVolume(volumeId);
                System.out.println("handling volume request");

                // TODO register a callback to return the results to the user
                //SingletonCoordinator.registerCallback(requestId, event.getChannel());
                } break;

            default:
                System.out.println("unknown request");
                break;
        }
    }

    public void exceptionCaught( ChannelHandlerContext context
                               , ExceptionEvent event
                               )
    {
        event.getCause().printStackTrace();
        event.getChannel().close();
        System.exit(1);
    }
}

