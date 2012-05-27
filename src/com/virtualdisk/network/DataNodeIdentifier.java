package com.virtualdisk.network;

public class DataNodeIdentifier
{
    protected Integer nodeId;
    protected String nodeAddress;

    public DataNodeIdentifier(Integer id, String address)
    {
        nodeId = id;
        nodeAddress = address;
    }

    public Integer getNodeId()
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
        return (  (nodeId.equals(that.getNodeId()))
               && (nodeAddress.equals(that.getNodeAddress())));
    }

    public int hashCode()
    {
        return (nodeId.hashCode()/2 + nodeAddress.hashCode()/2);
    }
}

