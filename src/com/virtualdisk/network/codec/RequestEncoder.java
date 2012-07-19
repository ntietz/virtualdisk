package com.virtualdisk.network.codec;

import com.virtualdisk.network.util.*;

import org.jboss.netty.channel.*;
import org.jboss.netty.buffer.*;
import org.jboss.netty.handler.codec.oneone.*;

/**
 * Encodes Sendable-type messages.
 */
public class RequestEncoder
extends OneToOneEncoder
{
    /**
     * Encodes the supplied message (passes through if not Sendable-type).
     *
     * @param   context     not used
     * @param   channel     not used
     * @param   rawMessage  the message to encode
     * @return  a ChannelBuffer containing the encoded message
     */
    @Override
    protected Object encode( ChannelHandlerContext context
                           , Channel channel
                           , Object rawMessage
                           )
    {
        if (rawMessage instanceof Sendable)
        {
            Sendable message = (Sendable) rawMessage;
            ChannelBuffer buffer = message.encodeWithHeader();
            return buffer;
        }
        else
        {
            return rawMessage;
        }
    }
}

