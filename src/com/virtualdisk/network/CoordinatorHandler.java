package com.virtualdisk.network;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.*;
import com.virtualdisk.network.util.Sendable.MessageType;

import org.jboss.netty.channel.*;

import java.util.*;

/**
 * Handles received messages on the coordinator side.
 * Any request result is sent directly back to the requesting client.
 * Any request has a request object built which is then forwarded to the appropriate datanodes.
 */
public class CoordinatorHandler
extends SimpleChannelHandler
{
    /**
     * The coordinator for the requests.
     */
    Coordinator coordinator = SingletonCoordinator.getCoordinator();
    
    /**
     * The server used to send messages.
     */
    NetworkServer server = SingletonCoordinator.getServer();

    /**
     * Handles requests: upon receipt of a request result, it gets sent
     * back to the requester; upon receipt of a request, it gets sent
     * to the appropriate datanodes.
     *
     * @param   context     not used
     * @param   event       the message received
     */
    @Override
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
            case unsetSegmentRequestResult:
                SingletonCoordinator.setResult(((RequestResult)result).getRequestId(), (RequestResult)result);
                break;

            case writeRequest: {
                WriteRequest request = (WriteRequest) result;
                int volumeId = request.getVolumeId();
                long logicalOffset = request.getLogicalOffset();
                byte[] block = request.getBlock();
                int requestId = coordinator.write(volumeId, logicalOffset, block);

                SingletonCoordinator.registerCallback(requestId, request.getRequestId(), event.getChannel());
                System.out.println("Registered callback for write request " + request.getRequestId()
                                 + " internal request id " + requestId);
                } break;

            case readRequest: {
                ReadRequest request = (ReadRequest) result;
                int volumeId = request.getVolumeId();
                long logicalOffset = request.getLogicalOffset();
                int requestId = coordinator.read(volumeId, logicalOffset);

                SingletonCoordinator.registerCallback(requestId, request.getRequestId(), event.getChannel());
                System.out.println("Registered callback for read request " + request.getRequestId());
                } break;

            case createVolumeRequest: {
                CreateVolumeRequest request = (CreateVolumeRequest) result;
                int volumeId = request.getVolumeId();
                int requestId = coordinator.createVolume(volumeId);

                SingletonCoordinator.registerCallback(requestId, request.getRequestId(), event.getChannel());
                System.out.println("Registered callback for create request " + request.getRequestId());
                } break;

            case deleteVolumeRequest: {
                DeleteVolumeRequest request = (DeleteVolumeRequest) result;
                int volumeId = request.getVolumeId();
                int requestId = coordinator.deleteVolume(volumeId);

                SingletonCoordinator.registerCallback(requestId, request.getRequestId(), event.getChannel());
                } break;

            case identifyRequest: {
                IdentifyRequest request = (IdentifyRequest) result;

                int requestId = request.getRequestId();
                
                Channel channel = event.getChannel();
                IdentifyRequestResult identity = new IdentifyRequestResult(requestId, IdentifyRequestResult.COORDINATOR);
                channel.write(identity);
                } break;

            case identifyRequestResult: {
                IdentifyRequestResult identifyResult = (IdentifyRequestResult) result;

                if (identifyResult.getType() == IdentifyRequestResult.CLIENT)
                {
                    Channel clientChannel = event.getChannel();
                    SingletonCoordinator.registerNewClient(clientChannel);
                }

                } break;


            default:
                break;
        }
    }

    /**
     * Registers the connecting client channel so that callbacks can be performed.
     *
     * @param   context     not used
     * @param   event       the channel connection event, where we get the channel from
     */
    @Override
    public void channelConnected( ChannelHandlerContext context
                                , ChannelStateEvent event
                                )
    {
        Channel clientChannel = event.getChannel();
        IdentifyRequest request = new IdentifyRequest(0);
        clientChannel.write(request);
    }

    /**
     * Handles caught exceptions by printing the stack trace and halting the program.
     *
     * @param   context     not used
     * @param   event       the exception we're handling
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

