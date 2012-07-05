package com.virtualdisk.network;

import com.virtualdisk.datanode.*;

import com.virtualdisk.network.*;
import com.virtualdisk.network.codec.*;

import org.jboss.netty.channel.*;

public class DataNodePipelineFactory
implements ChannelPipelineFactory
{
    public ChannelPipeline getPipeline()
    {
        ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("decoder", new RequestDecoder());
        pipeline.addLast("encoder", new RequestEncoder());
        pipeline.addLast("handler", new DataNodeHandler(SingletonDataNode.getDataNode()));

        return pipeline;
    }
}

