package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;

import java.util.*;

public class CreateVolumeHandler
extends Handler
{
    public CreateVolumeHandler(int volumeId, Coordinator coordinator)
    {
        this.volumeId = volumeId;
        this.coordinator = coordinator;
    }

    public void action()
    {
        int createId = coordinator.getServer().issueVolumeCreationRequest(volumeId);
        boolean waiting = true;
        boolean success = false;

        while (waiting)
        {
            List<CreateVolumeRequestResult> results = coordinator.getServer().getVolumeCreationRequestResults(createId);
            int completed = 0;
            int successful = 0;

            for (CreateVolumeRequestResult each : results)
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

        requestResult = new CreateVolumeRequestResult(requestId, true, success);
        coordinator.setRequestResult(requestId, (RequestResult)requestResult);
        finished = true;
    }
}

