package com.virtualdisk.network;

public interface Sendable
{
    byte messageType();

    final byte orderRequestResult = 0;
    final byte readRequestResult = 1;
    final byte writeRequestResult = 2;

    final byte orderRequest = 3;
    final byte readRequest = 4;
    final byte writeRequest = 5;
}

