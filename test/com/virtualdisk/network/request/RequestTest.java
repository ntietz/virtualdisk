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
        CreateVolumeRequest request = new CreateVolumeRequest(13);
        
        CreateVolumeRequest decodedRequest = new CreateVolumeRequest(0);
        assertTrue("Decoding should succeed", decodedRequest.decode(request.encode()));
        assertEquals("Requests should be equal", request, decodedRequest);
    }
    
    @Test
    public void testCVRRCodec()
    {
        CreateVolumeRequestResult result = new CreateVolumeRequestResult(true, false);
        
        CreateVolumeRequestResult decodedResult = new CreateVolumeRequestResult(false, false);
        assertTrue("Decoding should succeed", decodedResult.decode(result.encode()));
        assertEquals("Results should be equal", result, decodedResult);
    }
    
    @Test
    public void testDVRCodec()
    {
        DeleteVolumeRequest request = new DeleteVolumeRequest(17);
        
        DeleteVolumeRequest decodedRequest = new DeleteVolumeRequest(0);
        assertTrue("Decoding should succeed", decodedRequest.decode(request.encode()));
        assertEquals("Requests should be equal", request, decodedRequest);
    }
    
    @Test
    public void testDVRRCodec()
    {
        DeleteVolumeRequestResult result = new DeleteVolumeRequestResult(false, true);
        
        DeleteVolumeRequestResult decodedResult = new DeleteVolumeRequestResult(false, false);
        assertTrue("Decoding should succeed", decodedResult.decode(result.encode()));
        assertEquals("Results should be equal", result, decodedResult);
    }
    
    @Test
    public void testORCodec()
    {
        OrderRequest request = new OrderRequest(16, 23L, new Date(34));
        
        OrderRequest decodedRequest = new OrderRequest(0, 0, null);
        assertTrue("Decoding should succeed", decodedRequest.decode(request.encode()));
        assertEquals("Requests should be equal", request, decodedRequest);
    }
    
    @Test
    public void testORRCodec()
    {
        OrderRequestResult result = new OrderRequestResult(true, true);
        
        OrderRequestResult decodedResult = new OrderRequestResult(false, false);
        assertTrue("Decoding should succeed", decodedResult.decode(result.encode()));
        assertEquals("Results should be equal", result, decodedResult);
    }
    
    @Test
    public void testRRCodec()
    {
        ReadRequest request = new ReadRequest(13, 15L);
        
        ReadRequest decodedRequest = new ReadRequest(0, 0);
        assertTrue("Decoding should succeed", decodedRequest.decode(request.encode()));
        assertEquals("Requests should be equal", request, decodedRequest);
    }
    
    @Test
    public void testRRRCodec()
    {
        int blockSize = 32;
        byte[] block = new byte[blockSize];
        Random random = new Random(2603);
        random.nextBytes(block);
        
        ReadRequestResult result = new ReadRequestResult(true, true, block, new Date(23));
        
        ReadRequestResult decodedResult = new ReadRequestResult(false, false, null, null);
        assertTrue("Decoding should succeed", decodedResult.decode(result.encode()));
        assertEquals("Results should be equal", result, decodedResult);
    }
    
    @Test
    public void testVERCodec()
    {
        VolumeExistsRequest request = new VolumeExistsRequest(19);
        
        VolumeExistsRequest decodedRequest = new VolumeExistsRequest(0);
        assertTrue("Decoding should succeed", decodedRequest.decode(request.encode()));
        assertEquals("Requests should be equal", request, decodedRequest);
    }
    
    @Test
    public void testVERRCodec()
    {
        VolumeExistsRequestResult result = new VolumeExistsRequestResult(true, true);
        
        VolumeExistsRequestResult decodedResult = new VolumeExistsRequestResult(false, false);
        assertTrue("Decoding should succeed", decodedResult.decode(result.encode()));
        assertEquals("Results should be equal", result, decodedResult);
    }
    
    @Test
    public void testWRCodec()
    {
        int blockSize = 32;
        byte[] block = new byte[blockSize];
        Random random = new Random(2603);
        random.nextBytes(block);
        WriteRequest request = new WriteRequest(9, 23L, block, new Date(32));
        
        WriteRequest decodedRequest = new WriteRequest(0, 0, null, null);
        assertTrue("Decoding should succeed", decodedRequest.decode(request.encode()));
        assertEquals("Requests should be equal", request, decodedRequest);
    }
    
    @Test
    public void testWRRCodec()
    {
        WriteRequestResult result = new WriteRequestResult(true, true);
        
        WriteRequestResult decodedResult = new WriteRequestResult(false, false);
        assertTrue("Decoding should succeed", decodedResult.decode(result.encode()));
        assertEquals("Results should be equal", result, decodedResult);
    }
}
