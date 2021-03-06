package com.virtualdisk.network.request;

import nl.jqno.equalsverifier.*;

import org.junit.*;
import static org.junit.Assert.*;

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

        EqualsVerifier.forClass(CreateVolumeRequest.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
    }
    
    @Test
    public void testCVRRCodec()
    {
        CreateVolumeRequestResult result = new CreateVolumeRequestResult(9, true, false);
        
        CreateVolumeRequestResult decodedResult = new CreateVolumeRequestResult(0, false, false);
        decodedResult.decode(result.encode());
        assertEquals("Results should be equal", result, decodedResult);

        EqualsVerifier.forClass(CreateVolumeRequestResult.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
    }
    
    @Test
    public void testDVRCodec()
    {
        DeleteVolumeRequest request = new DeleteVolumeRequest(10, 17);
        
        DeleteVolumeRequest decodedRequest = new DeleteVolumeRequest(0, 0);
        decodedRequest.decode(request.encode());
        assertEquals("Requests should be equal", request, decodedRequest);

        EqualsVerifier.forClass(DeleteVolumeRequest.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
    }
    
    @Test
    public void testDVRRCodec()
    {
        DeleteVolumeRequestResult result = new DeleteVolumeRequestResult(91, false, true);
        
        DeleteVolumeRequestResult decodedResult = new DeleteVolumeRequestResult(0, false, false);
        decodedResult.decode(result.encode());
        assertEquals("Results should be equal", result, decodedResult);

        EqualsVerifier.forClass(DeleteVolumeRequestResult.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
    }
    
    @Test
    public void testORCodec()
    {
        OrderRequest request = new OrderRequest(42, 16, 23L, new Date(34));
        
        OrderRequest decodedRequest = new OrderRequest(0, 0, 0, null);
        decodedRequest.decode(request.encode());
        assertEquals("Requests should be equal", request, decodedRequest);

        EqualsVerifier.forClass(OrderRequest.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS, Warning.NULL_FIELDS)
                      .verify();
    }
    
    @Test
    public void testORRCodec()
    {
        OrderRequestResult result = new OrderRequestResult(3019, true, true);
        
        OrderRequestResult decodedResult = new OrderRequestResult(0, false, false);
        decodedResult.decode(result.encode());
        assertEquals("Results should be equal", result, decodedResult);

        EqualsVerifier.forClass(OrderRequestResult.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
    }
    
    @Test
    public void testRRCodec()
    {
        ReadRequest request = new ReadRequest(2012, 13, 15L);
        
        ReadRequest decodedRequest = new ReadRequest(0, 0, 0);
        decodedRequest.decode(request.encode());
        assertEquals("Requests should be equal", request, decodedRequest);

        EqualsVerifier.forClass(ReadRequest.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
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

        EqualsVerifier.forClass(ReadRequestResult.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS, Warning.NULL_FIELDS)
                      .verify();
    }
    
    @Test
    public void testVERCodec()
    {
        VolumeExistsRequest request = new VolumeExistsRequest(102, 19);
        
        VolumeExistsRequest decodedRequest = new VolumeExistsRequest(0, 0);
        decodedRequest.decode(request.encode());
        assertEquals("Requests should be equal", request, decodedRequest);

        EqualsVerifier.forClass(VolumeExistsRequest.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
    }
    
    @Test
    public void testVERRCodec()
    {
        VolumeExistsRequestResult result = new VolumeExistsRequestResult(103, true, true, true);
        
        VolumeExistsRequestResult decodedResult = new VolumeExistsRequestResult(0, false, false, false);
        decodedResult.decode(result.encode());
        assertEquals("Results should be equal", result, decodedResult);

        EqualsVerifier.forClass(VolumeExistsRequestResult.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
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

        EqualsVerifier.forClass(WriteRequest.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS, Warning.NULL_FIELDS)
                      .verify();
    }
    
    @Test
    public void testWRRCodec()
    {
        WriteRequestResult result = new WriteRequestResult(1931, true, true);
        
        WriteRequestResult decodedResult = new WriteRequestResult(0, false, false);
        decodedResult.decode(result.encode());
        assertEquals("Results should be equal", result, decodedResult);

        EqualsVerifier.forClass(WriteRequestResult.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
    }

    @Test
    public void testUSRCodec()
    {
        UnsetSegmentRequest request = new UnsetSegmentRequest(132, 13, 193, 194);

        UnsetSegmentRequest decodedRequest = new UnsetSegmentRequest(0, 0, 0, 0);
        decodedRequest.decode(request.encode());
        assertEquals("Requests should be equal", request, decodedRequest);

        EqualsVerifier.forClass(UnsetSegmentRequest.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
    }

    @Test
    public void testUSRRCodec()
    {
        UnsetSegmentRequestResult result = new UnsetSegmentRequestResult(19, true, true);

        UnsetSegmentRequestResult decodedResult = new UnsetSegmentRequestResult(0, false, false);
        decodedResult.decode(result.encode());
        assertEquals("Results should be equal", result, decodedResult);

        EqualsVerifier.forClass(UnsetSegmentRequestResult.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
    }

    @Test
    public void testIRCodec()
    {
        IdentifyRequest request = new IdentifyRequest(39);

        IdentifyRequest decodedRequest = new IdentifyRequest(0);
        decodedRequest.decode(request.encode());
        assertEquals("Requests should be equal", request, decodedRequest);

        EqualsVerifier.forClass(IdentifyRequest.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
    }

    @Test
    public void testIRRCodec()
    {
        IdentifyRequestResult result = new IdentifyRequestResult(39, (byte)2);

        IdentifyRequestResult decodedResult = new IdentifyRequestResult(0, (byte)-1);
        decodedResult.decode(result.encode());
        assertEquals("Results should be equal", result, decodedResult);

        EqualsVerifier.forClass(IdentifyRequestResult.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
    }

    @Test
    public void testReconfigurationRRCodec()
    {
        ReconfigurationRequestResult result = new ReconfigurationRequestResult(17, true, true);

        ReconfigurationRequestResult decodedResult = new ReconfigurationRequestResult(0, false, false);
        decodedResult.decode(result.encode());
        assertEquals("Results should be equal", result, decodedResult);

        EqualsVerifier.forClass(IdentifyRequestResult.class)
                      .withRedefinedSuperclass()
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
    }
}
