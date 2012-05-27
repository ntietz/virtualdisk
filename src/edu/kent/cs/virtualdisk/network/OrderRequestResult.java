package edu.kent.cs.virtualdisk.network;

public class OrderRequestResult
implements RequestResult, Sendable
{
    protected boolean completed = false;    // describes whether the request itself had errors
    protected boolean success = false;      // describes whether the order was successful

    public OrderRequestResult(boolean c, boolean s)
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
        return Sendable.orderRequestResult;
    }

    public boolean successful()
    {
        return success;
    }
}

