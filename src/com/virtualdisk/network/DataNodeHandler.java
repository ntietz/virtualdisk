package com.virtualdisk.network;

import com.virtualdisk.datanode.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.util.*;
import com.virtualdisk.network.util.Sendable.MessageType;

import org.jboss.netty.channel.*;

import java.util.*;

public class DataNodeHandler
extends SimpleChannelHandler
{
    DataNode dataNode;

    private DataNodeHandler() { }
    public DataNodeHandler(DataNode dataNode)
    {
        this.dataNode = dataNode;
    }

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
                    block = new byte[0];
                }
                else
                {
                    success = true;
                }
                Date timestamp = dataNode.getValueTimestamp(volumeId, logicalOffset);

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
                boolean success = dataNode.volumeExists(volumeId);

                Channel coordinatorChannel = event.getChannel();
                DeleteVolumeRequestResult result = new DeleteVolumeRequestResult(requestId, done, success);
                coordinatorChannel.write(result);
                } break;

            default:
                break;
        }
    }

    public void exceptionCaught( ChannelHandlerContext context
                               , ExceptionEvent event
                               )
    {
        event.getCause().printStackTrace();
        event.getChannel().close();
        System.exit(1);
    }
}

