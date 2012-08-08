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
extends Thread
{
    public static final int DEFAULT_BLOCK_SIZE = 1024;
    public static final int DEFAULT_PORT = 10000;

    private int blockSize = DEFAULT_BLOCK_SIZE;
    private int port = DEFAULT_PORT;
    private List<String> driveHandles = new ArrayList<String>();
    private List<Long> driveSizes = new ArrayList<Long>();

    private DataNode dataNode;

    public DataNodeMain(int port, int blockSize, List<String> driveHandles, List<Long> driveSizes)
    {
        this.port = port;
        this.blockSize = blockSize;
        this.driveHandles = driveHandles;
        this.driveSizes = driveSizes;
    }

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
        int blockSize = Integer.valueOf(args[1]);

        List<String> driveHandles = new ArrayList<String>();
        List<Long> driveSizes = new ArrayList<Long>();
        for (int index = 2; index < args.length; index += 2)
        {
            String driveHandle = args[index];
            driveHandles.add(driveHandle);
            long driveSize = Long.valueOf(args[index+1]);
            driveSizes.add(driveSize);
        }

        DataNodeMain main = new DataNodeMain(port, blockSize, driveHandles, driveSizes);
        main.start();
    }

    @Override
    public void run()
    {
        dataNode = new DataNode(blockSize, driveHandles, driveSizes);
        startDataNodeListener(port);
    }

    public void startDataNodeListener( int port
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

