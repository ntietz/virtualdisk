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
                // TODO
                // perform the order request
                // return the result on the same channel it came in on
                break;

            case readRequest:
                // TODO
                // perform the read request
                // return the result
                break;

            case writeRequest:
                // TODO
                // perform the write request
                // return the result
                break;

            case volumeExistsRequest:
                // TODO
                break;

            case createVolumeRequest:
                // TODO
                break;

            case deleteVolumeRequest:
                // TODO
                break;

            default:
                break;
        }
    }
}

