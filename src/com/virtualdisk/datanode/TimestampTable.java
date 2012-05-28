package com.virtualdisk.datanode;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimestampTable
{

    int numberOfDrives;
    Map<Integer, Map<Long, Date>> table;
    
    public TimestampTable(int n)
    {
        numberOfDrives = n;
        table = new HashMap<Integer, Map<Long, Date>>();
        
        for (int drive = 0; drive < numberOfDrives; ++drive)
        {
            Map<Long, Date> driveMap = new HashMap<Long, Date>();
            table.put(drive, driveMap);
        }
    }
    
    
}
