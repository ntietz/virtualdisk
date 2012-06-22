package com.virtualdisk.coordinator;

public class SingletonCoordinator
{
    private static Coordinator coordinator;
    private static CoordinatorServer server;
    private static SingletonCoordinator singleton;

    private SingletonCoordinator()
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

    public static void setup()
    {
        if (singleton == null)
        {
            // TODO make this use the correct constructor
            singleton = new SingletonCoordinator();
        }
    }
}

