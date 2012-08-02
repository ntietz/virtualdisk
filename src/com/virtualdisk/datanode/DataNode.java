package com.virtualdisk.datanode;

import com.virtualdisk.datanode.Drive;
import com.virtualdisk.util.DriveOffsetPair;
import java.util.List;
import java.util.Date;
import java.util.Collection;
import java.util.ArrayList;

/**
 * The DataNode class manages all the storage and timestamps on for the data nodes in virtualdisk.
 * All the network requests received are handled separately, then the interpreted requests are performed using this class.
 * @author  Nicholas Tietz
 */
public class DataNode
{

    /**
     * The volume table translates (volumeId, logicalOffset) pairs into phsyical locations, and manages all of the volume information.
     */
    private VolumeTable volumeTable;

    /**
     * The free space table contains all the free space on the disks, and allows easy allocation of space.
     */
    private FreeSpaceTable freeSpaceTable;

    /**
     * The timestamp tables store timestamps for each (volumeId, logicalOffset) pair.
     * The timestamps are Dates, so they are millisecond precision.
     */
    private TimestampTable orderTimestampTable;
    private TimestampTable valueTimestampTable;

    /**
     * A list of all the drives the node can use.
     */
    private List<Drive> drives;

    /**
     * Sets up a DataNode with the provided characteristics.
     * @param   blockSize       the size of a block, in bytes
     * @param   driveHandles    the handles for the drives for this datanode
     * @param   driveSizes      the sizes of each drive, in blocks
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

    /**
     * Creates a new volume.
     * @param   volumeId    the id of the volume we wish to create
     */
    public void createVolume(int volumeId)
    {
        volumeTable.addVolume(volumeId);
        orderTimestampTable.addVolume(volumeId);
        valueTimestampTable.addVolume(volumeId);
    }
    
    /**
     * Deteremines whether or not a given volume exists on this datanode.
     * @param   volumeId    the id of the volume we wish to check
     */
    public boolean volumeExists(int volumeId)
    {
        return volumeTable.exists(volumeId);
    }
    
    /**
     * Deletes the given volume.
     * @param   volumeId    the id of the volume we want to delete
     */
    public void deleteVolume(int volumeId)
    {
        Collection<DriveOffsetPair> allocatedLocations = volumeTable.getAllPhysicalLocations(volumeId);

        for (DriveOffsetPair location : allocatedLocations)
        {
            freeSpaceTable.release(location);
        }

        volumeTable.removeVolume(volumeId);
        orderTimestampTable.removeVolume(volumeId);
        valueTimestampTable.removeVolume(volumeId);
    }

    /**
     * Performs an order request on the data node.
     * To be successful, the supplied timestamp must be after the existing order and value timestamps, or there must be no existing timestamp.
     * @param   volumeId        the id of the volume we are ordering on
     * @param   logicalOffset   the logical offset we wish to order
     * @param   timestamp       the timestamp of the order request
     * @return  true of the ordering works, false otherwise
     */
    public boolean order(int volumeId, long logicalOffset, Date timestamp)
    {
        Date currentOrderTimestamp = getOrderTimestamp(volumeId, logicalOffset);
        Date currentValueTimestamp = getValueTimestamp(volumeId, logicalOffset);

        // TODO: fix this logic so that negative timestamps to not break the system. fine for now...
        if (currentOrderTimestamp == null)
        {
            currentOrderTimestamp = new Date(-1);
        }
        if (currentValueTimestamp == null)
        {
            currentValueTimestamp = new Date(-1);
        }
 
        if (!currentOrderTimestamp.before(timestamp) || !currentValueTimestamp.before(timestamp))
        {
            return false;
        }
        else
        {
            setOrderTimestamp(volumeId, logicalOffset, timestamp);

            return true;
        }
    }

    /**
     * This function attempts to perform a write operation: if the timestamp is correct, it will write the block to the disk; otherwise, no data will be written.
     * @param   volumeId        the volume we want to write to
     * @param   logicalOffset   the logical offset we want to write to
     * @param   block           the data we are trying to write
     * @param   timestamp       the timestamp from the request:
     * @return  true if the write succeeds, false otherwise
     */
    public boolean write(int volumeId, long logicalOffset, byte[] block, Date timestamp)
    {
        Date orderTimestamp = null;
        Date valueTimestamp = null;
        
        try
        {
            orderTimestamp = getOrderTimestamp(volumeId, logicalOffset);
            valueTimestamp = getValueTimestamp(volumeId, logicalOffset);
        }
        catch (NullPointerException npe)
        {
            return false;
        }
        
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
            freeSpaceTable.claim(location);

            int driveId = location.getDriveNumber();
            long physicalOffset = location.getOffset();
            return drives.get(driveId).write(physicalOffset, block);
        }
        else
        {
            return false;
        }
    }

    /**
     * This function attempts to perform a read operation and returns the result.
     * If the block is empty, null is returned.
     * @param   volumeId        the volume id of the read request
     * @param   logicalOffset   the logical offset for the read request
     * @return  null if nothing has been written, and the data from the requested location otherwise
     */
    public byte[] read(int volumeId, long logicalOffset)
    {
        DriveOffsetPair physicalOffset = null;
        
        try
        {
            physicalOffset = getDriveOffsetPair(volumeId, logicalOffset);
        }
        catch (NullPointerException npe)
        {
            return null;
        }

        if (physicalOffset == null)
        {
            return null;
        }
        else
        {
            return drives.get(physicalOffset.getDriveNumber()).read(physicalOffset.getOffset());
        }
    }

    /**
     * Fetches the ordering timestamp for a given volume ID and logical offset.
     * @param   volumeId        the volume we want the timestamp for
     * @param   logicalOffset   the logical offset we want the timestamp for
     * @return  the timsetamp for the requested location, or null if it has not been set
     */
    public Date getOrderTimestamp(int volumeId, long logicalOffset)
    {
        return orderTimestampTable.getTimestamp(volumeId, logicalOffset);
    }

    /**
     * Fetches the value timestamp for a given volume ID and the logical offset.
     * @param   volumeId        the volume we want the timestamp for
     * @param   logicalOffset   the logical offset we want the timestamp for
     * @return  the timestamp for the requested location, or null if it has not been set
     */
    public Date getValueTimestamp(int volumeId, long logicalOffset)
    {
        return valueTimestampTable.getTimestamp(volumeId, logicalOffset);
    }

    /**
     * Sets the ordering timestamp for a given volume ID and logical offset.
     * @param   volumeId        the volume we want to set the timestamp for
     * @param   logicalOffset   the logical offset we want to set the timestamp for
     * @param   timestamp       the timestamp we wish to set
     */
    public void setOrderTimestamp(int volumeId, long logicalOffset, Date timestamp)
    {
        orderTimestampTable.setTimestamp(volumeId, logicalOffset, timestamp);
    }

    /**
     * Sets the value timestamp for a given volume ID and the logical offset.
     * @param   volumeId        the volume we want to set the timestamp for
     * @param   logicalOffset   the logical offset we want to set the timestamp for
     * @param   timestamp       the timestamp we wish to set
     */
    public void setValueTimestamp(int volumeId, long logicalOffset, Date timestamp)
    {
        valueTimestampTable.setTimestamp(volumeId, logicalOffset, timestamp);
    }

    /**
     * Fetches the drive-offset-pair for the specified values.
     * @param   volumeId        the volume we want the physical drive for
     * @param   logicalOffset   the logical offset we want the physical offset for
     * @return  the (drive, physicalOffset) pair for the request location
     */
    public DriveOffsetPair getDriveOffsetPair(int volumeId, long logicalOffset)
    {
        return volumeTable.getPhysicalLocation(volumeId, logicalOffset);
    }

    /**
     * Sets the drive-offset-pair for the specified value.
     * @param   volumeId        the volume we want to set the drive of
     * @param   logicalOffset   the logical offset we want to set the physical offset of
     * @param   location        the (drive, physicalOffset) pair we wish to assign to the location
     */
    public void setDriveOffsetPair(int volumeId, long logicalOffset, DriveOffsetPair location)
    {
        volumeTable.setPhysicalLocation(volumeId, logicalOffset, location);
    }

    public void unset(int volumeId, long logicalOffset)
    {
        orderTimestampTable.removeTimestamp(volumeId, logicalOffset);
        valueTimestampTable.removeTimestamp(volumeId, logicalOffset);

        DriveOffsetPair physicalPair = volumeTable.getPhysicalLocation(volumeId, logicalOffset);
        freeSpaceTable.release(physicalPair);
        volumeTable.unsetPhysicalLocation(volumeId, logicalOffset);
    }

    /**
     * Returns the total free space for this data node.
     * @return  the total free space for this node
     */
    public long totalFreeSpace()
    {
        return freeSpaceTable.totalFreeSpace();
    }
}


