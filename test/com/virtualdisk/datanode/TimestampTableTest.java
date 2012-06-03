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
        assertNull("Unassigned timestamps should be null.", timestampTable.getTimestamp(new DriveOffsetPair(0, 0)));
        timestampTable.setTimestamp(new DriveOffsetPair(0, 0), new Date(10));
        assertEquals("Timestamps should match", new Date(10), timestampTable.getTimestamp(new DriveOffsetPair(0, 0)));
        timestampTable.removeTimestamp(new DriveOffsetPair(0, 0));
        assertNull("Unassigned timestamps should be null.", timestampTable.getTimestamp(new DriveOffsetPair(0, 0)));
        timestampTable.removeVolume(0);
    }

}
