package com.virtualdisk.coordinator;

import com.virtualdisk.network.request.*;

import java.util.*;

public class DeleteVolumeHandler
extends Handler
{
    public DeleteVolumeHandler(int volumeId, Coordinator coordinator)
    {
        this.volumeId = volumeId;
        this.coordinator = coordinator;
    }

    public void run()
    {
        delete();
    }

    public void delete()
    {
        int deleteId = coordinator.server.issueVolumeDeletionRequest(volumeId);
        boolean waiting = true;
        boolean success = false;

        while (waiting)
        {
            List<DeleteVolumeRequestResult> results = coordinator.server.getVolumeDeletionRequestResults(deleteId);
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

        coordinator.volumeRequestMap.put(requestId, success);
        coordinator.requestCompletionMap.put(requestId, true);
        finished = true;
    }
}

