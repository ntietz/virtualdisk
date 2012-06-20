package com.virtualdisk.coordinator;

public class CoordinatorNode
{
    private static Coordinator coordinator;
    private static CoordinatorServer server;
    private static CoordinatorNode node;

    private CoordinatorNode()
    {
        // if coordinator is null, initialize it

        // if server is null, initialize it
    }

    public static Coordinator getCoordinator()
    {
        return coordinator;
    }

    public static CoordinatorServer getServer()
    {
        return server;
    }

    public static CoordinatorNode getNode()
    {
        if (node != null)
        {
            return node;
        }
        else
        {
            node = new CoordinatorNode();
            return node;
        }
    }
}

