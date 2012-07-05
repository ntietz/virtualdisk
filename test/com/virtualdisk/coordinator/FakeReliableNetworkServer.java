package com.virtualdisk.coordinator;


import com.virtualdisk.coordinator.*;
import com.virtualdisk.datanode.*;
import com.virtualdisk.network.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.util.*;

import java.io.*;
import java.lang.*;
import java.util.*;

public class FakeReliableNetworkServer
extends NetworkServer
{
    // This list stores the datanode identifiers.
    List<DataNodeIdentifier> dataNodeIdentifiers;

    // This list stores the datanodes, populated by the node generator method, since we are communicating directly with them.
    List<DataNode> dataNodes;

    // These maps cache the request results for later fetching by the coordinator.
    Map<Integer, List<OrderRequestResult>> orderRequestResults;
    Map<Integer, List<ReadRequestResult>> readRequestResults;
    Map<Integer, List<WriteRequestResult>> writeRequestResults;
    Map<Integer, List<CreateVolumeRequestResult>> createVolumeRequestResults;
    Map<Integer, List<DeleteVolumeRequestResult>> deleteVolumeRequestResults;
    Map<Integer, List<VolumeExistsRequestResult>> volumeExistsReqestResults;

    /*
     * This constructor initializes the datanode with empty node identifier and node lists, and empty result maps.
     */
    public FakeReliableNetworkServer()
    {
        dataNodeIdentifiers = new ArrayList<DataNodeIdentifier>();
        dataNodes = new ArrayList<DataNode>();

        orderRequestResults = new HashMap<Integer, List<OrderRequestResult>>();
        readRequestResults = new HashMap<Integer, List<ReadRequestResult>>();
        writeRequestResults = new HashMap<Integer, List<WriteRequestResult>>();
        createVolumeRequestResults = new HashMap<Integer, List<CreateVolumeRequestResult>>();
        deleteVolumeRequestResults = new HashMap<Integer, List<DeleteVolumeRequestResult>>();
        volumeExistsReqestResults = new HashMap<Integer, List<VolumeExistsRequestResult>>();
    }

    /*
     * This method issues an order request and returns the id we can use to later fetch the results.
     */
    public synchronized int issueOrderRequest(SegmentGroup targets, int volumeId, long logicalOffset, Date timestamp)
    {
        Integer id = generateNewRequestId();

        List<OrderRequestResult> results = new ArrayList<OrderRequestResult>();

        for (DataNodeIdentifier each : targets.getMembers())
        {
            DataNode node = dataNodes.get(each.getNodeId());
            Boolean value = node.order(volumeId, logicalOffset, timestamp);

            OrderRequestResult result = new OrderRequestResult(id, true, value);
            results.add(result);
        }

        orderRequestResults.put(id, results);

        return id;
    }

    /*
     * This method issues a write request and returns the id we can use to later fetch the results.
     */
    public synchronized int issueWriteRequest(SegmentGroup targets, int volumeId, long logicalOffset, byte[] block, Date timestamp)
    {
        Integer id = generateNewRequestId();

        List<WriteRequestResult> results = new ArrayList<WriteRequestResult>();

        for (DataNodeIdentifier each : targets.getMembers())
        {
            DataNode node = dataNodes.get(each.getNodeId());
            //node.createVolume(volumeId); // can be done safely each time. would be handled by the datanode server.
            Boolean value = node.write(volumeId, logicalOffset, block, timestamp);

            WriteRequestResult result = new WriteRequestResult(id, true, value);
            results.add(result);
        }

        writeRequestResults.put(id, results);

        return id;
    }

    /*
     * This method issues a read request and returns the id we can use to later fetch the results.
     */
    public synchronized int issueReadRequest(SegmentGroup targets, int volumeId, long logicalOffset)
    {
        Integer id = generateNewRequestId();

        List<ReadRequestResult> results = new ArrayList<ReadRequestResult>();

        for (DataNodeIdentifier each : targets.getMembers())
        {
            DataNode node = dataNodes.get(each.getNodeId());
            byte[] value = node.read(volumeId, logicalOffset);
            Date ts = node.getValueTimestamp(volumeId, logicalOffset);

            ReadRequestResult result = new ReadRequestResult(id, true, true, ts, value);
            results.add(result);
        }

        readRequestResults.put(id, results);

        return id;
    }

    public synchronized int issueVolumeCreationRequest(int volumeId)
    {
        int id = generateNewRequestId();
        
        List<CreateVolumeRequestResult> results = new ArrayList<CreateVolumeRequestResult>();
        
        for (DataNode each : dataNodes)
        {
            each.createVolume(volumeId);
            
            CreateVolumeRequestResult result = new CreateVolumeRequestResult(id, true, true);
            results.add(result);
        }
        
        createVolumeRequestResults.put(id, results);
        
        return id;
    }
    
    public synchronized int issueVolumeDeletionRequest(int volumeId)
    {
        int id = generateNewRequestId();
        
        List<DeleteVolumeRequestResult> results = new ArrayList<DeleteVolumeRequestResult>();
        
        for (DataNode each : dataNodes)
        {
            each.deleteVolume(volumeId);
            
            DeleteVolumeRequestResult result = new DeleteVolumeRequestResult(id, true, true);
            results.add(result);
        }
        
        deleteVolumeRequestResults.put(id, results);
        
        return id;
    }
    
    public synchronized int issueVolumeExistsRequest(int volumeId)
    {
        int id = generateNewRequestId();
        
        List<VolumeExistsRequestResult> results = new ArrayList<VolumeExistsRequestResult>();
        
        for (DataNode each : dataNodes)
        {
            VolumeExistsRequestResult result = new VolumeExistsRequestResult(id, true, true, each.volumeExists(volumeId));
            results.add(result);
        }
        
        volumeExistsReqestResults.put(id, results);
        
        return id;
    }
    
    /*
     * This method fetches the results of a given order request.
     * If the request id is invalid, it will return null.
     */
    public List<OrderRequestResult> getOrderRequestResults(int requestId)
    {
        return orderRequestResults.get(requestId);
    }

    /*
     * This method fetches the results of a given write request.
     * If the request id is invalid, it will return null.
     */
    public List<WriteRequestResult> getWriteRequestResults(int requestId)
    {
        return writeRequestResults.get(requestId);
    }

    /*
     * This method fetches the results of a given read request.
     * If the request id is invalid, it will return null.
     */
    public List<ReadRequestResult> getReadRequestResults(int requestId)
    {
        return readRequestResults.get(requestId);
    }
    
    public List<CreateVolumeRequestResult> getVolumeCreationRequestResults(int requestId)
    {
        return createVolumeRequestResults.get(requestId);
    }
    
    public List<DeleteVolumeRequestResult> getVolumeDeletionRequestResults(int requestId)
    {
        return deleteVolumeRequestResults.get(requestId);
    }
    
    public List<VolumeExistsRequestResult> getVolumeExistsRequestResults(int requestId)
    {
        return volumeExistsReqestResults.get(requestId);
    }
    
    /*
     * This method fetches the datanodes the server is configured to use.
     */
    public List<DataNodeIdentifier> getDataNodes()
    {
        return dataNodeIdentifiers;
    }

    /*
     * This method returns false. It does nothing.
     */
    public boolean attachDataNode(DataNodeIdentifier node)
    {
        return false;
    }

    /*
     * This method returns false. It does nothing.
     */
    public boolean detachDataNode(DataNodeIdentifier node)
    {
        return false;
    }

    /*
     * This method generates new datanodes with the specified block size, drive size, and number of virtual disks per node.
     * Each virtual disk is named "fakedrive.nodeNumber.driveNumber". The disks are intialized to all 0s.
     * After the nodes and disks are created, the nodes and node identifiers are assigned for the network server to use.
     */
    public List<DataNodeIdentifier> generateDataNodes(int numberOfNodes, int blockSize, long driveSizeInBlocks, int disksPerNode)
    throws IOException
    {
        List<DataNodeIdentifier> ids = new ArrayList<DataNodeIdentifier>(numberOfNodes);
        List<DataNode> nodes = new ArrayList<DataNode>(numberOfNodes);

        final byte[] emptyBlock = new byte[blockSize];
        for (int index = 0; index < blockSize; ++index)
            emptyBlock[index] = 0;

        for (int index = 0; index < numberOfNodes; ++index)
        {
            String[] handles = new String[disksPerNode];
            Long[] sizes = new Long[disksPerNode];
            for (int loc = 0; loc < disksPerNode; ++loc)
            {
                handles[loc] = "data/fakedrive." + (Integer.toString(index)) + "." + (Integer.toString(loc));
                sizes[loc] = driveSizeInBlocks;

                File f = new File(handles[loc]);
                RandomAccessFile out = new RandomAccessFile(f, "rw");
                out.seek(0);
                for (int place = 0; place < driveSizeInBlocks; ++place)
                {
                    out.write(emptyBlock);
                }
            }

            DataNodeIdentifier currentId = new DataNodeIdentifier(index, Integer.toString(index), index);
            DataNode current = new DataNode(blockSize, Arrays.asList(handles), Arrays.asList(sizes));

            ids.add(index, currentId);
            nodes.add(index, current);
        }

        dataNodes = nodes;
        dataNodeIdentifiers = ids;

        return ids;
    }

    public void setResultFutures(int requestId, List<RequestFuture> futures)
    {
        throw new UnsupportedOperationException("This operation not supported in this test fixture.");
    }

    public List<RequestFuture> getResultFutures(int requestId)
    {
        throw new UnsupportedOperationException("This operation not supported in this test fixture.");
    }

    /*
     * This method generates unique ids for use by the other methods.
     */
    private int lastAssignedId = 0;
    private synchronized int generateNewRequestId()
    {
        ++lastAssignedId;
        return lastAssignedId;
    }

}

