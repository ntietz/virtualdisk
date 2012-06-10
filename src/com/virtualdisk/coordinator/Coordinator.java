package com.virtualdisk.coordinator;

import com.virtualdisk.network.*;
import com.virtualdisk.network.util.DataNodeIdentifier;
import com.virtualdisk.network.util.DataNodeStatus;
import com.virtualdisk.network.util.DataNodeStatusPair;

import java.util.*;
import java.util.concurrent.*;

public class Coordinator
{
    protected int blockSize;
    protected int segmentSize;
    protected int segmentGroupSize;
    protected int quorumSize;

    protected List<DataNodeIdentifier> datanodes;

    protected PriorityBlockingQueue<DataNodeStatusPair> datanodeStatuses;
    protected List<SegmentGroup> segmentGroupList;
    protected Map<Integer,Map<Long,SegmentGroup>> volumeTable;
    protected Date lastTimestamp;

    protected Map<Integer, Boolean> requestCompletionMap;
    protected Map<Integer, Boolean> writeResultMap;
    protected Map<Integer, byte[]> readResultMap;

    protected int lastAssignedId = 0;

    protected HandlerManager handlerManager;

    protected NetworkServer server;

    /*
     * This constructor initializes all the necessary information for the coordinator.
     */
    public Coordinator( int bs
                      , int ss
                      , int sgs
                      , int qs
                      , List<DataNodeIdentifier> initialNodes
                      , NetworkServer s
                      )
    {
        blockSize = bs;
        segmentSize = ss;
        segmentGroupSize = sgs;
        quorumSize = qs;

        server = s;

        datanodes = Collections.synchronizedList(new ArrayList<DataNodeIdentifier>(initialNodes));
        datanodeStatuses = new PriorityBlockingQueue<DataNodeStatusPair>();
        for (DataNodeIdentifier each : datanodes)
        {
            DataNodeStatus status = new DataNodeStatus(blockSize, segmentSize);
            DataNodeStatusPair pair = new DataNodeStatusPair(each, status);
            datanodeStatuses.add(pair);
        }

        segmentGroupList = Collections.synchronizedList(new ArrayList<SegmentGroup>());

        volumeTable = new ConcurrentHashMap<Integer,Map<Long,SegmentGroup>>();

        lastTimestamp = new Date(0);

        requestCompletionMap = new ConcurrentHashMap<Integer, Boolean>();
        writeResultMap = new ConcurrentHashMap<Integer, Boolean>();
        readResultMap = new ConcurrentHashMap<Integer, byte[]>();

        handlerManager = new HandlerManager(this);
        handlerManager.start();
    }

    /*
     * This method creates a new logical volume within the coordinator.
     */
    public boolean createVolume(int volumeId)
    {
        if (volumeTable.get(volumeId) != null)
        {
            return false;
        }
        else
        {
            Map<Long,SegmentGroup> volumeMap = new ConcurrentHashMap<Long,SegmentGroup>();
            volumeTable.put(volumeId, volumeMap);

            server.issueVolumeCreationRequest(volumeId);

            return true;
        }
    }

    /*
     * This method deletes a logical volume within the coordinator.
     */
    public boolean deleteVolume(int volumeId)
    {
        if (volumeTable.get(volumeId) != null)
        {
            volumeTable.remove(volumeId);

            server.issueVolumeDeletionRequest(volumeId);

            return true;
        }
        else
        {
            return false;
        }
    }

    /*
     * A synchronized method to generate new unique request IDs.
     */
    protected synchronized int generateNewRequestId()
    {
        ++lastAssignedId;
        return lastAssignedId;
    }
    
    /*
     * Initiates the write request and returns the request's ID.
     */
    public int write(int volumeId, long logicalOffset, byte[] block)
    {
        int id = generateNewRequestId();

        WriteHandler handler = new WriteHandler(volumeId, logicalOffset, block, this);
        handler.setRequestId(id);
        handler.start();

        return id;
    }

    /*
     * Returns the status of the write request (finished or not-finished).
     */
    public boolean writeCompleted(Integer requestId)
    {
        Boolean finished = requestCompletionMap.get(requestId);
        if (finished != null)
        {
            return finished;
        }
        else
        {
            return false;
        }
    }

    /*
     * This method fetches the result of the write request (success or failure).
     */
    public boolean writeResult(int requestId)
    {
        return writeResultMap.get(requestId);
    }

    /*
     * This method initiates a read request for the given volume ID and logical offset and returns its request ID.
     */
    public int read(int volumeId, long logicalOffset)
    {
        int id = generateNewRequestId();

        ReadHandler handler = new ReadHandler(volumeId, logicalOffset, this);
        handler.setRequestId(id);
        handler.start();

        return id;
    }

    /*
     * This method returns whether or not the specified read request has completed.
     */
    public boolean readCompleted(Integer requestId)
    {
        Boolean finished = requestCompletionMap.get(requestId);
        if (finished != null)
        {
            return finished;
        }
        else
        {
            return false;
        }
    }

    /*
     * This method fetches the results of a read request; If the request is in progress, it returns null.
     */
    public byte[] readResult(Integer requestId)
    {
        return readResultMap.get(requestId);
    }

    // TODO: IMPLEMENT LATER
    public boolean addDataNode(DataNodeIdentifier node)
    {
        return false;
    }

    // TODO: IMPLEMENT LATER
    public boolean removeDataNode(DataNodeIdentifier node)
    {
        return false;
    }

    /*
     * This method returns the segment group for a volumeId and logical offset.
     * If the volumeId and logical offset pair do not have a segment group, it will be assigned.
     */
    protected SegmentGroup getSegmentGroup(int volumeId, long logicalOffset)
    {
        SegmentGroup segmentgroup = volumeTable.get(volumeId).get(logicalOffset);

        if (segmentgroup == null)
        {
            segmentgroup = assignSegmentGroup(volumeId, logicalOffset);
        }

        return segmentgroup;
    }

    /*
     * This method takes a volumeId and logical offset and assigns that pair a segment group.
     * It generates the segment group based off which nodes have the lightest load.
     */
    protected synchronized SegmentGroup assignSegmentGroup(int volumeId, long logicalOffset)
    {
        SegmentGroup segmentgroup = volumeTable.get(volumeId).get(logicalOffset);

        if (segmentgroup != null)
        {
            return segmentgroup;
        }
        else
        {
            List<DataNodeStatusPair> segmentGroupMemberPairs = Collections.synchronizedList(new ArrayList<DataNodeStatusPair>(segmentGroupSize));
            List<DataNodeIdentifier> segmentGroupMembers = Collections.synchronizedList(new ArrayList<DataNodeIdentifier>(segmentGroupSize));

            for (int index = 0; index < segmentGroupSize; ++index)
            {
                DataNodeStatusPair current = datanodeStatuses.poll();
                DataNodeStatus status = current.getStatus();
                status.addStoredSegments(1);

                segmentGroupMemberPairs.add(current);
                segmentGroupMembers.add(current.getIdentifier());
            }

            segmentgroup = new SegmentGroup(segmentGroupMembers);

            for (int index = 0; index < segmentGroupSize; ++index)
            {
                datanodeStatuses.add(segmentGroupMemberPairs.get(index));
            }

            volumeTable.get(volumeId).put(logicalOffset, segmentgroup);

            return segmentgroup;
        }
    }

    /*
     * This method generates a unique timestamp for use in network requests.
     */
    protected synchronized Date getNewTimestamp()
    {
        long current = lastTimestamp.getTime();
        lastTimestamp = new Date(current+1);
        return lastTimestamp;
    }

}

