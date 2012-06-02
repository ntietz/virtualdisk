package com.virtualdisk.network;

public interface Sendable
{
    byte messageType();

    final byte orderRequestResult = 0;
    final byte readRequestResult = 1;
    final byte writeRequestResult = 2;
    final byte volumeExistsRequestResult = 6;
    final byte createVolumeRequestResult = 7;
    final byte deleteVolumeRequestResult = 8;

    final byte orderRequest = 3;
    final byte readRequest = 4;
    final byte writeRequest = 5;
    final byte volumeExistsRequest = 9;
    final byte createVolumeRequest = 10;
    final byte deleteVolumeRequest = 11;
}

