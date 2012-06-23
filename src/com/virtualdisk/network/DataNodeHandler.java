package com.virtualdisk.network;

import com.virtualdisk.network.util.*;
import com.virtualdisk.network.util.Sendable.MessageType;

import org.jboss.netty.channel.*;

public class DataNodeHandler
extends SimpleChannelHandler
{
    public void messageReceived( ChannelHandlerContext context
                               , MessageEvent event
                               )
    {
        Sendable request = (Sendable) event.getMessage();
        MessageType type = request.messageType();

        switch (type)
        {
            case orderRequest:
                break;

            case readRequest:
                break;

            case writeRequest:
                break;

            case volumeExistsRequest:
                break;

            case createVolumeRequest:
                break;

            case deleteVolumeRequest:
                break;

            default:
                break;
        }
    }
}

