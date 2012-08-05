package com.virtualdisk.network.util;

import org.junit.*;
import static org.junit.Assert.*;

public class DataNodeStatusTest
{
    @Test
    public void test()
    {
        int blockSize = 10;
        int segmentSize = 15;

        DataNodeStatus status = new DataNodeStatus(blockSize, segmentSize);

        assertEquals("Should be empty", 0, status.getSegmentsStored());

        status.addStoredSegments(5);
        status.addStoredSegments(7);

        assertEquals("Should be 12", 12, status.getSegmentsStored());
    }
}

