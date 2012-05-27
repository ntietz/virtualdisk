package edu.kent.cs.virtualdisk.network;

import org.jboss.netty.channel.*;

public class CoordinatorHandler
extends SimpleChannelHandler
{
    public void channelOpen( ChannelHandlerContext context
                           , ChannelStateEvent event
                           )
    {
        // register the channel
        // TODO
    }

    public void messageReceived( ChannelHandlerContext context
                               , MessageEvent event
                               )
    {
        Sendable obj = (Sendable) event.getMessage();

        switch (obj.messageType())
        {
            case Sendable.orderRequestResult :
                OrderRequestResult orderResult = (OrderRequestResult) obj;
                // TODO 
                break;

            case Sendable.writeRequestResult :
                WriteRequestResult writeResult = (WriteRequestResult) obj;
                // TODO
                break;

            case Sendable.readRequestResult :
                ReadRequestResult readResult = (ReadRequestResult) obj;
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
        // We probably need better error handling in here...
        // Perhaps we should check which channel this is, and either
        // try to reconnect to that node or begin reconfiguration
        // efforts based on what happened.
    }
}

