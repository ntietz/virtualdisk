package com.virtualdisk.client;

import com.virtualdisk.network.*;
import com.virtualdisk.network.request.*;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.buffer.*;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * The Client class provides an easy API for accessing the Virtualdisk system, handling all the network requests for you.
 */
public class Client
{
    /**
     * The default block size for the system.
     */
    private static final int DEFAULT_BLOCK_SIZE = 1024;

    /**
     * The block size the client is configured to use.
     */
    private static int blockSize = DEFAULT_BLOCK_SIZE;

    /**
     * The last used request id; this is used to generate uique ids for each request.
     * Note: these do not have to be synchronized between clients, as they're only used for
     * pairing requests with results on the client side; the coordinator registers callbacks
     * when the channel is connected, independent of request ids.
     */
    private static int requestId = 0;

    /**
     * The hostname of the coordinator.
     */
    private String host;

    /**
     * The port of the coordinator.
     */
    private int port;

    /**
     * The channel the coordinator is connected on.
     */
    private Channel channel;

    /**
     * The channel factory. This is only a member so that its resources can be released later.
     */
    private ChannelFactory channelFactory;

    /**
     * Clients must be constructed with a hostname and port for the client.
     *
     * @param   host    the coordinator's hostname
     * @param   port    the coordinator's port
     */
    public Client(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    /**
     * Getter for the block size.
     *
     * @return  the block size
     */
    public int getBlockSize()
    {
        return blockSize;
    }

    /**
     * Connects to the coordinator we are 
     */
    public boolean connect()
    {
        channelFactory = new NioClientSocketChannelFactory
            ( Executors.newCachedThreadPool()
            , Executors.newCachedThreadPool()
            );

        ClientBootstrap bootstrap = new ClientBootstrap(channelFactory);
        bootstrap.setPipelineFactory(new ClientPipelineFactory());

        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        future.awaitUninterruptibly();
        if (future.isDone() && future.isSuccess())
        {
            channel = future.getChannel();
            System.out.println("Connected to the coordinator.");
            return true;
        }
        else
        {
            System.out.println("Error connecting to the coordinator.");
            return false;
        }
    }

    /**
     * Sends a create-volume request.
     */
    public void createVolume(int volumeId)
    {
        ++requestId;
        CreateVolumeRequest request = new CreateVolumeRequest(requestId, volumeId);
        channel.write(request);
    }

    /**
     * Sends a delete-volume request.
     */
    public void deleteVolume(int volumeId)
    {
        ++requestId;
        DeleteVolumeRequest request = new DeleteVolumeRequest(requestId, volumeId);
        channel.write(request);
    }

    /**
     * Sends a write request with the supplied arguments.
     */
    public void write(int volumeId, long logicalOffset, byte[] block)
    {
        ++requestId;
        WriteRequest request = new WriteRequest(requestId, volumeId, logicalOffset, new Date(), block);
        channel.write(request);
        System.out.println("Sent request " + requestId);
    }

    /**
     * Sends a read request with the supplied arguments.
     */
    public void read(int volumeId, long logicalOffset)
    {
        ++requestId;
        ReadRequest request = new ReadRequest(requestId, volumeId, logicalOffset);
        channel.write(request);
    }

    /**
     * Disconnects from the coordinator.
     */
    public void disconnect()
    {
        try
        {
            channel.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            channelFactory.releaseExternalResources();
        }
        catch (Exception e)
        {
            System.exit(0);
        }
    }
}

