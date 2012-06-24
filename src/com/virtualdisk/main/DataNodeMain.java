package com.virtualdisk.main;

import com.virtualdisk.network.util.*;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class DataNodeMain
{
    private static final int DEFAULT_BLOCK_SIZE = 1024;

    private static int blockSize = DEFAULT_BLOCK_SIZE;
    private static List<String> driveHandles = new ArrayList<String>();
    private static List<Long> driveSizes = new ArrayList<Long>();

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

        // TODO assign drive info lists
        // TODO instantiate DataNode using SingletonDataNode

        start(port);
    }

    public static void start( int port
                            )
    {
        ChannelFactory channelFactory = new NioServerSocketChannelFactory
            ( Executors.newCachedThreadPool()
            , Executors.newCachedThreadPool()
            );

        ServerBootstrap bootstrap = new ServerBootstrap(channelFactory);

        bootstrap.bind(new InetSocketAddress(port));
    }
}

