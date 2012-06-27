package com.virtualdisk.datanode;

import com.virtualdisk.util.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Manages storing timestamps for a data node.
 * Currently, java.util.Date objects are used for timestamps.
 * @author  Nicholas Tietz
 */
public class TimestampTable
{
    /**
     * A map which maps (volumeId, logicalOffset) to a timestamp.
     */
    Map<Integer, Map<Long, Date>> table;
    
    /**
     * Standard constructor, which initializes an empty table.
     */
    public TimestampTable()
    {
        table = new ConcurrentHashMap<Integer, Map<Long, Date>>();
    }
    
    /**
     * Adds a new volume to store timestamps for.
     * @param   volumeId    the id of the volume to create
     */
    public void addVolume(int volumeId)
    {
        Map<Long, Date> volumeMap = new ConcurrentHashMap<Long, Date>();
        table.put(volumeId, volumeMap);
    }
    
    /**
     * Removes a volume from the timestamp table.
     * @param   volumeId    the id of the volume to delete
     */
    public void removeVolume(int volumeId)
    {
        table.remove(volumeId);
    }
    
    /**
     * Returns the timestamp for the supplied location.
     * @param   volumeId        the volume to fetch from
     * @param   logicalOffset   the logical offset of the location
     * @return  the timestamp of the requested location
     */
    public Date getTimestamp(int volumeId, long logicalOffset)
    {
        try
        {
            return table.get(volumeId).get(logicalOffset);
        }
        catch (NullPointerException npe)
        {
            return null;
        }
    }
    
    /**
     * Sets the timestamp for a given location.
     * @param   volumeId        the volume to fetch from
     * @param   logicalOffset   the logical offset of the location
     * @param   timestamp       the timestamp to assign
     */
    public void setTimestamp(int volumeId, long logicalOffset, Date timestamp)
    
    {
        try
        {
            table.get(volumeId).put(logicalOffset, timestamp);
        }
        catch (NullPointerException npe)
        {
            // .... eat the error
        }
    }

    /**
     * Removes the timestamp for a given location.
     * @param   volumeId        the volume to fetch from
     * @param   logicalOffset   the logical offset of the location
     */
    public void removeTimestamp(int volumeId, long logicalOffset)
    {
        try
        {
            table.get(volumeId).remove(logicalOffset);
        }
        catch (NullPointerException npe)
        {
            // .... eat the error
        }
    }
}
