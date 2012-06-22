package com.virtualdisk.network.codec;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.util.*;
import com.virtualdisk.network.util.Sendable.MessageType;

import org.jboss.netty.channel.*;

import java.util.*;

public class ServerHandler
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
                // TODO update to return result to user of the API
                break;

            case readRequestResult:
                // TODO update to return result to user of the API
                break;

            case writeRequestResult:
                // TODO update to return result to user of the API
                break;

            case volumeExistsRequestResult:
                // TODO update to return result to user of the API
                break;

            case createVolumeRequestResult:
                // TODO update to return result to user of the API
                break;

            case deleteVolumeRequestResult:
                // TODO update to return result to user of the API
                break;

            case writeRequest: {
                WriteRequest request = (WriteRequest) result;
                int volumeId = request.getVolumeId();
                long logicalOffset = request.getLogicalOffset();
                byte[] block = request.getBlock();
                int requestId = coordinator.write(volumeId, logicalOffset, block);

                // TODO register a callback to return the results to the user
                } break;

            case readRequest: {
                ReadRequest request = (ReadRequest) result;
                int volumeId = request.getVolumeId();
                long logicalOffset = request.getLogicalOffset();
                int requestId = coordinator.read(volumeId, logicalOffset);

                // TODO register a callback to return the results to the user
                } break;

            case createVolumeRequest: {
                CreateVolumeRequest request = (CreateVolumeRequest) result;
                int volumeId = request.getVolumeId();
                coordinator.createVolume(volumeId);

                // TODO register a callback to return the results to the user
                } break;

            case deleteVolumeRequest: {
                DeleteVolumeRequest request = (DeleteVolumeRequest) result;
                int volumeId = request.getVolumeId();
                coordinator.deleteVolume(volumeId);

                // TODO register a callback to return the results to the user
                } break;

            default:
                break;
        }
    }
}

