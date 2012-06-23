package com.virtualdisk.main;

import com.virtualdisk.network.*;
import com.virtualdisk.network.util.*;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.*;

import java.util.*;
import java.util.concurrent.*;

public class CoordinatorMain
{
    private static int blockSize;
    private static int segmentSize;
    private static int segmentGroupSize;
    private static int quorumSize;

    public static void main(String... args)
    {
        // TODO determine usage, convert to Apache Commons handling
    }

    public static void start( int port
                            , List<DataNodeIdentifier> nodes
                            )
    {

        ChannelFactory channelFactory = new NioClientSocketChannelFactory
            ( Executors.newCachedThreadPool()
            , Executors.newCachedThreadPool()
            );

        ClientBootstrap bootstrap = new ClientBootstrap(channelFactory);

        bootstrap.setPipelineFactory(new CoordinatorPipelineFactory());
    }
}

