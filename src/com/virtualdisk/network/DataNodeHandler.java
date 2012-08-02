package com.virtualdisk.network;

import com.virtualdisk.datanode.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.util.*;
import com.virtualdisk.network.util.Sendable.MessageType;

import org.jboss.netty.channel.*;

import java.util.*;

/**
 * Handles received messages on the datanode side.
 * When a request is received, the handler immediately performs this 
 * request on the datanode and sends the result back on the channel
 * the request was received on.
 */
public class DataNodeHandler
extends SimpleChannelHandler
{
    /**
     * The datanode which we perform requests on.
     */
    DataNode dataNode;

    /**
     * The default constructor is private; we cannot use a handler without its datanode.
     */
    private DataNodeHandler() { }

    /**
     * This constructor sets the datanode for requests to be performed on.
     * 
     * @param   dataNode    the node to perform all requests on
     */
    public DataNodeHandler(DataNode dataNode)
    {
        this.dataNode = dataNode;
    }

    /**
     * This method handles requests by performing the appropriate request immediately
     * on the datanode, then forwarding the result back from whence the request came.
     *
     * @param   context     not used
     * @param   event       the message we're handling
     */
    @Override
    public void messageReceived( ChannelHandlerContext context
                               , MessageEvent event
                               )
    {
        Sendable rawRequest = (Sendable) event.getMessage();
        MessageType type = rawRequest.messageType();

        switch (type)
        {
            case orderRequest: {
                OrderRequest request = (OrderRequest) rawRequest;
                int volumeId = request.getVolumeId();
                long logicalOffset = request.getLogicalOffset();
                Date timestamp = request.getTimestamp();

                int requestId = request.getRequestId();
                boolean done = true;
                boolean success = dataNode.order(volumeId, logicalOffset, timestamp);

                Channel coordinatorChannel = event.getChannel();
                OrderRequestResult result = new OrderRequestResult(requestId, done, success);
                coordinatorChannel.write(result);
                } break;

            case readRequest: {
                ReadRequest request = (ReadRequest) rawRequest;
                int volumeId = request.getVolumeId();
                long logicalOffset = request.getLogicalOffset();

                int requestId = request.getRequestId();
                boolean done = true;
                byte[] block = dataNode.read(volumeId, logicalOffset);
                boolean success;
                if (block == null)
                {
                    success = false;
                    //block = new byte[0];
                }
                else
                {
                    success = true;
                }
                Date timestamp = dataNode.getValueTimestamp(volumeId, logicalOffset);
                // TODO FIXME proper null handling here!!!
                if (timestamp == null)
                {
                    timestamp = new Date(0);
                }

                Channel coordinatorChannel = event.getChannel();
                ReadRequestResult result = new ReadRequestResult(requestId, done, success, timestamp, block);
                coordinatorChannel.write(result);
                } break;

            case writeRequest: {
                WriteRequest request = (WriteRequest) rawRequest;
                int volumeId = request.getVolumeId();
                long logicalOffset = request.getLogicalOffset();
                byte[] block = request.getBlock();
                Date timestamp = request.getTimestamp();

                int requestId = request.getRequestId();
                boolean done = true;
                boolean success = dataNode.write(volumeId, logicalOffset, block, timestamp);

                Channel coordinatorChannel = event.getChannel();
                WriteRequestResult result = new WriteRequestResult(requestId, done, success);
                coordinatorChannel.write(result);
                } break;

            case volumeExistsRequest: {
                VolumeExistsRequest request = (VolumeExistsRequest) rawRequest;
                int volumeId = request.getVolumeId();

                int requestId = request.getRequestId();
                boolean done = true;
                boolean success = true;
                boolean exists = dataNode.volumeExists(volumeId);

                Channel coordinatorChannel = event.getChannel();
                VolumeExistsRequestResult result = new VolumeExistsRequestResult(requestId, done, success, exists);
                coordinatorChannel.write(result);
                } break;

            case createVolumeRequest: {
                CreateVolumeRequest request = (CreateVolumeRequest) rawRequest;
                int volumeId = request.getVolumeId();

                int requestId = request.getRequestId();
                boolean done = true;
                dataNode.createVolume(volumeId);
                boolean success = dataNode.volumeExists(volumeId);

                Channel coordinatorChannel = event.getChannel();
                CreateVolumeRequestResult result = new CreateVolumeRequestResult(requestId, done, success);
                coordinatorChannel.write(result);
                } break;

            case deleteVolumeRequest: {
                DeleteVolumeRequest request = (DeleteVolumeRequest) rawRequest;
                int volumeId = request.getVolumeId();

                int requestId = request.getRequestId();
                boolean done = true;
                dataNode.deleteVolume(volumeId);
                boolean success = !dataNode.volumeExists(volumeId);

                Channel coordinatorChannel = event.getChannel();
                DeleteVolumeRequestResult result = new DeleteVolumeRequestResult(requestId, done, success);
                coordinatorChannel.write(result);
                } break;

            case unsetSegmentRequest: {
                UnsetSegmentRequest request = (UnsetSegmentRequest) rawRequest;
                int volumeId = request.getVolumeId();

                int requestId = request.getRequestId();
                boolean done = true;
                boolean success = true;
                for (long logicalOffset = request.getStartingOffset(); logicalOffset <= request.getStoppingOffset(); ++logicalOffset)
                {
                    dataNode.unset(volumeId, logicalOffset);
                }

                Channel coordinatorChannel = event.getChannel();
                UnsetSegmentRequestResult result = new UnsetSegmentRequestResult(requestId, done, success);
                coordinatorChannel.write(result);
                } break;

            case identifyRequest: {
                IdentifyRequest request = (IdentifyRequest) rawRequest;

                int requestId = request.getRequestId();
                
                Channel coordinatorChannel = event.getChannel();
                IdentifyRequestResult result = new IdentifyRequestResult(requestId, IdentifyRequestResult.DATANODE);
                coordinatorChannel.write(result);
                } break;

            default:
                break;
        }
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

