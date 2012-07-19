package com.virtualdisk.network;

import com.virtualdisk.datanode.*;

import com.virtualdisk.network.*;
import com.virtualdisk.network.codec.*;

import org.jboss.netty.channel.*;

/**
 * Constructs the pipeline for datanodes.
 */
public class DataNodePipelineFactory
implements ChannelPipelineFactory
{
    private DataNode dataNode;

    private DataNodePipelineFactory() { }
    public DataNodePipelineFactory(DataNode dataNode)
    {
        this.dataNode = dataNode;
    }

    /**
     * Creates a pipeline for a specific datanode, ensuring the handler is setup properly.
     * @return  a pipeline with decoder, encoder, and handler.
     */
    @Override
    public ChannelPipeline getPipeline()
    {
        ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("decoder", new RequestDecoder());
        pipeline.addLast("encoder", new RequestEncoder());
        pipeline.addLast("handler", new DataNodeHandler(dataNode));

        return pipeline;
    }
}

