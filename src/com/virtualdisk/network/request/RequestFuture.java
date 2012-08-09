package com.virtualdisk.network.request;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.*;
import com.virtualdisk.network.util.Sendable.*;

public class RequestFuture
{
    private int requestId;
    private long sentTime; // when we ask the future for the result, we also check if it timed out
    private MessageType requestType;
    private RequestResult result; // CAN BE NULL
    private DataNodeIdentifier resultResponder;

    public RequestFuture(int requestId, long sentTime, MessageType requestType, DataNodeIdentifier resultResponder)
    {
        this.requestId = requestId;
        this.sentTime = sentTime;
        this.requestType = requestType;
        this.result = null;
        this.resultResponder = resultResponder;
    }

    public boolean isResponder(DataNodeIdentifier candidateResponder)
    {
        return resultResponder.equals(candidateResponder);
    }

    public DataNodeIdentifier getResponder()
    {
        return resultResponder;
    }

    public int getRequestId()
    {
        return requestId;
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

    public void setResult(RequestResult result)
    {
        this.result = result;
    }

    public boolean isDone()
    {
        if (result == null)
        {
            return false;
        }
        else
        {
            return result.isDone();
        }
    }

    public boolean hasResultSet()
    {
        return result != null;
    }

    public RequestResult getResult()
    {
        if (result != null)
        {
            return result;
        }
        else
        {
            boolean done;
            if (isTimedOut())
            {
                done = true;
            }
            else
            {
                done = false;
            }

            switch (requestType)
            {
                case orderRequest:
                case orderRequestResult:
                    return new OrderRequestResult(requestId, done, false);

                case readRequest:
                case readRequestResult:
                    return new ReadRequestResult(requestId, done, false, null, null);

                case writeRequest:
                case writeRequestResult:
                    return new WriteRequestResult(requestId, done, false);

                case volumeExistsRequest:
                case volumeExistsRequestResult:
                    return new VolumeExistsRequestResult(requestId, done, false, false);

                case createVolumeRequest:
                case createVolumeRequestResult:
                    return new CreateVolumeRequestResult(requestId, done, false);

                case deleteVolumeRequest:
                case deleteVolumeRequestResult:
                    return new DeleteVolumeRequestResult(requestId, done, false);

                case unsetSegmentRequest:
                case unsetSegmentRequestResult:
                    return new UnsetSegmentRequestResult(requestId, done, false);

                default:
                    return null;
            }
        }
    }
}

