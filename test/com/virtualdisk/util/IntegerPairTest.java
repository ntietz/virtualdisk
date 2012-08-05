package com.virtualdisk.util;

import org.junit.*;
import static org.junit.Assert.*;

public class IntegerPairTest
{
    @Test
    public void test()
    {
        Integer first = 10;
        Integer second = 20;
        IntegerPair pair = new IntegerPair(first, second);
        assertEquals("Should match", first, pair.first());
        assertEquals("Should match", second, pair.second());
    }
}

