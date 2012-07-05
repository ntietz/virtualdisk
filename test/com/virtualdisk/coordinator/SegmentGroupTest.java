package com.virtualdisk.coordinator;

import com.virtualdisk.network.util.*;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;

public class SegmentGroupTest
{
    @Test
    public void testSegmentGroup()
    {
        List<DataNodeIdentifier> ids = new ArrayList<DataNodeIdentifier>();
        for (int index = 0; index < 13; ++index)
        {
            DataNodeIdentifier id = new DataNodeIdentifier(index, "add"+index, index);
            ids.add(id);
        }

        SegmentGroup segmentGroup = new SegmentGroup(ids);

        assertEquals("Ids should match", ids, segmentGroup.getMembers());
        
        for (DataNodeIdentifier each : ids)
        {
            assertTrue("Id should be contained", segmentGroup.isMember(each));
        }

        assertFalse("id should not be contained", segmentGroup.isMember(new DataNodeIdentifier(13, "blah", 18)));
    }
}

