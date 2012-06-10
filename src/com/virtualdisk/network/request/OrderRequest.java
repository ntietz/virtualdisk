package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

import java.util.*;

public class OrderRequest
extends BlockRequest
{
    protected Date timestamp;

    public OrderRequest(int requestId, int volumeId, long logicalOffset, Date timestamp)
    {
        super(requestId, volumeId, logicalOffset);
        this.timestamp = timestamp;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public MessageType messageType()
    {
        return MessageType.orderRequest;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj instanceof OrderRequest)
        {
            OrderRequest other = (OrderRequest) obj;

            return super.equals(other)
                && timestamp.equals(other.getTimestamp());
        }
        else
        {
            return false;
        }
    }
}

