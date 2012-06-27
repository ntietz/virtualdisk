package com.virtualdisk.datanode;

import com.virtualdisk.util.*;

import java.util.*;

/**
 * @author nicholas
 *
 */
public class VolumeTable
{
    Map<Integer, Map<Long, DriveOffsetPair>> table;
    
    /**
     * Default empty constructor.
     */
    public VolumeTable()
    {
        table = new HashMap<Integer, Map<Long, DriveOffsetPair>>();
    }
    
    /**
     * @param volumeId  the volume id we wish to add a diskmap for
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
     * @param volumeId  the volume id we wish to remove the diskmap for
     */
    public void removeVolume(int volumeId)
    {
        table.remove(volumeId);
    }
    
    /**
     * @param volumeId      the volume id for the location we're trying to find
     * @param logicalOffset the logical offset for the location we're trying to find
     * @return  the DriveOffsetPair describing the physical location of a block
     */
    public DriveOffsetPair getPhysicalLocation(int volumeId, long logicalOffset)
    {
        return table.get(volumeId).get(logicalOffset);
    }
    
    /**
     * @param volumeId  the volume id we want to get all locations of
     * @return          a collection of the DriveOffsetPairs which are allocated for the volume
     */
    public Collection<DriveOffsetPair> getAllPhysicalLocations(int volumeId)
    {
        return table.get(volumeId).values();
    }
    
    /**
     * @param volumeId      the volume id for the location we're setting
     * @param logicalOffset the logical offset for the location we're setting
     * @param location      the physical location we're assigning
     */
    public void setPhysicalLocation(int volumeId, long logicalOffset, DriveOffsetPair location)
    {
        table.get(volumeId).put(logicalOffset, location);
    }
    
    /**
     * @param volumeId      the volume id of the location we want to unset
     * @param logicalOffset the logical offset for the location we're unsetting
     */
    public void unsetPhysicalLocation(int volumeId, long logicalOffset)
    {
        table.get(volumeId).remove(logicalOffset);
    }
    
    public boolean exists(int volumeId)
    {
        return table.containsKey(volumeId);
    }
}
