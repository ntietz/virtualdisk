package com.virtualdisk.network;

import org.jboss.netty.channel.*;

public class DataNodePipelineFactory
implements ChannelPipelineFactory
{
    public ChannelPipeline getPipeline()
    throws Exception
    {
        ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("decoder", new RequestDecoder());
        pipeline.addLast("encoder", new RequestEncoder());
        pipeline.addLast("handler", new DataNodeHandler());

        return pipeline;
    }
}

