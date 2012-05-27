package edu.kent.cs.virtualdisk.datanode;

import org.jboss.netty.channel.group.*;

public class DataNodeServer
{
    static private final ChannelGroup allChannels = new DefaultChannelGroup("DataNodeServer");
    static private DataNode datanode;

    public static void main(String... args)
    {
        /*
         * USAGE:   datanode.sh
         *          datanode.sh nodeconf.xml
         *  configuration is read in from a configuration file.
         *  this configuration file is nodeconf.xml by default.
         */
        // TODO determine usage and implement it

        // TODO connect to coordinators

        // TODO connect to all nodes
    }

}

