package com.virtualdisk.network;

import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.*;
import com.virtualdisk.network.util.Sendable.MessageType;

import org.jboss.netty.channel.*;

/**
 * The ClientHandler takes care of receiving messages from the coordinator.
 * Whenever a message is received, it logs any relevant information and stores or prints the message.
 */
public class ClientHandler
extends SimpleChannelHandler
{
    /**
     * This method handles received messages.
     * 
     * @param   context     the context arround teh channel; it is not used for this handler
     * @param   event       the contents of the received message
     */
    @Override
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

        switch (type)
        {
            case orderRequestResult: {
                } break;

            case readRequestResult: {
                ReadRequestResult result = (ReadRequestResult) rawResult;
                System.out.print(result.getTimestamp()); System.out.print(" ");
                if (result.getBlock() != null)
                    System.out.print(result.getBlock().length);
                else
                    System.out.print("(null)");
                } break;

            case writeRequestResult: {
                } break;

            case volumeExistsRequestResult: {
                VolumeExistsRequestResult result = (VolumeExistsRequestResult) rawResult;
                System.out.print(result.volumeExists() ? "exists" : "missing"); System.out.print(" ");
                } break;

            case createVolumeRequestResult: {
                } break;

            case deleteVolumeRequestResult: {
                } break;

            default: {
                System.out.print("error.");
                } break;
        }

        System.out.println("");
    }

    /**
     * When an exception is caught, its stack trace is printed and the program halts execution.
     *
     * @param   context     not used here
     * @param   event       the caught exception
     */
    @Override
    public void exceptionCaught( ChannelHandlerContext context
                               , ExceptionEvent event
                               )
    {
        event.getCause().printStackTrace();
        event.getChannel().close();
        System.exit(1);
    }
}

