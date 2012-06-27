package com.virtualdisk.coordinator;

import com.virtualdisk.network.*;
import com.virtualdisk.network.util.DataNodeIdentifier;

import java.util.*;

/**
 * SegmentGroup is a class for managing segment groups within the Coordinator's algorithms.
 * It is little more than a list of datanode ids and some operations on the list.
 */
public class SegmentGroup
{
    /**
     * A list of members of the segment group.
     */
    private List<DataNodeIdentifier> members;

    /**
     * Standard constructor.
     * @param   members the members of the segment group
     */
    public SegmentGroup(List<DataNodeIdentifier> members)
    {
        this.members = new ArrayList<DataNodeIdentifier>(members);
    }

    /**
     * Returns a reference to the list of members of the segment group.
     * WARNING: be very careful with this method. If you modify the return value's contents, it will mess up the segment group itself.
     * @return  the list of members of this segment group
     */
    public List<DataNodeIdentifier> getMembers()
    {
        return members;
    }

    /**
     * This method simply checks whether or not a supplied datanode is a member of this segment group.
     * @param   node    the node to check membership of
     * @return  true if node is a member, false otherwise
     */
    public boolean isMember(DataNodeIdentifier node)
    {
        return members.contains(node);
    }

}

