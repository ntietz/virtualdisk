package edu.kent.cs.virtualdisk.coordinator;

import edu.kent.cs.virtualdisk.network.*;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.group.*;
import org.jboss.netty.channel.socket.nio.*;

import java.util.*;
import java.util.concurrent.*;

public class CoordinatorServer
{
    static private final ChannelGroup allChannels = new DefaultChannelGroup("CoordinatorServer");
    static private Coordinator coordinator;

    public static void main(String... args)
    {
        /*
         *  USAGE:  coordinator.sh
         *          coordinator.sh conf.xml
         *  configuration is read in from a configuration file.
         *  this configuration file is conf.xml by default.
         */
        // TODO determine usage and implement it

        int port = Integer.valueOf(args[0]);
        
        List<DataNodeIdentifier> nodes; // TODO initialize

        ServerBootstrap serverBootstrap = new ServerBootstrap(
            new NioServerSocketChannelFactory( Executors.newCachedThreadPool()
                                             , Executors.newCachedThreadPool()));

        ClientBootstrap clientBootstrap = new ClientBootstrap(
            new NioClientSocketChannelFactory( Executors.newCachedThreadPool()
                                             , Executors.newCachedThreadPool()));

        // TODO connect to all the nodes


    }

}

