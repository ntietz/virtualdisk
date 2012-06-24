package com.virtualdisk.coordinator;

import com.virtualdisk.network.util.*;

import org.jboss.netty.channel.*;

import java.util.*;

public class SingletonCoordinator
{
    public static final int CLIENT_NOT_FOUND = -1;

    private static Coordinator coordinator;
    private static CoordinatorServer server;
    private static SingletonCoordinator singleton;

    private static Map<Channel, Integer> clientRegistry;
    private static Map<Integer, Integer> requestCallbacks;

    private SingletonCoordinator()
    {
        // if coordinator is null, initialize it

        // if server is null, initialize it
    }

    public static Coordinator getCoordinator()
    {
        return coordinator;
    }

    public static CoordinatorServer getServer()
    {
        return server;
    }

    public static void setup()
    {
        if (singleton == null)
        {
            // TODO make this use the correct constructor
            singleton = new SingletonCoordinator();
        }
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

    public static void sendToClient(Sendable result, Channel channel)
    {
        // TODO 
    }
}

