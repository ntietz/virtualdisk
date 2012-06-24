package com.virtualdisk.main;

import org.apache.commons.lang3.*;

public class Main
{
    public static void main(String... args)
    {
        argsOrDie(args, 1);

        if (args[0].equals("coordinator"))
        {
            CoordinatorMain.main(ArrayUtils.remove(args, 0));
        }
        else if (args[0].equals("datanode"))
        {
            DataNodeMain.main(ArrayUtils.remove(args, 0));
        }
        else if (args[0].equals("client"))
        {
            ClientMain.main(ArrayUtils.remove(args, 0));
        }
        else
        {
            System.out.println("Usage!");
            System.exit(1);
        }

    }

    public static void argsOrDie(String[] args, int num)
    {
        if (args.length < num)
        {
            System.out.println("Usage!");
            System.exit(1);
        }
    }
}
