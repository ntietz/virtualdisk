package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;

import java.util.*;

/**
 * The DeleteVolumeHandler makes DeleteVolumeRequests, issues the requests to the server, handles receiving the results, and sets the results for return to the client.
 */
public class DeleteVolumeHandler
extends Handler
{
    /**
     * Standard constructor.
     * @param   volumeId    the id of the volume we want to delete
     * @param   coordinator the coordinator we use for requests
     */
    public DeleteVolumeHandler(int volumeId, Coordinator coordinator)
    {
        this.volumeId = volumeId;
        this.coordinator = coordinator;
    }

    /**
     * Every handler must define an action; this action issues the DeleteVolumeRequest and waits to get a response.
     */
    @Override
    public void action()
    {
        int deleteId = coordinator.getServer().issueVolumeDeletionRequest(volumeId);
        boolean waiting = true;
        boolean success = false;

        while (waiting)
        {
            List<DeleteVolumeRequestResult> results = coordinator.getServer().getVolumeDeletionRequestResults(deleteId);
            int completed = 0;
            int successful = 0;

            for (DeleteVolumeRequestResult each : results)
            {
                if (each.isDone())
                {
                    ++completed;
                    if (each.wasSuccessful())
                    {
                        ++successful;
                    }
                }
            }

            if (completed == results.size())
            {
                waiting = false;
                success = (successful == results.size());
            }
        }

        requestResult = new DeleteVolumeRequestResult(requestId, true, success);
        coordinator.setRequestResult(requestId, (RequestResult)requestResult);
    }
}

