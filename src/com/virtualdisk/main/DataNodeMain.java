package com.virtualdisk.main;

import com.virtualdisk.datanode.*;
import com.virtualdisk.network.*;
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

    private static DataNode dataNode;

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

        List<String> driveHandles = new ArrayList<String>();
        List<Long> driveSizes = new ArrayList<Long>();
        for (int index = 1; index < args.length; index += 2)
        {
            String driveHandle = args[index];
            driveHandles.add(driveHandle);
            long driveSize = Long.valueOf(args[index+1]);
            driveSizes.add(driveSize);
        }

        dataNode = DataNodeFactory.setup(port, driveHandles, driveSizes);

        startDataNodeListener(port);
    }

    public static void startDataNodeListener( int port
                                            )
    {
        ChannelFactory channelFactory = new NioServerSocketChannelFactory
            ( Executors.newCachedThreadPool()
            , Executors.newCachedThreadPool()
            );

        ServerBootstrap bootstrap = new ServerBootstrap(channelFactory);
        bootstrap.setPipelineFactory(new DataNodePipelineFactory(dataNode));

        bootstrap.bind(new InetSocketAddress(port));
    }
}

