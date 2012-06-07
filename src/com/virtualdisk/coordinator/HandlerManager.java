package com.virtualdisk.coordinator;

/*
 * This class manages the read/write request handlers.
 */
public class HandlerManager extends Thread
{
    private boolean keepAlive = true;
    //private Coordinator coordinator;

    public HandlerManager(Coordinator c)
    {
        //coordinator = c;
    }

    /*
     * This method allows you to signal to the manager that it should shut down, to terminate the thread.
     */
    public void shutdown()
    {
        keepAlive = false;
    }

    /*
     * This method iterates through the handler queues and starts each handler, letting it run until it blocks on IO.
     */
    public void run()
    {
        
        while (keepAlive)
        {

        }
    }
}

