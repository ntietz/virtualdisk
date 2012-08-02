package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.*;

/**
 * The Handler class is used to handle all kinds of network/coordinator requests. It must be subclassed to be used.
 */
public abstract class Handler extends Thread
{
    /**
     * The id assigned by the coordinator for this request.
     */
    protected int requestId;

    /**
     * The logical volume id the request is hitting.
     */
    protected int volumeId;

    /**
     * The logical offset the request is hitting. This is not used for all requests, such as create volume requests.
     */
    protected long logicalOffset;

    /**
     * The coordinator for the requests.
     */
    protected Coordinator coordinator;

    /**
     * The result of the request. This is set when the request finishes.
     */
    protected Sendable requestResult;

    /**
     * Setter for the request's id. This must be done.
     */
    public void setRequestId(int id)
    {
        requestId = id;
    }

    /**
     * Getter for the request's id.
     */
    public int getRequestId()
    {
        return requestId;
    }

    /**
     * Sets off the action, then passes along the result to the client.
     */
    public final void run()
    {
        action();

        // TODO : change these lines... they're gross!!!! we should have a queue or something.
        if (requestResult != null)
            SingletonCoordinator.sendToClient(requestId, requestResult);
        /*

        change to something like this:
            coordinator.getServer().set

        */
    }

    public abstract void action();
}
