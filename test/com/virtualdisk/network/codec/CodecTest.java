package com.virtualdisk.network.codec;

import com.virtualdisk.network.request.*;

import org.jboss.netty.buffer.*;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;

public class CodecTest
{
    public final int seed = 2603;

    @Test
    public void dummy() { }

    /*
    @Test
    public void testCodecGarbage()
    {
        RequestDecoder decoder = new RequestDecoder();

        fail("Not yet implemented.");
    }
    */

    @Test
    public void testCodecWrite()
    throws Exception
    {
        RequestEncoder encoder = new RequestEncoder();
        RequestDecoder decoder = new RequestDecoder();

        Random writeRandom = new Random(seed);
        int requestId = 53;
        int volumeId = 94;
        long logicalOffset = 1043L;
        Date timestamp = new Date(5832);
        byte[] block = new byte[14];
        writeRandom.nextBytes(block);

        List<WriteRequest> writeRequests = new ArrayList<WriteRequest>();

        writeRequests.add(new WriteRequest(requestId, volumeId, logicalOffset, timestamp, block));

        requestId += 942;
        writeRequests.add(new WriteRequest(requestId, volumeId, logicalOffset, timestamp, block));

        volumeId += 18492;
        writeRequests.add(new WriteRequest(requestId, volumeId, logicalOffset, timestamp, block));

        logicalOffset -= 194L;
        writeRequests.add(new WriteRequest(requestId, volumeId, logicalOffset, timestamp, block));

        timestamp = new Date(194948);
        writeRequests.add(new WriteRequest(requestId, volumeId, logicalOffset, timestamp, block));

        block = new byte[194];
        writeRandom.nextBytes(block);
        writeRequests.add(new WriteRequest(requestId, volumeId, logicalOffset, timestamp, block));

        for (WriteRequest request : writeRequests)
        {
            ChannelBuffer buffer = (ChannelBuffer) encoder.encode(null, null, request);
            WriteRequest decodedRequest = (WriteRequest) decoder.decode(null, null, buffer);

            assertEquals("Decoded object should be equal.", request, decodedRequest);
        }

        ChannelBuffer resultBuffer = ChannelBuffers.dynamicBuffer();
        for (int index = 0; index < writeRequests.size(); ++index)
        {
            WriteRequest request = writeRequests.get(index);
            ChannelBuffer buffer = (ChannelBuffer) encoder.encode(null, null, request);
            resultBuffer.writeBytes(buffer);
        }

        for (int index = 0; index < writeRequests.size(); ++index)
        {
            WriteRequest request = writeRequests.get(index);
            WriteRequest decodedRequest = (WriteRequest) decoder.decode(null, null, resultBuffer);

            assertEquals("Decoded object should be equal.", request, decodedRequest);
        }

        List<WriteRequestResult> writeResults = new ArrayList<WriteRequestResult>();

        boolean done = false;
        boolean success = false;

        writeResults.add(new WriteRequestResult(requestId, done, success));

        requestId += 8472;
        writeResults.add(new WriteRequestResult(requestId, done, success));

        done = !done;
        writeResults.add(new WriteRequestResult(requestId, done, success));

        success = !success;
        writeResults.add(new WriteRequestResult(requestId, done, success));

        for (WriteRequestResult result : writeResults)
        {
            ChannelBuffer buffer = (ChannelBuffer) encoder.encode(null, null, result);
            WriteRequestResult decodedResult = (WriteRequestResult) decoder.decode(null, null, buffer);

            assertEquals("Decoded object should be equal.", result, decodedResult);
        }

        resultBuffer = ChannelBuffers.dynamicBuffer();
        for (int index = 0; index < writeResults.size(); ++index)
        {
            WriteRequestResult result = writeResults.get(index);
            ChannelBuffer buffer = (ChannelBuffer) encoder.encode(null, null, result);
            resultBuffer.writeBytes(buffer);
        }

        for (int index = 0; index < writeResults.size(); ++index)
        {
            WriteRequestResult result = writeResults.get(index);
            WriteRequestResult decodedResult = (WriteRequestResult) decoder.decode(null, null, resultBuffer);

            assertEquals("Decoded object should be equal.", result, decodedResult);
        }

    }

    /*
    @Test
    public void testCodecRead()
    {

        fail("Not yet implemented.");
    }

    @Test
    public void testCodecOrder()
    {

        fail("Not yet implemented.");
    }

    @Test
    public void testCodecCreateVolume()
    {

        fail("Not yet implemented.");
    }

    @Test
    public void testCodecDeleteVolume()
    {

        fail("Not yet implemented.");
    }

    @Test
    public void testCodecVolumeExists()
    {

        fail("Not yet implemented.");
    }
    */
}

