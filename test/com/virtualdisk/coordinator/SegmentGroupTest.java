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
        long startingBlock = 19;
        long stoppingBlock = 23;
        int volumeId = 2;

        List<DataNodeIdentifier> ids = new ArrayList<DataNodeIdentifier>();
        for (int index = 0; index < 13; ++index)
        {
            DataNodeIdentifier id = new DataNodeIdentifier(index, "add"+index, index);
            ids.add(id);
        }

        SegmentGroup segmentGroup = new SegmentGroup(ids, volumeId, startingBlock, stoppingBlock);

        assertEquals("Ids should match", ids, segmentGroup.getMembers());
        
        for (DataNodeIdentifier each : ids)
        {
            assertTrue("Id should be contained", segmentGroup.isMember(each));
        }

        DataNodeIdentifier otherNode = new DataNodeIdentifier(13, "blah", 18);

        assertFalse("id should not be contained", segmentGroup.isMember(otherNode));

        assertEquals("Volume id should match", volumeId, segmentGroup.getVolumeId());
        assertEquals("Offsets should match", startingBlock, segmentGroup.getStartingBlock());
        assertEquals("Offsets should match", stoppingBlock, segmentGroup.getStoppingBlock());

        segmentGroup.replace(ids.get(0), otherNode);
        
        assertTrue("id should be contained", segmentGroup.isMember(otherNode));

        for (long offset = 0; offset < startingBlock; ++offset)
        {
            assertFalse("should not be contained", segmentGroup.contains(offset));
        }
        for (long offset = startingBlock; offset <= stoppingBlock; ++offset)
        {
            assertTrue("should be contained", segmentGroup.contains(offset));
        }
        for (long offset = stoppingBlock+1; offset < 2*stoppingBlock; ++offset)
        {
            assertFalse("should not be contained", segmentGroup.contains(offset));
        }

    }
}

