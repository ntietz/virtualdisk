package com.virtualdisk.coordinator;

/*
 * This class manages the read/write request handlers.
 */
public class HandlerManager extends Thread
{
    private boolean keepAlive = true;
    private Coordinator coordinator;

    public HandlerManager(Coordinator c)
    {
        coordinator = c;
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
            //fetch from read queue, resume, wait for it to pause, put it on end of queue
            WriteHandler currentWriter = coordinator.writeHandlers.poll();
            if (currentWriter != null)
            {
                synchronized (currentWriter)
                {
                    currentWriter.notify();
                }

                while (!currentWriter.isPaused())
                {
                    // block until it pauses
                }

                if (!currentWriter.getFinished())
                {
                    coordinator.writeHandlers.offer(currentWriter);
                }
                else
                {
                    coordinator.requestCompletionMap.put(currentWriter.getRequestId(), true);
                    coordinator.writeResultMap.put(currentWriter.getRequestId(), currentWriter.getResult());
                }
            }

            //fetch from write queue, resume, wait for it to pause, put it on end of queue
            ReadHandler currentReader = coordinator.readHandlers.poll();
            if (currentReader != null)
            {
                synchronized (currentReader)
                {
                    currentReader.notify();
                }

                while (!currentReader.isPaused())
                {
                    // block until it pauses
                }

                if (!currentReader.getFinished())
                {
                    coordinator.readHandlers.offer(currentReader);
                }
                else
                {
                    coordinator.requestCompletionMap.put(currentReader.getRequestId(), true);
                    coordinator.readResultMap.put(currentReader.getRequestId(), currentReader.getResult());
                }
            }
        }
    }
}

