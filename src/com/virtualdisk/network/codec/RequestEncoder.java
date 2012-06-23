package com.virtualdisk.network.codec;

import com.virtualdisk.network.util.*;

import org.jboss.netty.channel.*;
import org.jboss.netty.buffer.*;

public class RequestEncoder
extends SimpleChannelHandler
{
    public void writeRequested(ChannelHandlerContext context, MessageEvent event)
    {
        Sendable message = (Sendable) event.getMessage();
        
        ChannelBuffer buffer = message.encode();

        Channels.write(context, event.getFuture(), buffer);
    }
}

