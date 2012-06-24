package com.virtualdisk.coordinator;

import com.virtualdisk.network.util.*;

import com.google.common.collect.*;

import org.jboss.netty.channel.*;

import java.util.*;

public class SingletonCoordinator
{
    public static final int CLIENT_NOT_FOUND = -1;

    private static Coordinator coordinator;
    private static CoordinatorServer server;
    private static SingletonCoordinator singleton;

    private static BiMap<Channel, Integer> clientRegistry;
    private static Map<Integer, Integer> requestCallbacks;

    private static int lastClientId = 0;

    private SingletonCoordinator( int blockSize
                                , int segmentSize
                                , int segmentGroupSize
                                , int quorumSize
                                , List<DataNodeIdentifier> nodes
                                , Map<Integer, Channel> channelMap
                                )
    {
        clientRegistry = HashBiMap.create();
        requestCallbacks = new HashMap<Integer, Integer>();

        server = CoordinatorServer.getInstance(nodes, channelMap);
        coordinator = new Coordinator(blockSize, segmentSize, segmentGroupSize, quorumSize, nodes, server);
    }

    public static Coordinator getCoordinator()
    {
        return coordinator;
    }

    public static CoordinatorServer getServer()
    {
        return server;
    }

    public static void setup( int blockSize
                            , int segmentSize
                            , int segmentGroupSize
                            , int quorumSize
                            , List<DataNodeIdentifier> nodes
                            , Map<Integer, Channel> channelMap
                            )
    {
        if (singleton == null)
        {
            singleton = new SingletonCoordinator( blockSize
                                                , segmentSize
                                                , segmentGroupSize
                                                , quorumSize
                                                , nodes
                                                , channelMap
                                                );
        }
    }

    public static void registerNewClient(Channel client)
    {
        ++lastClientId;
        clientRegistry.put(client, lastClientId);
    }

    public static int getClientId(Channel client)
    {
        Integer clientId = clientRegistry.get(client);
        
        if (clientId == null)
        {
            clientId = CLIENT_NOT_FOUND;
        }

        return clientId;
    }

    public static void registerCallback(int requestId, Channel channel)
    {
        requestCallbacks.put(requestId, getClientId(channel));
    }

    public static void sendToClient(int requestId, Sendable result)
    {
        int clientId = requestCallbacks.get(requestId);
        Channel channel = clientRegistry.inverse().get(clientId);
        channel.write(result);
    }
}

