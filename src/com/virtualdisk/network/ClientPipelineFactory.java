package com.virtualdisk.network;

import com.virtualdisk.client.*;
import com.virtualdisk.network.codec.*;

import org.jboss.netty.channel.*;

/**
 * Constructs the pipeline for clients.
 */
public class ClientPipelineFactory
implements ChannelPipelineFactory
{
    Client client;

    public ClientPipelineFactory(Client client)
    {
        this.client = client;
    }

    /**
     * Creates a pipeline.
     * @return  a pipeline with decoder, encoder, and handler.
     */
    @Override
    public ChannelPipeline getPipeline()
    {
        ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("decoder", new RequestDecoder());
        pipeline.addLast("encoder", new RequestEncoder());
        pipeline.addLast("handler", new ClientHandler(client));

        return pipeline;
    }
}

