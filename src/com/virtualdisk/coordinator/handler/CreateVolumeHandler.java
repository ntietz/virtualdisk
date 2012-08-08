package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;

import java.util.*;

/**
 * The CreateVolumeHandler makes CreateVolumeRequests, issues the requests to the server, handles the receival of the results, and sets the results for return to the client.
 */
public class CreateVolumeHandler
extends Handler
{
    /**
     * Standard constructor.
     * @param   volumeId    the id of the volume we want to create
     * @param   coordinator the coordinator we use for requests
     */
    public CreateVolumeHandler(int volumeId, Coordinator coordinator)
    {
        this.volumeId = volumeId;
        this.coordinator = coordinator;
    }

    /**
     * Every handler must define an action; this action issues the CreateVolumeRequest and waits to get a response.
     */
    @Override
    public void action()
    {
        int createId = coordinator.getServer().issueVolumeCreationRequest(volumeId);
        boolean waiting = true;
        boolean success = false;

        System.out.println("Starting the wait.");
        while (waiting)
        {
            List<CreateVolumeRequestResult> results = coordinator.getServer().getVolumeCreationRequestResults(createId);
            int completed = 0;
            int successful = 0;

            for (CreateVolumeRequestResult each : results)
            {
                //System.out.println("Result is: " + (each.isDone() ? "done" : "running"));
                if (each.isDone())
                {
                    ++completed;
                    if (each.wasSuccessful())
                    {
                        ++successful;
                    }
                }
            }

            //System.out.println("Completed: " + completed);
            //System.out.println("Num. results: " + results.size());
            if (completed == results.size())
            {
                waiting = false;
                success = (successful == results.size());
            }
        }
        System.out.println("Finished the wait.");

        requestResult = new CreateVolumeRequestResult(requestId, true, success);
        coordinator.setRequestResult(requestId, (RequestResult)requestResult);
    }
}

