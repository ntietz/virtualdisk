package com.virtualdisk.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class DriveOffsetPairTest
{

    @Test
    public void test()
    {
        int driveNumber = 10;
        long offset = 20L;
        
        DriveOffsetPair driveOffsetPair = new DriveOffsetPair(driveNumber, offset);
        
        assertEquals("Drive number should match", driveNumber, driveOffsetPair.getDriveNumber());
        assertEquals("Offset should match", offset, driveOffsetPair.getOffset());
    }

}
