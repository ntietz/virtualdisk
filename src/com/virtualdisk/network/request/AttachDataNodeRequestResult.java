package com.virtualdisk.network.request;

import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.Sendable.*;

public abstract class AttachDataNodeRequestResult
extends RequestResult
{
    public AttachDataNodeRequestResult(int requestId)
    {
        super(requestId, false, false); // FIXME
    }
}

