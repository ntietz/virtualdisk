package com.virtualdisk.datanode;

import com.virtualdisk.datanode.Drive;
import com.virtualdisk.util.DriveOffsetPair;
import com.virtualdisk.util.Range;

import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.io.RandomAccessFile;
import java.io.File;
import java.util.ArrayList;

public class DataNode
{

    /*
     * The volume table maps integer volume IDs onto diskmaps.
     * Diskmaps are simply maps of integers onto logical offsets (integers) onto physical offsets (integers).
     */
    //protected Map<Integer,Map<Integer,DriveOffsetPair>> volumeTable;
    protected VolumeTable volumeTable;

    /*
     * The free space table contains ranges of free space which are able to be allocated.
     * It is indexed as freeSpaceTable[drive][rangeNumber]
     */
    //protected List<List<Range>> freeSpaceTable;
    protected FreeSpaceTable freeSpaceTable;

    /*
     * The timestamp tables map each volume ID to a map of each logical offset to a timestamp.
     * The timestamps are Dates, so they are millisecond precision.
     */
    //protected Map<Integer,Map<Integer,Date>> orderTimestampTable;
    protected TimestampTable orderTimestampTable;
    //protected Map<Integer,Map<Integer,Date>> valueTimestampTable;
    protected TimestampTable valueTimestampTable;

    /*
     * A list of all the drives the node can use.
     */
    protected List<Drive> drives;

    /*
     * Sets up a DataNode with the provided characteristics
     */
    public DataNode(int blockSize, List<String> driveHandles, List<Long> driveSizes)
    {
        int numberOfDrives = driveHandles.size();
        
        // initialize and add all the drives
        drives = new ArrayList<Drive>(numberOfDrives);
        for (int index = 0; index < numberOfDrives; ++index)
        {
            Drive current = new Drive(blockSize, driveSizes.get(index), driveHandles.get(index));
            drives.add(index, current);
        }

        // create an empty volume table
        volumeTable = new VolumeTable();

        // initialize and add all the free spaces
        freeSpaceTable = new FreeSpaceTable(numberOfDrives, driveSizes);

        // create empty timestamp tables
        orderTimestampTable = new TimestampTable();
        valueTimestampTable = new TimestampTable();
    }

    /*
     * Creates a diskmap for a new volume with the given volume ID.
     * Returns the status of the insertion.
     */
    public void createVolume(int volumeId)
    {
        volumeTable.addVolume(volumeId);
        orderTimestampTable.addVolume(volumeId);
        valueTimestampTable.removeVolume(volumeId);
    }

    /*
     * Attempts to delete the volume.
     * Returns the status of the deletion.
     */
    public Boolean deleteVolume(int volumeId)
    {
        Collection<DriveOffsetPair> allocatedLocations = volumeTable.getAllPhysicalLocations(volumeId);

        for (DriveOffsetPair location : allocatedLocations)
        {
            freeSpaceTable.release(location);
        }

        volumeTable.removeVolume(volumeId);
        orderTimestampTable.removeVolume(volumeId);
        valueTimestampTable.removeVolume(volumeId);

        return true;
    }

    /*
     * This function performs an order request.
     */
    public Boolean order(int volumeId, long logicalOffset, Date timestamp)
    {
        Date currentOrderTimestamp = getOrderTimestamp(volumeId, logicalOffset);
        Date currentValueTimestamp = getValueTimestamp(volumeId, logicalOffset);

        // TODO: fix this logic so that negative timestamps to not break the system. fine for now...
        if (currentOrderTimestamp == null)
        {
            currentOrderTimestamp = new Date(0);
        }
        if (currentValueTimestamp == null)
        {
            currentValueTimestamp = new Date(0);
        }
 
        if (currentOrderTimestamp.after(timestamp) || currentValueTimestamp.after(timestamp))
        {
            return false;
        }
        else
        {
            setOrderTimestamp(volumeId, logicalOffset, timestamp);

            return true;
        }
    }

    /*
     * This function writes a single block to the disk.
     * It chooses the first available location on the disk and writes it in that location, along with updating all the necessary timestamps.
     */
    public boolean write(int volumeId, long logicalOffset, byte[] block, Date timestamp)
    {
        // for each byte in the data:
        //      update/write timestamp
        //      check if it already has a written value
        //      if yes, then overwrite
        //      if no, then allocate space and write the value in

        //TODO: add exception-checking for the correct length of data (block size)

        Date orderTimestamp = getOrderTimestamp(volumeId, logicalOffset);
        Date valueTimestamp = getValueTimestamp(volumeId, logicalOffset);

        if (orderTimestamp == null)
        {
            return false; // you cannot write if an ordering has not been performed
        }
        if (valueTimestamp == null)
        {
            valueTimestamp = new Date(-1);
        }

        boolean writeable = timestamp.after(valueTimestamp) && ! timestamp.before(orderTimestamp);

        if (writeable)
        {

            DriveOffsetPair location = getDriveOffsetPair(volumeId, logicalOffset);

            if (location == null)
            {
                // test and set loop to ensure that our location is actually owned by us
                do {
                    location = freeSpaceTable.next();
                } while (!freeSpaceTable.claim(location));
                
                setDriveOffsetPair(volumeId, logicalOffset, location);
            }

            setValueTimestamp(volumeId, logicalOffset, timestamp);

            return drives.get(location.getDriveNumber()).write(location.getOffset(), block);
        }
        else
        {
            return false;
        }
    }

    /*
     * This method reads and returns the specified block from the disk.
     * If the block is empty, null is returned.
     */
    public byte[] read(int volumeId, long logicalOffset)
    {
        DriveOffsetPair physicalOffset = getDriveOffsetPair(volumeId, logicalOffset);

        if (physicalOffset == null)
        {
            return null;
        }
        else
        {
            return drives.get(physicalOffset.getDriveNumber()).read(physicalOffset.getOffset());
        }
    }

    /*
     * Fetches the ordering timestamp for a given volume ID and logical offset.
     */
    public Date getOrderTimestamp(int volumeId, long logicalOffset)
    {
        return orderTimestampTable.getTimestamp(new DriveOffsetPair(volumeId, logicalOffset));
    }

    /*
     * Fetches the value timestamp for a given volume ID and the logical offset.
     */
    public Date getValueTimestamp(int volumeId, long logicalOffset)
    {
        return valueTimestampTable.getTimestamp(new DriveOffsetPair(volumeId, logicalOffset));
    }

    /*
     * Sets the ordering timestamp for a given volume ID and logical offset.
     */
    public void setOrderTimestamp(int volumeId, long logicalOffset, Date timestamp)
    {
        orderTimestampTable.setTimestamp(new DriveOffsetPair(volumeId, logicalOffset), timestamp);
    }

    /*
     * Sets the value timestamp for a given volume ID and the logical offset.
     */
    public void setValueTimestamp(int volumeId, long logicalOffset, Date timestamp)
    {
        valueTimestampTable.setTimestamp(new DriveOffsetPair(volumeId, logicalOffset), timestamp);
    }

    /*
     * Fetches the drive-offset-pair for the specified values.
     */
    public DriveOffsetPair getDriveOffsetPair(int volumeId, long logicalOffset)
    {
        return volumeTable.getPhysicalLocation(volumeId, logicalOffset);
    }

    /*
     * Sets the drive-offset-pair for the specified value.
     */
    public void setDriveOffsetPair(int volumeId, long logicalOffset, DriveOffsetPair location)
    {
        volumeTable.setPhysicalLocation(volumeId, logicalOffset, location);
    }
}


