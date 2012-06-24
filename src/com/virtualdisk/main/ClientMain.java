package com.virtualdisk.main;

import com.virtualdisk.client.*;

public class ClientMain
{
    public static void main(String... args)
    {
        String host = args[0];
        int port = Integer.valueOf(args[1]);

        Client client = new Client(host, port);
        client.connect();

        client.createVolume(0);

        // ...... issue some reads/writes........


        client.deleteVolume(0);

        client.disconnect();
    }
}

