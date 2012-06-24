package com.virtualdisk.datanode;

public class SingletonDataNode
{
    private static DataNode dataNode;
    private static SingletonDataNode singleton;

    private SingletonDataNode()
    {
        // ...
    }

    public static DataNode getDataNode()
    {
        return dataNode;
    }

}

