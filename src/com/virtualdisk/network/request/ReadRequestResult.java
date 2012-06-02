package com.virtualdisk.network.request;
import com.virtualdisk.network.Sendable;

import java.util.Date;

public class ReadRequestResult
extends RequestResult
implements Sendable
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

    public byte messageType()
    {
        return Sendable.readRequestResult;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public byte[] getResult()
    {
        return result;
    }
}

