package com.virtualdisk.network;

import com.virtualdisk.network.codec.*;

import org.jboss.netty.channel.*;

public class ClientPipelineFactory
implements ChannelPipelineFactory
{
    public ChannelPipeline getPipeline()
    {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder", new RequestDecoder());
        pipeline.addLast("encoder", new RequestEncoder());
        pipeline.addLast("handler", new ClientHandler());

        return pipeline;
    }
}

