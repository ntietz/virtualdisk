package com.virtualdisk.datanode;

import static org.junit.Assert.*;

import org.junit.*;

public class VolumeTableTest
{

    @Test(expected=NullPointerException.class)
    public void testVolumeNotCreated()
    {
        VolumeTable table = new VolumeTable();
        table.getAllPhysicalLocations(0);
    }
    
    @Test
    public void testVolumeUse()
    {
        fail("Not yet implemented");
    }

}
