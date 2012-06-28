package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;

import java.util.*;

public class DeleteVolumeHandler
extends Handler
{
    public DeleteVolumeHandler(int volumeId, Coordinator coordinator)
    {
        this.volumeId = volumeId;
        this.coordinator = coordinator;
    }

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
        finished = true;
    }
}

