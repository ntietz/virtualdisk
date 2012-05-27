package edu.kent.cs.virtualdisk.coordinator;

import edu.kent.cs.virtualdisk.network.*;

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

