package com.virtualdisk.coordinator;

public abstract class Handler extends Thread
{
    protected boolean finished = false;
    protected int requestId;
    protected int volumeId;
    protected long logicalOffset;
    protected Coordinator coordinator;

    public boolean isFinished()
    {
        return finished;
    }

    public void setRequestId(int id)
    {
        requestId = id;
    }

    public int getRequestId()
    {
        return requestId;
    }
}
