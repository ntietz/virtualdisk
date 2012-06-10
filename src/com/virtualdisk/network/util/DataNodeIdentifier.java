package com.virtualdisk.network.util;

public class DataNodeIdentifier
{
    protected int nodeId;
    protected String nodeAddress;

    public DataNodeIdentifier(int id, String address)
    {
        nodeId = id;
        nodeAddress = address;
    }

    public int getNodeId()
    {
        return nodeId;
    }

    public String getNodeAddress()
    {
        return nodeAddress;
    }

    public boolean equals(Object pThat)
    {
        if (this == pThat)
        {
            return true;
        }
        if (!(pThat instanceof DataNodeIdentifier))
        {
            return false;
        }
        DataNodeIdentifier that = (DataNodeIdentifier)pThat;
        return (  (nodeId == that.getNodeId())
               && (nodeAddress.equals(that.getNodeAddress())));
    }

    public int hashCode()
    {
        return nodeId + nodeAddress.hashCode();
    }
}

