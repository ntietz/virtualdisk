package com.virtualdisk.main;

import com.virtualdisk.client.*;

import java.lang.*;
import java.util.*;

public class ClientMain
{
    public static void main(String... args)
    throws Exception
    {
        String host = args[0];
        int port = Integer.valueOf(args[1]);
        String filename = args[2];

        Client client = new Client(host, port);
        Random random = new Random(2012);
        List<Integer> requestIds = new ArrayList();

        System.out.println("Connecting to the coordinator on " + host + ":" + port + "...");
        client.connect();

        requestIds.add(client.createVolume(0));
        requestIds.add(client.createVolume(1));

        while (requestIds.size() > 0)
        {
            blockOnRequest(client, requestIds.remove(0));
        }

        System.out.println("Disconnecting from the coordinator...");
        client.disconnect();

        /*
        System.out.println("Connecting to the coordinator on " + host + ":" + port + "...");

        Client client = new Client(host, port);
        client.connect();

        System.out.println("Connected.");
        System.out.println("Creating volume 0...");

        client.createVolume(0);
        client.createVolume(1);

        System.out.println("Created.");

        Thread.sleep(1000);

        // ...... issue some reads/writes........
        Thread.sleep(100);

        byte[] block = new byte[client.getBlockSize()];

        random.nextBytes(block);
        client.write(0, 0, block);

        random.nextBytes(block);
        client.write(0, 1, block);

        for (int index = 0; index < 50; ++index)
        {
            System.out.println("Writing a block to volume 0, location " + index + "...");
            random.nextBytes(block);
            client.write(0, index, block);
        }

        for (int index = 0; index < 100; ++index)
        {
            System.out.println("Reading a block from volume 0, location " + index + "...");
            client.read(0, index);
        }

        for (int index = 50; index < 100; ++index)
        {
            System.out.println("Writing a block to volume 0, location " + index + "...");
            random.nextBytes(block);
            client.write(0, index, block);
        }


        Thread.sleep(500);

        System.out.println("Deleting volume 0...");

        client.deleteVolume(0);

        Thread.sleep(100);

        System.out.println("Deleted.");
        System.out.println("Disconnecting from the coordinator...");

        client.disconnect();

        System.out.println("Disconnected. Now exiting.");
        */
    }

    public static void blockOnRequest(Client client, int requestId)
    {
        while (!client.hasFinished(requestId));
    }
}

