package edu.kent.cs.virtualdisk.network;

public class WriteRequestResult
implements RequestResult, Sendable
{
    protected boolean completed = false;
    protected boolean success = false;

    public WriteRequestResult(boolean c, boolean s)
    {
        completed = c;
        success = s;
    }

    public boolean completed()
    {
        return completed;
    }

    public byte messageType()
    {
        return Sendable.writeRequestResult;
    }

    public boolean successful()
    {
        return success;
    }
}

