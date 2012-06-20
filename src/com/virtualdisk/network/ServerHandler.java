package com.virtualdisk.network.codec;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.util.*;
import com.virtualdisk.network.util.Sendable.MessageType;

import org.jboss.netty.channel.*;

public class ServerHandler
extends SimpleChannelHandler
{
    //Coordinator coordinator = CoordinatorServer.getCoordinator();
    Coordinator coordinator = null;

    public void messageReceived( ChannelHandlerContext context
                               , MessageEvent event
                               )
    {
        Sendable result = (Sendable) event.getMessage();
        MessageType type = result.messageType();

        switch (type)
        {
            case orderRequestResult:
                break;

            case readRequestResult:
                break;

            case writeRequestResult:
                break;

            case volumeExistsRequestResult:
                break;

            case createVolumeRequestResult:
                break;

            case deleteVolumeRequestResult:
                break;

            case writeRequest:
                // user-program writing
                break;

            case readRequest:
                // user-program reading
                break;

            case createVolumeRequest:
                // user-program creating volume
                break;

            case deleteVolumeRequest:
                // user-program deleting volume
                break;

            case volumeExistsRequest:
                // user-program checking volume
                break;

            default:
                break;
        }
    }
}

