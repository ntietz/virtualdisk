package com.virtualdisk.network.request;

import com.virtualdisk.network.Sendable;

public abstract class RequestResult
extends Sendable
{
    protected boolean completed = false;    // describes whether the request itself had errors
    protected boolean successful = false;      // describes whether the order was successful

    public boolean completed()
    {
        return completed;
    }
    
    public boolean successful()
    {
        return successful;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (completed ? 1231 : 1237);
        result = prime * result + (successful ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof RequestResult))
        {
            return false;
        }
        RequestResult other = (RequestResult) obj;
        if (completed != other.completed)
        {
            return false;
        }
        if (successful != other.successful)
        {
            return false;
        }
        return true;
    }
}

