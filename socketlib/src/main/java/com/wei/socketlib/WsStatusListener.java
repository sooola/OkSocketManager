package com.wei.socketlib;

import okhttp3.Response;
import okio.ByteString;

/**
 * Created by wei on 2020/5/7.
 */
public interface WsStatusListener {

    public void onOpen(Response response);

    public void onMessage(String text) ;

    public void onMessage(ByteString bytes);

    public void onReconnect() ;

    public void onClosing(int code, String reason);

    public void onClosed(int code, String reason);

    public void onFailure(Throwable t, Response response);
}
