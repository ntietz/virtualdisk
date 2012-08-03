package com.virtualdisk.network.util;

import org.jboss.netty.buffer.*;

public abstract class Sendable
{
    public static final int prime = 31;

    public abstract MessageType messageType();
    public abstract void decode(ChannelBuffer buffer);
    public abstract ChannelBuffer encode();
    public abstract ChannelBuffer addHeader(ChannelBuffer buffer);
    public abstract ChannelBuffer encodeWithHeader();
    public abstract int messageSize(); // EXCLUDES header size
    public int headerSize()
    {
        return 5; // 1 byte for message type, 4 for message length
    }

    public int hashCode()
    {
        return 17;
    }
    public abstract boolean equals(Object obj);
    
    /**
     * 
     * @param buffer    the channel buffer which contains a 
     * @return  the message type of the encoded message, or a signal for an empty buffer
     */
    public static MessageType encodedMessageType(ChannelBuffer buffer)
    {
        if (buffer.readableBytes() < 1)
        {
            buffer.resetReaderIndex();
            return MessageType.emptyBuffer;
        }

        MessageType type = MessageType.fromByte(buffer.readByte());
        
        buffer.resetReaderIndex();
        
        return type;
    }

    public static enum MessageType {
        emptyBuffer(0)
      , unknownRequest(1)
      , orderRequest(2)
      , orderRequestResult(3)
      , readRequest(4)
      , readRequestResult(5)
      , writeRequest(6)
      , writeRequestResult(7)
      , volumeExistsRequest(8)
      , volumeExistsRequestResult(9)
      , createVolumeRequest(10)
      , createVolumeRequestResult(11)
      , deleteVolumeRequest(12)
      , deleteVolumeRequestResult(13)
      , unsetSegmentRequest(14)
      , unsetSegmentRequestResult(15)
      , identifyRequest(16)
      , identifyRequestResult(17)
      , reconfigurationRequest(18)
      , reconfigurationRequestResult(19)
      ;
          
        private byte type;
        
        private MessageType(int t)
        {
            type = (byte)t;
        }
        
        public byte byteValue()
        {
            return type;
        }
        
        public static MessageType fromByte(byte t)
        {
            for (MessageType each : values())
            {
                if (t == each.byteValue())
                {
                    return each;
                }
            }
            
            return unknownRequest;
        }
    }
}

