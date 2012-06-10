package com.virtualdisk.network.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Date;
import java.util.Random;

public class RequestTest
{

    @Test
    public void testCVRCodec()
    {
        CreateVolumeRequest request = new CreateVolumeRequest(10, 13);
        
        CreateVolumeRequest decodedRequest = new CreateVolumeRequest(0, 0);
        decodedRequest.decode(request.encode());
        assertEquals("Requests should be equal", request, decodedRequest);
    }
    
    @Test
    public void testCVRRCodec()
    {
        CreateVolumeRequestResult result = new CreateVolumeRequestResult(9, true, false);
        
        CreateVolumeRequestResult decodedResult = new CreateVolumeRequestResult(0, false, false);
        decodedResult.decode(result.encode());
        assertEquals("Results should be equal", result, decodedResult);
    }
    
    @Test
    public void testDVRCodec()
    {
        DeleteVolumeRequest request = new DeleteVolumeRequest(10, 17);
        
        DeleteVolumeRequest decodedRequest = new DeleteVolumeRequest(0, 0);
        decodedRequest.decode(request.encode());
        assertEquals("Requests should be equal", request, decodedRequest);
    }
    
    @Test
    public void testDVRRCodec()
    {
        DeleteVolumeRequestResult result = new DeleteVolumeRequestResult(91, false, true);
        
        DeleteVolumeRequestResult decodedResult = new DeleteVolumeRequestResult(0, false, false);
        decodedResult.decode(result.encode());
        assertEquals("Results should be equal", result, decodedResult);
    }
    
    @Test
    public void testORCodec()
    {
        OrderRequest request = new OrderRequest(42, 16, 23L, new Date(34));
        
        OrderRequest decodedRequest = new OrderRequest(0, 0, 0, null);
        decodedRequest.decode(request.encode());
        assertEquals("Requests should be equal", request, decodedRequest);
    }
    
    @Test
    public void testORRCodec()
    {
        OrderRequestResult result = new OrderRequestResult(3019, true, true);
        
        OrderRequestResult decodedResult = new OrderRequestResult(0, false, false);
        decodedResult.decode(result.encode());
        assertEquals("Results should be equal", result, decodedResult);
    }
    
    @Test
    public void testRRCodec()
    {
        ReadRequest request = new ReadRequest(2012, 13, 15L);
        
        ReadRequest decodedRequest = new ReadRequest(0, 0, 0);
        decodedRequest.decode(request.encode());
        assertEquals("Requests should be equal", request, decodedRequest);
    }
    
    @Test
    public void testRRRCodec()
    {
        int blockSize = 32;
        byte[] block = new byte[blockSize];
        Random random = new Random(2603);
        random.nextBytes(block);
        
        ReadRequestResult result = new ReadRequestResult(2013, true, true, new Date(23), block);
        
        ReadRequestResult decodedResult = new ReadRequestResult(0, false, false, null, null);
        decodedResult.decode(result.encode());
        assertEquals("Results should be equal", result, decodedResult);
    }
    
    @Test
    public void testVERCodec()
    {
        VolumeExistsRequest request = new VolumeExistsRequest(102, 19);
        
        VolumeExistsRequest decodedRequest = new VolumeExistsRequest(0, 0);
        decodedRequest.decode(request.encode());
        assertEquals("Requests should be equal", request, decodedRequest);
    }
    
    @Test
    public void testVERRCodec()
    {
        VolumeExistsRequestResult result = new VolumeExistsRequestResult(103, true, true);
        
        VolumeExistsRequestResult decodedResult = new VolumeExistsRequestResult(0, false, false);
        decodedResult.decode(result.encode());
        assertEquals("Results should be equal", result, decodedResult);
    }
    
    @Test
    public void testWRCodec()
    {
        int blockSize = 32;
        byte[] block = new byte[blockSize];
        Random random = new Random(2603);
        random.nextBytes(block);
        WriteRequest request = new WriteRequest(10402, 9, 23L, new Date(32), block);
        
        WriteRequest decodedRequest = new WriteRequest(0, 0, 0, null, null);
        decodedRequest.decode(request.encode());
        assertEquals("Requests should be equal", request, decodedRequest);
    }
    
    @Test
    public void testWRRCodec()
    {
        WriteRequestResult result = new WriteRequestResult(1931, true, true);
        
        WriteRequestResult decodedResult = new WriteRequestResult(0, false, false);
        decodedResult.decode(result.encode());
        assertEquals("Results should be equal", result, decodedResult);
    }
}
