package com.virtualdisk.network.util;

import nl.jqno.equalsverifier.*;

import org.junit.*;
import static org.junit.Assert.*;

public class DataNodeIdentifierTest
{
    @Test
    public void test()
    {
        int id = 10;
        String host = "localhost";
        int port = 8000;

        DataNodeIdentifier datanode = new DataNodeIdentifier(id, host, port);

        assertEquals(id, datanode.getNodeId());
        assertEquals(host, datanode.getNodeAddress());
        assertEquals(port, datanode.getPort());

        EqualsVerifier.forClass(DataNodeIdentifier.class)
                      .suppress(Warning.NONFINAL_FIELDS, Warning.NULL_FIELDS)
                      .verify();
    }
}

