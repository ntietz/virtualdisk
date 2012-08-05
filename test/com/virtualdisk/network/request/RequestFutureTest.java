package com.virtualdisk.network.request;

import com.virtualdisk.network.util.Sendable.*;

import org.junit.*;
import static org.junit.Assert.*;

public class RequestFutureTest
{
    @Test
    public void test()
    {
        int requestId = 10;
        long sentTime = 1234;
        MessageType messageType = MessageType.orderRequest;

        RequestFuture future = new RequestFuture(requestId, sentTime, messageType);

        assertEquals(requestId, future.getRequestId());
        assertFalse(future.isDone());

        OrderRequestResult result = new OrderRequestResult(requestId, true, true);
        future.setResult(result);

        assertTrue(future.isDone());
        assertTrue(future.hasResultSet());

        assertFalse(future.isTimedOut());
        assertEquals(result, future.getResult());

        future.setResult(null);
        assertTrue(future.isTimedOut());

    }
}

