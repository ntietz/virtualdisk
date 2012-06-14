package com.virtualdisk.network.codec;

import com.virtualdisk.network.util.*;
import com.virtualdisk.network.util.Sendable.MessageType;

import org.jboss.netty.channel.*;

public class ServerHandler
extends SimpleChannelHandler
{
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

            default:
                break;
        }
    }
}

