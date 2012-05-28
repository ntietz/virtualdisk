package com.virtualdisk.datanode;

import com.virtualdisk.util.DriveOffsetPair;
import com.virtualdisk.util.Range;

import java.util.*;

public class FreeSpaceTable
{
    List<List<Range>> table;
    int numberOfDrives;
    
    /**
     * @param n             the number of drives the table describes
     * @param driveSize     a list of drive sizes 
     */
    public FreeSpaceTable(int n, List<Long> driveSize)
    {
        numberOfDrives = n;
        table = new ArrayList<List<Range>>(numberOfDrives);
        
        for (int drive = 0; drive < numberOfDrives; ++drive)
        {
            Range wholeDrive = new Range(0, driveSize.get(drive));
            List<Range> driveSpace = new ArrayList<Range>();
            driveSpace.add(wholeDrive);
            table.add(driveSpace);
        }
    }
    
    /**
     * @return  the next free disk location
     */
    public DriveOffsetPair next()
    {
        // iterate through the drives for a free location until one is found
        // construct a drive-offset pair from the free location and return it
        for (int drive = 0; drive < numberOfDrives; ++drive)
        {
            List<Range> driveTable = table.get(drive);
            
            if (driveTable.size() > 0)
            {
                Range space = driveTable.get(0);
                DriveOffsetPair location = new DriveOffsetPair(drive, space.getBegin());
                return location;
            }
        }

        // returns null if no free space is found
        return null;
    }
    
    /**
     * @param location  the disk location we are checking to see if is free
     * @return          true if the location is free, false if it is in use
     */
    public boolean isFree(DriveOffsetPair location)
    {
        List<Range> driveTable = table.get(location.getDriveNumber());
        
        for (int index = 0; index < driveTable.size(); ++index)
        {
            Range space = driveTable.get(index);
            if (space.contains(location.getOffset()))
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * @param location  the disk location we are trying to claim set as in-use
     * @return          false if it is already in use, true if we successfully claim it
     */
    public boolean claim(DriveOffsetPair location)
    {
        List<Range> driveTable = table.get(location.getDriveNumber());
        
        for (int index = 0; index < driveTable.size(); ++index)
        {
            Range space = driveTable.get(index);
            if (space.contains(location.getOffset()))
            {
                driveTable.remove(index);
                
                Range preSpace = new Range(space.getBegin(), location.getOffset()-1);
                if (preSpace.valid())
                {
                    driveTable.add(preSpace);
                }
                
                Range postSpace = new Range(location.getOffset()+1, space.getEnd());
                if (postSpace.valid())
                {
                    driveTable.add(postSpace);
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * @param location  the disk location we wish to release
     * @return          true if we released the location, false if it was not already in use
     */
    public boolean release(DriveOffsetPair location)
    {
        List<Range> driveTable = table.get(location.getDriveNumber());
        
        for (int index = 0; index < driveTable.size(); ++index)
        {
            Range space = driveTable.get(index);
            
            if (space.contains(location.getOffset()))
            {
                return false;
            }
            else if (space.adjacent(location.getOffset()))
            {
                if (space.getBegin() == location.getOffset()+1)
                {
                    driveTable.set(index, new Range(location.getOffset(), space.getEnd()));
                }
                else
                {
                    driveTable.set(index, new Range(space.getBegin(), location.getOffset()));
                }
                
                return true;
            }
        }
        
        driveTable.add(new Range(location.getOffset(), location.getOffset()));
        
        return true;
    }

    /**
     * @return  the total number of free blocks the FreeSpaceTable describes
     */
    public long totalFreeSpace()
    {
        long freeSpace = 0;
        
        for (List<Range> drive : table)
        {
            for (Range range : drive)
            {
                freeSpace += (range.getEnd() - range.getBegin()) + 1;
            }
        }
        
        return freeSpace;
    }
}
