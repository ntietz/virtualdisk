package com.virtualdisk.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class RangeTest
{

    @Test
    public void test()
    {
        long begin = 10;
        long end = 20;
        Range range = new Range(begin, end);
        
        assertEquals("Beginning should match.", begin, range.getBegin());
        assertEquals("Ending should match", end, range.getEnd());
        assertTrue("Midpoint should be contained", range.contains((begin+end)/2));
        assertTrue("Range should be valid", range.valid());
        assertFalse("Endpoints should not be adjacent", range.adjacent(begin));
        assertFalse("Endpoints should not be adjacent", range.adjacent(end));
        assertTrue("Should be adjacent.", range.adjacent(begin-1));
        assertTrue("Should be adjacent.", range.adjacent(end+1));
        
        Range badRange = new Range(end, begin);
        assertFalse("Range should not be valid", badRange.valid());
        
    }

}
