package com.virtualdisk.network;

import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.*;
import com.virtualdisk.network.util.Sendable.MessageType;

import org.jboss.netty.channel.*;

public class ClientHandler
extends SimpleChannelHandler
{
    public void messageReceived( ChannelHandlerContext context
                               , MessageEvent event
                               )
    {
        Sendable rawResult = (Sendable) event.getMessage();

        MessageType type = rawResult.messageType();

        System.out.print("Received: ");
        if (rawResult instanceof RequestResult)
        {
            RequestResult result = (RequestResult) rawResult;
            System.out.print(result.isDone() ? "done" : "not-done"); System.out.print(" ");
            System.out.print(result.wasSuccessful() ? "success" : "failed"); System.out.print(" ");
        }

        switch(type)
        {
            case orderRequestResult: {
                }break;

            case readRequestResult: {
                ReadRequestResult result = (ReadRequestResult) rawResult;
                System.out.print(result.getTimestamp()); System.out.print(" ");
                System.out.print(new String(result.getBlock()));
                }break;

            case writeRequestResult: {
                }break;

            case volumeExistsRequestResult: {
                VolumeExistsRequestResult result = (VolumeExistsRequestResult) rawResult;
                System.out.print(result.volumeExists() ? "exists" : "missing"); System.out.print(" ");
                }break;

            case createVolumeRequestResult: {
                }break;

            case deleteVolumeRequestResult: {
                }break;

            default: {
                System.out.print("error.");
                } break;
        }

        System.out.println("");
    }
}

