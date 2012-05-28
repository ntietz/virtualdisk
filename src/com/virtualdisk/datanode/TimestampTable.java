package com.virtualdisk.datanode;

import com.virtualdisk.util.DriveOffsetPair;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimestampTable
{
    Map<Integer, Map<Long, Date>> table;
    
    public TimestampTable()
    {
        table = new HashMap<Integer, Map<Long, Date>>();
    }
    
    /**
     * @param volumeId  the id of the volume to create
     */
    public void addVolume(int volumeId)
    {
        Map<Long, Date> volumeMap = new HashMap<Long, Date>();
        table.put(volumeId, volumeMap);
    }
    
    /**
     * @param volumeId  the id of the volume to delete
     */
    public void removeVolume(int volumeId)
    {
        table.remove(volumeId);
    }
    
    /**
     * @param location  the disk location to fetch the timestamp for
     * @return  the timestamp of the requested location
     */
    public Date getTimestamp(DriveOffsetPair location)
    {
        return table.get(location.getDriveNumber()).get(location.getOffset());
    }
    
    /**
     * @param location  the disk location to set the timestamp for
     * @param timestamp the timestamp to assign
     */
    public void setTimestamp(DriveOffsetPair location, Date timestamp)
    
    {
        table.get(location.getDriveNumber()).put(location.getOffset(), timestamp);
    }

    /**
     * @param location  the disk location to remove the timestamp for
     */
    public void removeTimestamp(DriveOffsetPair location)
    {
        table.get(location.getDriveNumber()).remove(location.getOffset());
    }
}
