package com.virtualdisk.coordinator;

import com.virtualdisk.network.request.*;

import java.util.*;

public class VolumeExistsHandler
extends Handler
{
    public VolumeExistsHandler(int volumeId, Coordinator coordinator)
    {
        this.volumeId = volumeId;
        this.coordinator = coordinator;
    }

    public void action()
    {
        int existsId = coordinator.server.issueVolumeExistsRequest(volumeId);
        boolean waiting = true;
        boolean exists = false;

        while (waiting)
        {
            List<VolumeExistsRequestResult> results = coordinator.server.getVolumeExistsRequestResults(existsId);
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

        coordinator.volumeRequestMap.put(requestId, exists);
        coordinator.requestCompletionMap.put(requestId, true);
        finished = true;
    }
}

