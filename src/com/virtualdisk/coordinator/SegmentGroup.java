package com.virtualdisk.coordinator;

import com.virtualdisk.network.*;
import com.virtualdisk.network.util.DataNodeIdentifier;

import java.util.*;

public class SegmentGroup
{
    protected List<DataNodeIdentifier> members;

    public SegmentGroup(List<DataNodeIdentifier> m)
    {
        members = new ArrayList<DataNodeIdentifier>(m.size());
        for (int index = 0; index < m.size(); ++index)
        {
            members.add(m.get(index));
        }
    }

    public List<DataNodeIdentifier> getMembers()
    {
        return members;
    }

    public Boolean isMember(DataNodeIdentifier d)
    {
        return members.contains(d);
    }

}

