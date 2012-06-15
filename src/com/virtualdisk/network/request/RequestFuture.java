package com.virtualdisk.network.request;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

public class RequestFuture
{
    private int requestId;
    private long sentTime; // when we ask the future for the result, we also check if it timed out
    private MessageType requestType;
    private RequestResult result; // CAN BE NULL

    public RequestFuture(int requestId, long sentTime, MessageType requestType)
    {
        this.requestId = requestId;
        this.sentTime = sentTime;
        this.requestType = requestType;
        this.result = null;
    }

    public boolean isTimedOut()
    {
        if ((System.currentTimeMillis() - sentTime) > NetworkServer.timeoutLength())
        {
            if (result != null)
            {
                // if we have a result stored, that means it returned before we checked timeout
                return false;
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isDone()
    {
        if (result == null)
        {
            return false;
        }
        else
        {
            return ((RequestResult) result).isDone();
        }
    }

    public RequestResult getResult()
    {
        if (result != null)
        {
            return result;
        }
        else
        {
            switch (requestType)
            {
                case orderRequest:
                case orderRequestResult:
                    result = new OrderRequestResult(requestId, true, false);
                    return result;

                case readRequest:
                case readRequestResult:
                    result = new ReadRequestResult(requestId, true, false, null, null);
                    return result;

                case writeRequest:
                case writeRequestResult:
                    result = new WriteRequestResult(requestId, true, false);
                    return result;

                case volumeExistsRequest:
                case volumeExistsRequestResult:
                    //...
                    break;

                case createVolumeRequest:
                case createVolumeRequestResult:
                    //...
                    break;

                case deleteVolumeRequest:
                case deleteVolumeRequestResult:
                    //...
                    break;

                default:
                    return null;
            }
        }
        // TODO TODO TODO remove this line, here just temporarily for compiling
        return null;
    }
}

