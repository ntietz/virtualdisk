package com.virtualdisk.network.request;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.util.Arrays;
import java.util.Date;

public class ReadRequestResult
extends RequestResult
{
    protected byte[] result = null;
    protected Date timestamp = null;

    public ReadRequestResult(boolean c, boolean s, byte[] r, Date ts)
    {
        completed = c;
        successful = s;
        result = r;
        timestamp = ts;
    }

    public MessageType messageType()
    {
        return MessageType.readRequestResult;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public byte[] getResult()
    {
        return result;
    }
    
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        
        buffer.writeInt(result.length);
        buffer.writeBytes(result);
        buffer.writeLong(timestamp.getTime());
        buffer.writeByte(completed ? 1 : 0);
        buffer.writeByte(successful ? 1 : 0);
        
        return buffer;
    }
    
    public boolean decode(ChannelBuffer buffer)
    {
        if (buffer.readableBytes() < 4)
        {
            return false;
        }
        
        int length = buffer.readInt();
        
        if (buffer.readableBytes() < length + 6)
        {
            buffer.resetReaderIndex();
            return false;
        }
        else
        {
            result = new byte[length];
            buffer.readBytes(result);
            timestamp = new Date(buffer.readLong());
            completed = (buffer.readByte() == 1) ? true : false;
            successful = (buffer.readByte() == 1) ? true : false;
            return true;
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(this.result);
        result = prime * result
                + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (!(obj instanceof ReadRequestResult))
        {
            return false;
        }
        ReadRequestResult other = (ReadRequestResult) obj;
        if (!Arrays.equals(result, other.result))
        {
            return false;
        }
        if (timestamp == null)
        {
            if (other.timestamp != null)
            {
                return false;
            }
        } else if (!timestamp.equals(other.timestamp))
        {
            return false;
        }
        return true;
    }
}

