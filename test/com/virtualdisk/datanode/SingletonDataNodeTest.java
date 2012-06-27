package com.virtualdisk.datanode;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

public class SingletonDataNodeTest
{
    @Test
    public void test()
    {
        assertNull("DataNode should be null.", SingletonDataNode.getDataNode());
        SingletonDataNode.setup(10, new ArrayList<String>(), new ArrayList<Long>());
    }
}

