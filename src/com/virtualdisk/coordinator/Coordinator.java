package com.virtualdisk.coordinator;

import com.virtualdisk.coordinator.handler.*;
import com.virtualdisk.network.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.*;

import java.util.*;
import java.util.concurrent.*;

public class Coordinator
{
    /**
     * The block size (in bytes) for the system.
     */
    private int blockSize;

    /**
     * The segment size (in blocks) for the system.
     */
    private int segmentSize;

    /**
     * The segment group size (in datanodes) for the system.
     */
    private int segmentGroupSize;

    /**
     * The quorum size for the system; a quorum is the minimum number of nodes you need to reach a consensus.
     */
    private int quorumSize;

    /**
     * All the identifiers for the datanodes which are connected to this coordinator.
     * This is safe to use concurrently, as it is made synchronized within the constructor.
     */
    private List<DataNodeIdentifier> datanodes;

    /**
     * A priority queue which puts the least loaded nodes first.
     * This is safe to use concurrently.
     */
    private PriorityBlockingQueue<DataNodeStatusPair> datanodeStatuses;

    /**
     * A list of all the segment groups this coordinator has.
     * This will likely be converted to a PriorityBlockingQueue in the future so that assignments
     * go first to the least loaded groups.
     */
    private List<SegmentGroup> segmentGroupList;

    /**
     * The coordinator's volume table maps (volumeId, logicalOffset) onto a segment group.
     * This will likely be converted to a custom class in the future; probably a segment group manager
     * along with an address resolver.
     */
    private Map<Integer,Map<Long,SegmentGroup>> volumeTable;

    /**
     * The completion map maps request ids onto booleans; true means the request finished, false means it did not.
     * Note: "finished" includes timing out and having errors.
     * Note: having a true completion implies that there will be a non-null result in the result map.
     * This member can be used concurrently safely, as it is made synchronized in the constructor.
     */
    private Map<Integer, Boolean> requestCompletionMap;

    /**
     * The result map maps request ids onto RequestResults, which are castable to their specific request type.
     * This member can be used concurrently safely, as it is made synchronized in the constructor.
     */
    private Map<Integer, RequestResult> resultMap;

    /**
     * This is the last assigned id; it is tracked for the purposes of assigning new request ids.
     */
    private int lastAssignedId = 0;

    /**
     * This is a reference to the server which the coordinator uses to send and receive requests.
     */
    private NetworkServer server;

    /**
     * Standard constructor which makes sure all the data structures are safe to use concurrently.
     * @param   blockSize           the size of the blocks, in bytes
     * @param   segmentSize         the size of the segments, in blocks
     * @param   segmentGroupSize    the size of the segment groups for the system
     * @param   quorumSize          the quorum size for the system
     * @param   initialNodes        the first nodes to add to the system and connect to
     * @param   server              the server to be used for network requests
     */
    public Coordinator( int blockSize
                      , int segmentSize
                      , int segmentGroupSize
                      , int quorumSize
                      , List<DataNodeIdentifier> initialNodes
                      , NetworkServer server
                      )
    {
        this.blockSize = blockSize;
        this.segmentSize = segmentSize;
        this.segmentGroupSize = segmentGroupSize;
        this.quorumSize = quorumSize;
        this.server = server;
        datanodes = Collections.synchronizedList(new ArrayList<DataNodeIdentifier>(initialNodes));

        datanodeStatuses = new PriorityBlockingQueue<DataNodeStatusPair>();
        for (DataNodeIdentifier each : datanodes)
        {
            DataNodeStatus status = new DataNodeStatus(blockSize, segmentSize);
            DataNodeStatusPair pair = new DataNodeStatusPair(each, status);
            datanodeStatuses.add(pair);
        }

        segmentGroupList = Collections.synchronizedList(new ArrayList<SegmentGroup>());
        // TODO initialize segment groups here.

        volumeTable = new ConcurrentHashMap<Integer,Map<Long,SegmentGroup>>();

        requestCompletionMap = new ConcurrentHashMap<Integer, Boolean>();
        resultMap = new ConcurrentHashMap<Integer, RequestResult>();
    }

    /**
     * This method creates a new logical volume within the coordinator and tells the datanodes to do the same.
     * @param   volumeId    the id of the volume to create
     * @return  the request id, which is used to determine the success or failure of the attempt
     */
    public int createVolume(int volumeId)
    {
        int id = generateNewRequestId();

        CreateVolumeHandler handler = new CreateVolumeHandler(volumeId, this);
        handler.setRequestId(id);
        handler.start();

        Map<Long, SegmentGroup> volumeMap = new ConcurrentHashMap<Long, SegmentGroup>();
        volumeTable.put(volumeId, volumeMap);

        return id;
    }

    /**
     * This method indicates whether or not a certain request has finished.
     * @param   requestId   the id of the request we want to check
     * @return  true if the request is done (including time-outs or errors), false otherwise
     */
    public boolean createVolumeCompleted(int requestId)
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

    /**
     * This method returns the result of a create volume request.
     * Note: if it has not yet been set (for example, if the request is not finished), it will return null.
     * @param   requestId   the id of the request we want the result of
     * 
     */
    public CreateVolumeRequestResult createVolumeResult(int requestId)
    {
        RequestResult result = resultMap.get(requestId);
        if (result instanceof CreateVolumeRequestResult)
        {
            return (CreateVolumeRequestResult) result;
        }
        else
        {
            return null;
        }
    }

    /*
     * This method deletes a logical volume within the coordinator.
     */
    public int deleteVolume(int volumeId)
    {
        int id = generateNewRequestId();

        DeleteVolumeHandler handler = new DeleteVolumeHandler(volumeId, this);
        handler.setRequestId(id);
        handler.start();

        volumeTable.remove(volumeId);

        return id;
    }

    public boolean deleteVolumeCompleted(int requestId)
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

    public DeleteVolumeRequestResult deleteVolumeResult(int requestId)
    {
        RequestResult result = resultMap.get(requestId);
        if (result instanceof DeleteVolumeRequestResult)
        {
            return (DeleteVolumeRequestResult) result;
        }
        else
        {
            return null;
        }
    }

    /*
     * A synchronized method to generate new unique request IDs.
     */
    private synchronized int generateNewRequestId()
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
    public WriteRequestResult writeResult(int requestId)
    {
        RequestResult result = resultMap.get(requestId);
        if (result instanceof WriteRequestResult)
        {
            return (WriteRequestResult) result;
        }
        else
        {
            return null;
        }
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
    public ReadRequestResult readResult(Integer requestId)
    {
        RequestResult result = resultMap.get(requestId);
        if (result instanceof ReadRequestResult)
        {
            return (ReadRequestResult) result;
        }
        else
        {
            return null;
        }
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
    private SegmentGroup getSegmentGroup(int volumeId, long logicalOffset)
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
    private synchronized SegmentGroup assignSegmentGroup(int volumeId, long logicalOffset)
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

    /**
     * This method generates a new timestamp for use in network requests.
     * @return  a new timestamp corresponding to the system time.
     */
    private synchronized Date getNewTimestamp()
    {
        return new Date();
    }

}

