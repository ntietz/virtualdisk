package com.virtualdisk.network;

import com.virtualdisk.client.*;
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
    Client client;

    public ClientHandler(Client client)
    {
        this.client = client;
    }

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

        switch (type)
        {
            case identifyRequest: {
                IdentifyRequest request = (IdentifyRequest) rawResult;

                int requestId = request.getRequestId();
                
                Channel coordinatorChannel = event.getChannel();
                IdentifyRequestResult result = new IdentifyRequestResult(requestId, IdentifyRequestResult.CLIENT);
                coordinatorChannel.write(result);
                } break;

            case orderRequestResult: {
                } break;

            case readRequestResult: {
                ReadRequestResult result = (ReadRequestResult) rawResult;
                client.setReadResult(result.getRequestId(), result.getBlock());
                } break;

            case writeRequestResult: {
                } break;

            case volumeExistsRequestResult: {
                } break;

            case createVolumeRequestResult: {
                } break;

            case deleteVolumeRequestResult: {
                } break;

            default: {
                } break;
        }

        if (rawResult instanceof RequestResult)
        {
            RequestResult result = (RequestResult) rawResult;
            int requestId = result.getRequestId();
            
            client.setSuccess(requestId, result.wasSuccessful());
            client.setFinished(requestId, true);
        }
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

