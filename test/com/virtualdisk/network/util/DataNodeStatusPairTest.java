package com.virtualdisk.network.util;

import org.junit.*;
import static org.junit.Assert.*;

public class DataNodeStatusPairTest
{
    @Test
    public void test()
    {
        int blockSize = 10;
        int segmentSize = 15;

        DataNodeStatus statusA = new DataNodeStatus(blockSize, segmentSize);
        DataNodeIdentifier idA = new DataNodeIdentifier(1, "A", 8000);
        DataNodeStatusPair pairA = new DataNodeStatusPair(idA, statusA);

        DataNodeStatus statusB = new DataNodeStatus(blockSize, segmentSize);
        DataNodeIdentifier idB = new DataNodeIdentifier(2, "B", 8001);
        DataNodeStatusPair pairB = new DataNodeStatusPair(idB, statusB);

        assertEquals(idA, pairA.getIdentifier());
        assertEquals(idB, pairB.getIdentifier());
        assertEquals(statusA, pairA.getStatus());
        assertEquals(statusB, pairB.getStatus());

        statusA.addStoredSegments(15);
        statusB.addStoredSegments(10);

        assertEquals(1, pairA.compareTo(pairB));
        assertEquals(-1, pairB.compareTo(pairA));
        assertEquals(0, pairA.compareTo(pairA));

    }
}

