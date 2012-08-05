package com.virtualdisk.datanode;

import static org.junit.Assert.*;

import com.virtualdisk.util.DriveOffsetPair;

import org.junit.Test;

import java.util.Date;

public class TimestampTableTest
{

    @Test
    public void testTableOperations()
    {
        TimestampTable timestampTable = new TimestampTable();
        
        timestampTable.addVolume(0);
        assertNull("Unassigned timestamps should be null.", timestampTable.getTimestamp(0, 0));
        timestampTable.setTimestamp(0, 0, new Date(10));
        assertEquals("Timestamps should match", new Date(10), timestampTable.getTimestamp(0, 0));
        timestampTable.removeTimestamp(0, 0);
        assertNull("Unassigned timestamps should be null.", timestampTable.getTimestamp(0, 0));
        timestampTable.removeVolume(0);

        assertNull("Uncreated volume should give null.", timestampTable.getTimestamp(13, 10));
    }

}
