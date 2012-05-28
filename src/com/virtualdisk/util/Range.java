package com.virtualdisk.util;

public class Range
{

    protected long begin;
    protected long end;

    public Range(long b, long e)
    {
        begin = b;
        end = e;
    }

    public long getBegin()
    {
        return begin;
    }

    public long getEnd()
    {
        return end;
    }
    
    public boolean contains(long location)
    {
        return begin <= location && location <= end;
    }
    
    public boolean valid()
    {
        return begin <= end;
    }

    public boolean adjacent(long location)
    {
        if (location+1 == begin || end+1 == location)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}

