package com.virtualdisk.coordinator;

import org.junit.*;
import static org.junit.Assert.*;

public class NetworkServerTest
{
    @Test
    public void testSetTimeout()
    {
        long timeoutLength = 100L;
        NetworkServer.setTimeoutLength(timeoutLength);

        assertEquals("Timeout length should match.", timeoutLength, NetworkServer.timeoutLength());

        timeoutLength = 1000L;
        NetworkServer.setTimeoutLength(timeoutLength);

        assertEquals("Timeout length should match.", timeoutLength, NetworkServer.timeoutLength());
    }
}

