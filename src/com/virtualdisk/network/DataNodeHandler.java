package com.virtualdisk.network;

import org.jboss.netty.channel.*;

public class DataNodeHandler
extends SimpleChannelHandler
{
    public void channelOpen( ChannelHandlerContext context
                           , ChannelStateEvent event
                           )
    {
        // TODO
    }

    public void messageReceived( ChannelHandlerContext context
                               , MessageEvent event
                               )
    {
        Sendable obj = (Sendable) event.getMessage();

        switch (obj.messageType())
        {
            case Sendable.orderRequest :
                OrderRequest orderRequest = (OrderRequest) obj;
                // TODO
                break;

            case Sendable.writeRequest :
                WriteRequest writeRequest = (WriteRequest) obj;
                // TODO
                break;

            case Sendable.readRequest :
                ReadRequest readRequest = (ReadRequest) obj;
                // TODO
                break;
        }
    }

    public void exceptionCaught( ChannelHandlerContext context
                               , ExceptionEvent event
                               )
    {
        event.getCause().printStackTrace();
        event.getChannel().close();
        // TODO
        // We probably need better error handling in here.
        // Perhaps we should check which channel this is 
        // (it should be the coordinator), try to reconnect,
        // and then if we can't reconnect... what do we do?
    }
}

