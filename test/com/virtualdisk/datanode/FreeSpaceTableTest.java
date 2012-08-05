package com.virtualdisk.datanode;

import static org.junit.Assert.*;

import com.virtualdisk.util.DriveOffsetPair;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FreeSpaceTableTest
{

    @Test
    public void testTableOperations()
    {
        List<Long> driveSizes = new ArrayList<Long>();
        driveSizes.add(10L);
        driveSizes.add(20L);
        driveSizes.add(5L);
        
        FreeSpaceTable freeSpaceTable = new FreeSpaceTable(driveSizes.size(), driveSizes);
        
        assertEquals("Free space should be correct.", 35L, freeSpaceTable.totalFreeSpace());
        
        assertTrue("Next location should be free", freeSpaceTable.isFree(freeSpaceTable.next()));
        
        List<DriveOffsetPair> claimedLocations = new ArrayList<DriveOffsetPair>();
        
        for (int index = 10; index < 25; ++index)
        {
            DriveOffsetPair location = freeSpaceTable.next();
            freeSpaceTable.claim(location);
            assertFalse("Claimed location should not be free", freeSpaceTable.isFree(location));
            claimedLocations.add(location);
        }
        
        assertEquals("Free space should be correct.", 20L, freeSpaceTable.totalFreeSpace());
        
        for (DriveOffsetPair each : claimedLocations)
        {
            freeSpaceTable.release(each);
            assertTrue("Location should be released.", freeSpaceTable.isFree(each));
        }
        
        assertEquals("Free space should be correct.", 35L, freeSpaceTable.totalFreeSpace());
        
        for (int index = 0; index < 35; ++index)
        {
            freeSpaceTable.claim(freeSpaceTable.next());
        }
        
        assertEquals("Table should be full.", 0L, freeSpaceTable.totalFreeSpace());
        assertNull("Should have no valid locations to give", freeSpaceTable.next());
    }

}
