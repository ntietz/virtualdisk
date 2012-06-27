package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.util.*;

public abstract class Handler extends Thread
{
    protected boolean finished = false;
    protected int requestId;
    protected int volumeId;
    protected long logicalOffset;
    protected Coordinator coordinator;
    protected Sendable requestResult;

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

    public void run()
    {
        action();
        SingletonCoordinator.sendToClient(requestId, requestResult);
    }

    public abstract void action();
}
