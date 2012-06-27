package com.virtualdisk.util;

/**
 * Range is a conveninence class for defining where a range of locations begins and ends.
 * Each range includes [begin, end], inclusive.
 * 
 * @author  Nicholas Tietz
 */
public class Range
{
    /**
     * The beginning of the range.
     */
    protected long begin;

    /**
     * The ending of the range.
     */
    protected long end;

    /**
     * @param   begin   the beginning of the range
     * @param   end     the ending of the range
     */
    public Range(long begin, long end)
    {
        this.begin = begin;
        this.end = end;
    }

    /**
     * Returns the beginning of a range.
     * @return  the beginning of the range
     */
    public long getBegin()
    {
        return begin;
    }

    /**
     * Returns the ending of a range.
     * @return  the ending of the range
     */
    public long getEnd()
    {
        return end;
    }
    
    /**
     * Checks whether the location is contained in the inclusive interval [begin, end].
     * @param   location    the location we wish to check inclusion of
     * @return  true the location is within [begin, end], false otherwise
     */
    public boolean contains(long location)
    {
        return begin <= location && location <= end;
    }
    
    /**
     * Checks whether or not this is a validly constructed range.
     * @return  true if the values of the range are valid, false if they are logically inconsistent
     */
    public boolean valid()
    {
        return begin <= end;
    }

    /**
     * Checks whether a given location is immediately outside the range.
     * @param   location    the location we wish to check adjacency of
     * @return  true if the location is in [begin-1, end+1] but not in [begin, end], false otherwise
     */
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

