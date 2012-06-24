package com.virtualdisk.main;

import com.virtualdisk.network.util.*;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.socket.nio.*;

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
        
        List<DataNodeIdentifier> nodes; // TODO initialize

        //ServerBootstrap serverBootstrap = new ServerBootstrap(
        //    new NioServerSocketChannelFactory( Executors.newCachedThreadPool()
        //                                     , Executors.newCachedThreadPool()));

        // TODO switch to be server bootstrap!!!
        ClientBootstrap clientBootstrap = new ClientBootstrap(
            new NioClientSocketChannelFactory( Executors.newCachedThreadPool()
                                             , Executors.newCachedThreadPool()));

        // TODO 
    }

    public static void start( int port
                            )
    {

    }

}
