package com.wei.socketdemo

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.wei.socketlib.WsManager
import com.wei.socketlib.WsStatusListener
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.ByteString
import java.util.concurrent.TimeUnit

/**
 * Created by wei on 2020/5/27.
 */
class SocketService : Service()  {

    var mBinder :LocalBinder? = null

    //心跳包发送时间计时
    private var sendTime: Long = 0L
    private val mHandler = Handler()

    // 每隔40秒发送一次心跳包，告诉服务器不要断开连接
    val HEART_BEAT_RATE = 40 * 1000.toLong()

    var heartBeatRunnable :Runnable = object :Runnable{
        override fun run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
                sendTime = System.currentTimeMillis();
//                var msg = OnMessage("1", "" , "", OnMessageData(null, "", 1, "heart", null)
                var msg = ""
                mWsBaseManager.sendMessage(msg.toString())
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE); //每隔一定的时间，对长连接进行一次心跳检测
        }

    }

    lateinit var mWsBaseManager : WsManager

    override fun onBind(p0: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        mBinder = LocalBinder(this)
        initSocket()
        initEvent()
    }

    open fun getSocketManager() : WsManager {
        return mWsBaseManager
    }

    private fun initEvent() {

    }

    private fun initSocket() {
        mWsBaseManager = WsManager.Builder(this)
                .client(
                        OkHttpClient().newBuilder()
                                .pingInterval(15, TimeUnit.SECONDS)
                                .retryOnConnectionFailure(true)
                                .build())
                .needReconnect(true)
                .wsUrl("ws:// url")
                .build();

        mWsBaseManager!!.setWsStatusListener(object : WsStatusListener {
            override fun onOpen(response: Response?) {
                Log.e("SocketService" , "onOpen" + response)
                mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE)
            }

            override fun onFailure(t: Throwable?, response: Response?) {
                Log.e("SocketService" , "onFailure" + response)
            }

            override fun onReconnect() {
                Log.e("SocketService" , "onReconnect")
            }

            override fun onClosing(code: Int, reason: String?) {
                Log.e("SocketService" , "onClosing " + reason)
            }


            override fun onMessage(text: String?) {
                Log.e("SocketService" , "onMessage " + text)
            }

            override fun onMessage(bytes: ByteString?) {
                Log.e("SocketService" , "onMessage " + bytes)
            }

            override fun onClosed(code: Int, reason: String?) {
                Log.e("SocketService" , "onClosed " + reason)
            }

        });
        mWsBaseManager!!.startConnect();

    }

    class LocalBinder(private val socketService: SocketService) : Binder() {

        fun getService(): SocketService? {
            return socketService
        }
    }
}