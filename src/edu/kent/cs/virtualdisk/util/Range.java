package edu.kent.cs.virtualdisk.util;

public class Range
{

    protected Integer begin;
    protected Integer end;

    public Range(Integer b, Integer e)
    {
        begin = b;
        end = e;
    }

    public Integer getBegin()
    {
        return begin;
    }

    public Integer getEnd()
    {
        return end;
    }

}

