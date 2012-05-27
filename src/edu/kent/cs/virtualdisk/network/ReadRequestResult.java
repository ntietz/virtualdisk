package edu.kent.cs.virtualdisk.network;
import java.util.Date;

public class ReadRequestResult
implements RequestResult, Sendable
{
    protected boolean completed = false;
    protected byte[] result = null;
    protected Date timestamp = null;

    public ReadRequestResult(boolean c, byte[] r, Date ts)
    {
        completed = c;
        result = r;
        timestamp = ts;
    }

    public boolean completed()
    {
        return completed;
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

