package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.*;

public abstract class Handler extends Thread
{
    protected int requestId;
    protected int volumeId;
    protected long logicalOffset;
    protected Coordinator coordinator;
    protected Sendable requestResult;

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

        // TODO : change this line... it's kind of gross.
        SingletonCoordinator.sendToClient(requestId, requestResult);
    }

    public abstract void action();
}
