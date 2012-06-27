package com.virtualdisk.datanode;

import java.util.*;

/**
 * A singleton object which keeps a DataNode around. This is needed so that the network code can access the state of the data node.
 */
public class SingletonDataNode
{
    /**
     * The data node which this singleton stores.
     */
    private static DataNode dataNode;

    /**
     * A reference to itself.
     */
    private static SingletonDataNode singleton;

    /**
     * Standard constructor which initializes the DataNode with the supplied information.
     * @param   blockSize       the size of blocks for the datanode, in bytes
     * @param   driveHandles    a list of the handles for the drives for the node
     * @param   driveSizes      a list of the sizes of the drives for the node
     */
    private SingletonDataNode( int blockSize
                             , List<String> driveHandles
                             , List<Long> driveSizes
                             )
    {
        dataNode = new DataNode(blockSize, driveHandles, driveSizes);
    }

    /**
     * Retrieves the data node which this singleton keeps.
     * Must have setup(...) run before the DataNode will be created.
     * @return  the datanode which this singleton stores
     */
    public static DataNode getDataNode()
    {
        return dataNode;
    }

    /**
     * Constructs the DataNode and configures the singleton. Safe to call many times.
     * @param   blockSize       the size of blocks for the datanode, in bytes
     * @param   driveHandles    a list of the handles for the drives for the node
     * @param   driveSizes      a list of the sizes of the drives for the node
     */
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

