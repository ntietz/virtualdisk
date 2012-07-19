package com.virtualdisk.network;

import com.virtualdisk.network.codec.*;

import org.jboss.netty.channel.*;

/**
 * Constructs the pipeline for clients.
 */
public class ClientPipelineFactory
implements ChannelPipelineFactory
{
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
        pipeline.addLast("handler", new ClientHandler());

        return pipeline;
    }
}

