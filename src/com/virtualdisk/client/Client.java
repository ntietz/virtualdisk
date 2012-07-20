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
 * An asychronous client for the Virtualdisk project.
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
     * Maps a request id onto true if it has finished, false or null otherwise.
     */
    private Map<Integer, Boolean> finishedMap = new HashMap();

    /**
     * Maps a request id onto true if the result has finished and succeeded, false if it has finished and failed.
     */
    private Map<Integer, Boolean> successMap = new HashMap();

    /**
     * The result map for reading.
     */
    private Map<Integer, byte[]> readResults = new HashMap();

    /**
     * The last used request id; this is used to generate uique ids for each request.
     * Note: these do not have to be synchronized between clients, as they're only used for
     * pairing requests with results on the client side; the coordinator registers callbacks
     * when the channel is connected, independent of request ids.
     */
    private static int lastAssignedId = 0;

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
        bootstrap.setPipelineFactory(new ClientPipelineFactory(this));

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
    public int createVolume(int volumeId)
    {
        int requestId = generateNewRequestId();

        CreateVolumeRequest request = new CreateVolumeRequest(requestId, volumeId);
        channel.write(request);

        return requestId;
    }

    /**
     * Sends a delete-volume request.
     */
    public int deleteVolume(int volumeId)
    {
        int requestId = generateNewRequestId();

        DeleteVolumeRequest request = new DeleteVolumeRequest(requestId, volumeId);
        channel.write(request);

        return requestId;
    }

    /**
     * Sends a write request with the supplied arguments.
     */
    public int write(int volumeId, long logicalOffset, byte[] block)
    {
        int requestId = generateNewRequestId();

        WriteRequest request = new WriteRequest(requestId, volumeId, logicalOffset, new Date(), block);
        channel.write(request);

        return requestId;
    }

    /**
     * Sends a read request with the supplied arguments.
     */
    public int read(int volumeId, long logicalOffset)
    {
        int requestId = generateNewRequestId();

        ReadRequest request = new ReadRequest(requestId, volumeId, logicalOffset);
        channel.write(request);

        return requestId;
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

    public void setSuccess(int requestId, boolean success)
    {
        successMap.put(requestId, success);
    }

    public void setFinished(int requestId, boolean success)
    {
        finishedMap.put(requestId, success);
    }

    public void setReadResult(int requestId, byte[] block)
    {
        readResults.put(requestId, block);
    }

    public boolean wasSuccessful(int requestId)
    {
        Boolean success = successMap.get(requestId);

        if (success == null)
        {
            success = false;
        }

        return success;
    }

    public boolean hasFinished(int requestId)
    {
        Boolean finished = finishedMap.get(requestId);

        if (finished == null)
        {
            finished = false;
        }

        return finished;
    }

    public byte[] getReadResult(int requestId)
    {
        return readResults.get(requestId);
    }

    private synchronized int generateNewRequestId()
    {
        ++lastAssignedId;
        return lastAssignedId;
    }
}

