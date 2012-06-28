package com.virtualdisk.datanode;

import static org.junit.Assert.*;

import com.virtualdisk.util.DriveOffsetPair;

import org.junit.*;

public class VolumeTableTest
{
    @Test
    public void testVolumeUse()
    {
        VolumeTable volumeTable = new VolumeTable();
        
        assertTrue("Volume should not exist", !volumeTable.exists(0));
        volumeTable.addVolume(0);
        assertTrue("Volume should exist", volumeTable.exists(0));
        volumeTable.addVolume(0);
        volumeTable.addVolume(1);
        
        assertNull("Unset location should be null.", volumeTable.getPhysicalLocation(0, 0));
        
        DriveOffsetPair standardLocation = new DriveOffsetPair(0, 1L);
        
        volumeTable.setPhysicalLocation(0, 0, standardLocation);
        
        assertEquals("Locations should match.", standardLocation, volumeTable.getPhysicalLocation(0, 0));
        
        volumeTable.unsetPhysicalLocation(0, 0);
        
        assertNull("Unset locaton should be null.", volumeTable.getPhysicalLocation(0, 0));
        
        for (int index = 0; index < 10; ++index)
        {
            DriveOffsetPair location = new DriveOffsetPair(0, index);
            volumeTable.setPhysicalLocation(0, index, location);
        }
        
        assertEquals("Number of locations should be correct", 10, volumeTable.getAllPhysicalLocations(0).size());
        
        volumeTable.removeVolume(0);
        volumeTable.addVolume(0);
        
        assertEquals("Volume should be empty", 0, volumeTable.getAllPhysicalLocations(0).size());
    }
}

