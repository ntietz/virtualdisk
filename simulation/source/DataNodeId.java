package simulation;
 
public class DataNodeId
implements Comparable
{
    private int numericId;
    private int segmentGroupMemberships;

    public DataNodeId(int numericId)
    {
        this.numericId = numericId;
    }

    public int getNumericId()
    {
        return numericId;
    }

    public void updateSegmentGroupMemberships(int amount)
    {
        segmentGroupMemberships += amount;
    }

    public int getSegmentGroupMemberships()
    {
        return segmentGroupMemberships;
    }

    public int compareTo(Object obj)
    {
        DataNodeId other = (DataNodeId) obj;
        return (getSegmentGroupMemberships() - other.getSegmentGroupMemberships());
    }
}

