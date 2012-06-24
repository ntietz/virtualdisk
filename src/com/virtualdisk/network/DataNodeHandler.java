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
    DataNode dataNode = SingletonDataNode.getDataNode();

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

