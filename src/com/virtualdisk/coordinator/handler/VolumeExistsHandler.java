package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;

import java.util.*;

/**
 * The VolumeExistsHandler creates, issues, and manages queries about volume existence, and their results.
 */
public class VolumeExistsHandler
extends Handler
{
    /**
     * Standard constructor.
     * @param   volumeId    the volume we want to check existnece of
     * @param   coordinator the coordinator for the request
     */
    public VolumeExistsHandler(int volumeId, Coordinator coordinator)
    {
        this.volumeId = volumeId;
        this.coordinator = coordinator;
    }

    /**
     * This action issues the VolumeExistsRequest and waits to get a response.
     */
    @Override
    public void action()
    {
        int existsId = coordinator.getServer().issueVolumeExistsRequest(volumeId);
        boolean waiting = true;
        boolean exists = false;

        while (waiting)
        {
            List<VolumeExistsRequestResult> results = coordinator.getServer().getVolumeExistsRequestResults(existsId);
            int completed = 0;
            int successful = 0;

            for (VolumeExistsRequestResult each : results)
            {
                if (each.isDone())
                {
                    ++completed;
                    if (each.wasSuccessful() && each.volumeExists())
                    {
                        ++successful;
                    }
                }
            }

            if (completed == results.size())
            {
                waiting = false;
                exists = (successful == results.size());
            }
        }

        requestResult = new VolumeExistsRequestResult(requestId, true, true, exists);
        coordinator.setRequestResult(requestId, (RequestResult)requestResult);
    }
}

