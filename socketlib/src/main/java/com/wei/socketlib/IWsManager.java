package com.wei.socketlib;

import okhttp3.WebSocket;
import okio.ByteString;

/**
 * Created by wei on 2020/5/8.
 */
public interface IWsManager {

    WebSocket getWebSocket();

    void startConnect();

    void stopConnect();

    boolean isWsConnected();

    int getCurrentStatus();

    void setCurrentStatus(int currentStatus);

    boolean sendMessage(String msg);

    boolean sendMessage(ByteString byteString);
}
