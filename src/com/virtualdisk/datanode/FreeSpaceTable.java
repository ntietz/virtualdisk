package com.virtualdisk.datanode;

import com.virtualdisk.util.DriveOffsetPair;
import com.virtualdisk.util.Range;

import java.util.*;
import java.util.concurrent.*;

/**
 * FreeSpaceTable keeps track of where free locations are on disks for a DataNode.
 * @author  Nicholas Tietz
 */
public class FreeSpaceTable
{
    /**
     * Stores the free space available.
     * Each range is a block of free space (where the end points, and everything between, are free)
     * Each list of ranges is the free space for one disk.
     * The list of lists of ranges is the free space for all disks.
     */
    private List<List<Range>> table;

    /**
     * The number of disks the freespace table is managing.
     */
    private int numberOfDrives;
    
    /**
     * Standard constructor.
     * @param numberOfDrives    the number of drives to manage
     * @param driveSize         a list of the sizes of the drives, in number of blocks
     */
    public FreeSpaceTable(int numberOfDrives, List<Long> driveSize)
    {
        this.numberOfDrives = numberOfDrives;
        table = Collections.synchronizedList(new ArrayList<List<Range>>(numberOfDrives));
        
        for (int drive = 0; drive < numberOfDrives; ++drive)
        {
            Range wholeDrive = new Range(0, driveSize.get(drive)-1);
            List<Range> driveSpace = Collections.synchronizedList(new ArrayList<Range>());
            driveSpace.add(wholeDrive);
            table.add(driveSpace);
        }
    }
    
    /**
     * Selects and returns the next available disk location. Naive algorithm which picks the first free block and does not attempt to optimize locations.
     * @return  the next free disk location as a DriveOffsetPair
     */
    public DriveOffsetPair next()
    {
        // Naive algorithm which simply selects the first free block.
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
     * Checks whether or not the supplied disk location is available to assign.
     * @param   location    the disk location we are checking to see if is free
     * @return  true if the location is free, false if it is in use
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
     * Attempts to put the supplied disk location into use; it returns whether or not it was able to do so.
     * @param   location    the disk location we are trying to claim set as in-use
     * @return  false if it is already in use, true if we successfully claim it
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
     * Attempts to release a given disk location.
     * @param   location    the disk location we wish to release
     * @return  true if we released the location, false if it was not already in use
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
     * Checks the total number of free blocks in the drives the table is configured for.
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
