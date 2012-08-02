package com.virtualdisk.coordinator;

import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.*;
import com.virtualdisk.util.*;

import com.google.common.collect.*;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.*;

import java.util.*;

/**
 * This class wraps coordinators with utilities.
 */
public class SingletonCoordinator
{
    /**
     * This is the code which is returned for an unfound client id.
     */
    public static final int CLIENT_NOT_FOUND = -1;

    /**
     * The coordinator which is stored in this singleton.
     */
    private static Coordinator coordinator;

    /**
     * The server which is stored in this singleton, which the coordinator is configured to use.
     */
    private static NetworkServer server;

    /**
     * An instance of this singleton.
     */
    private static SingletonCoordinator singleton;

    /**
     * A bidirectional map used to connect client ids to their channels.
     */
    private static final BiMap<Channel, Integer> clientRegistry;
    static {
        clientRegistry = HashBiMap.create();
    }

    /**
     * A map which connects request ids to the channel id the result should be returned on.
     */
    private static Map<Integer, IntegerPair> requestCallbacks;

    /**
     * The last client id which was assigned. This is used to allow unique client ids to be given when new channels are registered.
     */
    private static int lastClientId = 0;

    private SingletonCoordinator( int blockSize
                                , int segmentSize
                                , int segmentsPerSegmentGroup
                                , int nodesPerSegmentGroup
                                , int quorumSize
                                , List<DataNodeIdentifier> nodes
                                , Map<Integer, Channel> channelMap
                                , ClientBootstrap bootstrap
                                )
    {
        requestCallbacks = new HashMap();

        server = CoordinatorServer.getInstance(nodes, channelMap, bootstrap); // TODO FIXME this needs to also contain the last-used-nodeid
        coordinator = new Coordinator(blockSize, segmentSize, segmentsPerSegmentGroup, nodesPerSegmentGroup, quorumSize, nodes, server);
    }

    /**
     * Gets the coordinator.
     * @return  the instance of the coordinator
     */
    public static Coordinator getCoordinator()
    {
        return coordinator;
    }

    /**
     * Gets the coordinator's server.
     * @return  the server the coordinator uses
     */
    public static NetworkServer getServer()
    {
        return server;
    }

    /**
     * A method which must be called to set up the coordinator and coordinator server.
     * @param   blockSize           the block size in bytes for the system
     * @param   segmentSize         the segment size in blocks for the system
     * @param   segmentGroupSize    the segment group size in nodes
     * @param   quorumSize          the number of nodes needed for a quorum
     * @param   nodes               the initial nodes in the cluster
     */
    public static void setup( int blockSize
                            , int segmentSize
                            , int segmentsPerSegmentGroup
                            , int nodesPerSegmentGroup
                            , int quorumSize
                            , List<DataNodeIdentifier> nodes
                            , Map<Integer, Channel> channelMap
                            , ClientBootstrap bootstrap
                            )
    {
        if (singleton == null)
        {
            singleton = new SingletonCoordinator( blockSize
                                                , segmentSize
                                                , segmentsPerSegmentGroup
                                                , nodesPerSegmentGroup
                                                , quorumSize
                                                , nodes
                                                , channelMap
                                                , bootstrap
                                                );
        }
    }

    /**
     * Registers a new client. This assigns an id to the channel and puts it in the registry so that callbacks can be resolved.
     * @param   client  the channel we wish to register
     */
    public static void registerNewClient(Channel client)
    {
        ++lastClientId;
        if (clientRegistry == null)
        {
            System.out.println("ruh-roh");
        }
        clientRegistry.put(client, lastClientId);
    }

    /**
     * Gets the client id associated with a given channel.
     * @param   client  the channel we wish to resolve to an id
     * @return  the id associated with the channel, or CLIENT_NOT_FOUND if it is not in the registry
     */
    public static int getClientId(Channel client)
    {
        Integer clientId = clientRegistry.get(client);
        
        if (clientId == null)
        {
            clientId = CLIENT_NOT_FOUND;
        }

        return clientId;
    }

    /**
     * Associates a request id with a channel so that when the result is generated, we can forward it to the correct client.
     * @param   requestId   the id of the request we are registering a callback for
     * @param   channel     the channel we want the result to be forwarded to
     */
    public static void registerCallback(int requestId, int clientRequestId, Channel channel)
    {
        requestCallbacks.put(requestId, new IntegerPair(clientRequestId, getClientId(channel)));
    }

    /**
     * Sends the result back to the client, resolving the callback.
     * @param   requestId   the id of the request we're forwarding the result from
     * @param   result      the result we are forwarding
     */
    public static void sendToClient(int requestId, Sendable result)
    {
        // TODO: remove the callback after we've completed it
        IntegerPair callbackPair = requestCallbacks.get(requestId);
        Integer clientRequestId = callbackPair.first();
        Integer clientId = callbackPair.second();
        ((RequestResult)result).setRequestId(clientRequestId);
        if (clientId != null)
        {
            System.out.println("Writing to client " + clientId + ", request " + ((RequestResult)result).getRequestId());
            Channel channel = clientRegistry.inverse().get(clientId);
            if (channel != null)
            {
                channel.write(result);
            }
        }
        else
        {
            // TODO do we want to log that the client disconnected before getting the result back?
        }
    }

    /**
     * This method sets the result of a request.
     * @param   requestId   the request id we are setting the result of
     * @param   result      the result we are setting
     */
    public synchronized static void setResult(int requestId, RequestResult result)
    {
        List<RequestFuture> futures = server.getResultFutures(requestId);

        for (RequestFuture each : futures)
        {
            if (!each.hasResultSet())
            {
                each.setResult(result);
                break;
            }
        }

        server.setResultFutures(requestId, futures);
    }
}

