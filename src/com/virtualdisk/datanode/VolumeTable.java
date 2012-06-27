package com.virtualdisk.datanode;

import com.virtualdisk.util.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Maps (volume, logicalOffset) pairs onto (phsyicalDrive, phsyicalOffset) pairs. This is the primary class used for address translation on data nodes.
 * @author  Nicholas Tietz
 */
public class VolumeTable
{
    /**
     * This map maps (volume, logicalOffset) pairs onto their phsyical locations.
     */
    private Map<Integer, Map<Long, DriveOffsetPair>> table;
    
    /**
     * Default empty constructor.
     */
    public VolumeTable()
    {
        table = new ConcurrentHashMap<Integer, Map<Long, DriveOffsetPair>>();
    }
    
    /**
     * Adds a volume into the volume map.
     * @param   volumeId    the volume id we wish to add a diskmap for
     */
    public void addVolume(int volumeId)
    {
        if (table.get(volumeId) == null)
        {
            Map<Long, DriveOffsetPair> emptyDiskMap = new HashMap<Long, DriveOffsetPair>();
            table.put(volumeId, emptyDiskMap);
        }
    }
    
    /**
     * Removes a volume from the volume map.
     * @param   volumeId    the volume id we wish to remove the diskmap for
     */
    public void removeVolume(int volumeId)
    {
        table.remove(volumeId);
    }
    
    /**
     * Gets the physical location for a (volume, logicalOffset) pair.
     * @param   volumeId        the volume id for the location we're trying to find
     * @param   logicalOffset   the logical offset for the location we're trying to find
     * @return  the DriveOffsetPair describing the physical location of a block
     */
    public DriveOffsetPair getPhysicalLocation(int volumeId, long logicalOffset)
    {
        return table.get(volumeId).get(logicalOffset);
    }
    
    /**
     * Returns all phsyical locations for a given volume.
     * The primary use for this method is to know which locations we must free when we delete a volume.
     * @param   volumeId    the volume id we want to get all locations of
     * @return  a collection of the DriveOffsetPairs which are allocated for the volume
     */
    public Collection<DriveOffsetPair> getAllPhysicalLocations(int volumeId)
    {
        return table.get(volumeId).values();
    }
    
    /**
     * Assigns a supplied physical location to the given (volumeId, logicalOffset) pair.
     * @param   volumeId        the volume id for the location we're setting
     * @param   logicalOffset   the logical offset for the location we're setting
     * @param   location        the physical location we're assigning
     */
    public void setPhysicalLocation(int volumeId, long logicalOffset, DriveOffsetPair location)
    {
        table.get(volumeId).put(logicalOffset, location);
    }
    
    /**
     * Unasigns the given physical location from the (volumeId, logicalOffset) pair.
     * @param   volumeId        the volume id of the location we want to unset
     * @param   logicalOffset   the logical offset for the location we're unsetting
     */
    public void unsetPhysicalLocation(int volumeId, long logicalOffset)
    {
        table.get(volumeId).remove(logicalOffset);
    }
    
    /**
     * Checks whether a given volume exists within the volume table.
     * @param   volumeId    the volume we wish to check the existence of
     * @return  true of the volume exists, false otherwise
     */
    public boolean exists(int volumeId)
    {
        return table.containsKey(volumeId);
    }
}
