package com.virtualdisk.coordinator;

import com.virtualdisk.coordinator.handler.*;
import com.virtualdisk.network.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Coordinator handles coordinating read and write requests, etc.
 */
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
    private int nodesPerSegmentGroup;
    private int segmentsPerSegmentGroup;

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
    private VolumeTable volumeTable;
    //private Map<Integer,Map<Long,SegmentGroup>> volumeTable;

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

    private boolean reconfigurationInProgress = false;

    /**
     * Standard constructor which makes sure all the data structures are safe to use concurrently.
     * @param   blockSize           the size of the blocks, in bytes
     * @param   segmentSize         the size of the segments, in blocks
     * @param   segmentGroupSize    the size of the segment groups for the system
     * @param   quorumSize          the quorum size for the system
     * @param   initialNodes        the first nodes to add to the system and connect to
     * @param   server              the server to be used for network requests
     * TODO FIXME fix the javadoc
     */
    public Coordinator( int blockSize
                      , int segmentSize
                      , int segmentsPerSegmentGroup
                      , int nodesPerSegmentGroup
                      , int quorumSize
                      , List<DataNodeIdentifier> initialNodes
                      , NetworkServer server
                      )
    {
        this.blockSize = blockSize;
        this.segmentSize = segmentSize;
        this.segmentsPerSegmentGroup = segmentsPerSegmentGroup;
        this.nodesPerSegmentGroup = nodesPerSegmentGroup;
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

        volumeTable = new VolumeTable(blockSize, segmentSize, segmentsPerSegmentGroup, nodesPerSegmentGroup);

        requestCompletionMap = new ConcurrentHashMap<Integer, Boolean>();
        resultMap = new ConcurrentHashMap<Integer, RequestResult>();
    }

    /**
     * Getter for blockSize.
     * @return the block size in bytes
     */
    public int getBlockSize()
    {
        return blockSize;
    }

    /**
     * Getter for segmentSize.
     * @return  the segment size in blocks
     */
    public int getSegmentSize()
    {
        return segmentSize;
    }

    public int getNodesPerSegmentGroup()
    {
        return nodesPerSegmentGroup;
    }

    public int getSegmentsPerSegmentGroup()
    {
        return segmentsPerSegmentGroup;
    }

    /**
     * Getter for quorumSize.
     * @return  the quorum size
     */
    public int getQuorumSize()
    {
        return quorumSize;
    }

    /**
     * Getter for the server.
     * @return  the network server this coordinator is configured to use
     */
    public NetworkServer getServer()
    {
        return server;
    }

    /**
     * Sets the result of a request.
     * @param   requestId   the id of the request
     * @param   result      the result of the request
     */
    public void setRequestResult(int requestId, RequestResult result)
    {
        resultMap.put(requestId, result);
        requestCompletionMap.put(requestId, true);
    }

    /**
     * This method indicates whether a request has finished or not.
     * @param   requestId   the id of the request
     * @return  true if hte request has finished (or timed out, or errored out, etc.), false otherwise
     */
    public boolean requestFinished(int requestId)
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

        volumeTable.addVolume(volumeId);

        return id;
    }

    /**
     * This method returns the result of a create volume request.
     * Note: if it has not yet been set (for example, if the request is not finished), it will return null.
     * @param   requestId   the id of the request we want the result of
     * @return  the result of the request, or null if it has not been stored yet
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

    /**
     * This method issues a request to check whether a certain volume exists or not.
     * @param   volumeId    the id of the volume to check for
     * @return  the id for the request, to check for results later
     */
    public int volumeExists(int volumeId)
    {
        int id = generateNewRequestId();

        VolumeExistsHandler handler = new VolumeExistsHandler(volumeId, this);
        handler.setRequestId(id);
        handler.start();

        return id;
    }

    /**
     * This method returns the result of a volume exists request.
     * @param   requestId   the id of the request we want the result of
     * @return  the result of the request, or null if it has not been stored yet
     */
    public VolumeExistsRequestResult volumeExistsResult(int requestId)
    {
        RequestResult result = resultMap.get(requestId);
        if (result instanceof VolumeExistsRequestResult)
        {
            return (VolumeExistsRequestResult) result;
        }
        else
        {
            return null;
        }
    }

    /**
     * This method deletes a logical volume within the coordinator and tells the datanodes to do the same.
     * @param   volumeId    the id of the volume to delete
     * @return  the request id, which is used to determine the success or failure of the attempt
     */
    public int deleteVolume(int volumeId)
    {
        int id = generateNewRequestId();

        DeleteVolumeHandler handler = new DeleteVolumeHandler(volumeId, this);
        handler.setRequestId(id);
        handler.start();

        volumeTable.deleteVolume(volumeId);

        return id;
    }

    /**
     * This method returns the result of a delete volume request.
     * Note: if it has not yet been set (for example, if the request is not finished), it will return null.
     * @param   requestId   the id of the request we want the result of
     * @return  the result of the request, or null if it has not been stored yet
     */
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

    /**
     * A synchronized method to generate new unique request IDs.
     * Note: this id may not be unique. After 2^32 requests, ids will repeat.
     * @return  a new unique request id
     */
    private synchronized int generateNewRequestId()
    {
        ++lastAssignedId;
        return lastAssignedId;
    }
    
    /**
     * Initiates the write request and returns the request's id.
     * @param   volumeId        the volume the write is writing to
     * @param   logicalOffset   the logical offset of the write
     * @param   block           the data we are writing
     * @return  the id of the request
     */
    public int write(int volumeId, long logicalOffset, byte[] block)
    {
        SegmentGroup segmentGroup = getSegmentGroup(volumeId, logicalOffset);
        return writeWithTarget(segmentGroup, volumeId, logicalOffset, block);
    }

    public int writeWithTarget(SegmentGroup targetGroup, int volumeId, long logicalOffset, byte[] block)
    {
        int id = generateNewRequestId();

        WriteHandler handler = new WriteHandler(volumeId, logicalOffset, block, targetGroup, this);
        handler.setRequestId(id);
        handler.start();

        return id;
    }

    /**
     * This method returns the result of a write request.
     * Note: if it has not yet been set (for example, if the request is not finished), it will return null.
     * @param   requestId   the id of the request we want the result of
     * @return  the result of the request, or null if it has not been stored yet
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

    /**
     * This method performs a read request.
     * @param   volumeId        the volume id of the request
     * @param   logicalOffset   the logical offset of the request
`    * @return  the request id for this request
     */
    public int read(int volumeId, long logicalOffset)
    {
        int id = generateNewRequestId();

        SegmentGroup segmentGroup = getSegmentGroup(volumeId, logicalOffset);
        ReadHandler handler = new ReadHandler(volumeId, logicalOffset, segmentGroup, this);
        handler.setRequestId(id);
        handler.start();

        return id;
    }

    /**
     * This method returns the result of a read request.
     * Note: if it has not yet been set (for example, if the request is not finished), it will return null.
     * @param   requestId   the id of the request we want the result of
     * @return  the result of the request, or null if it has not been stored yet
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

    public int addDataNode(DataNodeIdentifier node)
    {
        int id = generateNewRequestId();

        DataNodeReconfigurationHandler handler = new DataNodeReconfigurationHandler( volumeTable
                                                                                   , datanodes
                                                                                   , datanodeStatuses
                                                                                   , server
                                                                                   , node
                                                                                   , true
                                                                                   , this
                                                                                   );
        handler.setRequestId(id);
        handler.start();

        return id;
    }

    public int removeDataNode(DataNodeIdentifier node)
    {
        int id = generateNewRequestId();

        DataNodeReconfigurationHandler handler = new DataNodeReconfigurationHandler( volumeTable
                                                                                   , datanodes
                                                                                   , datanodeStatuses
                                                                                   , server
                                                                                   , node
                                                                                   , false
                                                                                   , this
                                                                                   );
        handler.setRequestId(id);
        handler.start();

        return id;
    }

    // note: should only be called when reconfiguration is done
    public void attachDataNode(DataNodeIdentifier node)
    {
        datanodes.add(node);

        DataNodeStatus status = new DataNodeStatus(blockSize, segmentSize);
        datanodeStatuses.put(new DataNodeStatusPair(node, status));
    }

    // note: should only be called when reconfiguration is done
    public void detachDataNode(DataNodeIdentifier node)
    {
        datanodes.remove(node);

        for (DataNodeStatusPair each : datanodeStatuses)
        {
            if (each.getIdentifier().equals(node))
            {
                datanodeStatuses.remove(each);
                break;
            }
        }
    }

    /**
     * Triest to start a reconfiguration attempt; it is an atomic test-and-set.
     * @return  true if we got the lock; false if someone else is already reconfiguring
     */
    public synchronized boolean startReconfiguration()
    {
        boolean oldValue = reconfigurationInProgress;
        reconfigurationInProgress = true;
        return !oldValue;
    }

    public synchronized void finishReconfiguration()
    {
        reconfigurationInProgress = false;
    }

    /**
     * This method returns the segment group for a volumeId and logical offset.
     * If the volumeId and logical offset pair do not have a segment group, it will be assigned.
     * @param   volumeId        the volume id we want to use
     * @param   logicalOffset   the logical offset of the request
     * @return  the segment group for the given (volume, logicalOffset) pair
     */
    private SegmentGroup getSegmentGroup(int volumeId, long logicalOffset)
    {
        SegmentGroup segmentgroup = volumeTable.getSegmentGroup(volumeId, logicalOffset);

        if (segmentgroup == null)
        {
            segmentgroup = assignSegmentGroup(volumeId, logicalOffset);
        }

        return segmentgroup;
    }

    /**
     * This method takes a volumeId and logical offset and assigns that pair a segment group.
     * It generates the segment group based off which nodes have the lightest load.
     * @param   volumeId        the volume id we want to use
     * @param   logicalOffset   the logical offset of the request
     * @return  the segment group for the given (volume, logicalOffset) pair
     */
    private synchronized SegmentGroup assignSegmentGroup(int volumeId, long logicalOffset)
    {
        SegmentGroup segmentgroup = volumeTable.getSegmentGroup(volumeId, logicalOffset);

        if (segmentgroup != null)
        {
            return segmentgroup;
        }
        else
        {
            List<DataNodeStatusPair> segmentGroupMemberPairs = new ArrayList<DataNodeStatusPair>();
            List<DataNodeIdentifier> segmentGroupMembers = new ArrayList<DataNodeIdentifier>();

            for (int index = 0; index < nodesPerSegmentGroup; ++index)
            {
                DataNodeStatusPair current = datanodeStatuses.poll();
                DataNodeStatus status = current.getStatus();
                status.addStoredSegments(segmentsPerSegmentGroup);

                segmentGroupMemberPairs.add(current);
                segmentGroupMembers.add(current.getIdentifier());
            }

            segmentgroup = volumeTable.makeSegmentGroup(segmentGroupMembers, volumeId, logicalOffset);

            for (int index = 0; index < nodesPerSegmentGroup; ++index)
            {
                datanodeStatuses.add(segmentGroupMemberPairs.get(index));
            }

            volumeTable.setSegmentGroup(volumeId, logicalOffset, segmentgroup);

            return segmentgroup;
        }
    }

    /**
     * This method generates a new timestamp for use in network requests.
     * @return  a new timestamp corresponding to the system time.
     */
    public Date getTimestamp()
    {
        return new Date();
    }

}

