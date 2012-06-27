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
        assertNotNull("DataNode should not be null.", SingletonDataNode.getDataNode());

        DataNode node1 = SingletonDataNode.getDataNode();
        DataNode node2 = SingletonDataNode.getDataNode();

        assertTrue("Nodes should point to the same place", node1 == node2);
    }
}

