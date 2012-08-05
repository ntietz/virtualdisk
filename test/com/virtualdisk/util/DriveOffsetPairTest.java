package com.virtualdisk.util;

import org.junit.*;
import static org.junit.Assert.*;

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
