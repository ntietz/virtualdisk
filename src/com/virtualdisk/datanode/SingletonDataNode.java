package com.virtualdisk.datanode;

import java.util.*;

public class SingletonDataNode
{
    private static DataNode dataNode;
    private static SingletonDataNode singleton;

    private SingletonDataNode( int blockSize
                             , List<String> driveHandles
                             , List<Long> driveSizes
                             )
    {
        dataNode = new DataNode(blockSize, driveHandles, driveSizes);
    }

    public static DataNode getDataNode()
    {
        return dataNode;
    }

    public static void setup( int blockSize
                            , List<String> driveHandles
                            , List<Long> driveSizes
                            )
    {
        if (singleton == null)
        {
            singleton = new SingletonDataNode( blockSize
                                             , driveHandles
                                             , driveSizes
                                             );
        }
    }

}

