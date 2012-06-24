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
            case volumeExistsRequestResult:
            case createVolumeRequestResult:
            case deleteVolumeRequestResult:
                SingletonCoordinator.sendToClient(((RequestResult)result).getRequestId(), result);
                break;

            case writeRequest: {
                WriteRequest request = (WriteRequest) result;
                int volumeId = request.getVolumeId();
                long logicalOffset = request.getLogicalOffset();
                byte[] block = request.getBlock();
                int requestId = coordinator.write(volumeId, logicalOffset, block);

                SingletonCoordinator.registerCallback(requestId, event.getChannel());
                } break;

            case readRequest: {
                ReadRequest request = (ReadRequest) result;
                int volumeId = request.getVolumeId();
                long logicalOffset = request.getLogicalOffset();
                int requestId = coordinator.read(volumeId, logicalOffset);

                SingletonCoordinator.registerCallback(requestId, event.getChannel());
                } break;

            case createVolumeRequest: {
                CreateVolumeRequest request = (CreateVolumeRequest) result;
                int volumeId = request.getVolumeId();
                coordinator.createVolume(volumeId);

                // TODO register a callback to return the results to the user
                //SingletonCoordinator.registerCallback(requestId, event.getChannel());
                } break;

            case deleteVolumeRequest: {
                DeleteVolumeRequest request = (DeleteVolumeRequest) result;
                int volumeId = request.getVolumeId();
                coordinator.deleteVolume(volumeId);

                // TODO register a callback to return the results to the user
                //SingletonCoordinator.registerCallback(requestId, event.getChannel());
                } break;

            default:
                break;
        }
    }
}

