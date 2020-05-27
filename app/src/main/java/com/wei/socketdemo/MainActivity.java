package com.wei.socketdemo;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.wei.socketlib.WsManager;
import com.wei.socketlib.WsStatusListener;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    private WsManager wsManager;
    private TextView tv_content;
    private static String TAG = "MainActivity";

    private Conn mConn = new Conn();

    private WsStatusListener wsStatusListener = new WsStatusListener() {
        @Override
        public void onOpen(Response response) {
            tv_content.append(Spanny.spanText("服务器连接成功\n\n", new ForegroundColorSpan(
                    ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))));
        }

        @Override
        public void onMessage(String text) {
            Log.d(TAG, "WsManager-----onMessage");
            tv_content.append(Spanny
                    .spanText("服务器 " + DateUtils.formatDateTime(getBaseContext(), System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME) + "\n",
                            new ForegroundColorSpan(
                                    ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))));
            tv_content.append(fromHtmlText(text) + "\n\n");
        }

        @Override
        public void onMessage(ByteString bytes) {
            Log.d(TAG, "WsManager-----onMessage");
        }

        @Override
        public void onReconnect() {
            Log.d(TAG, "WsManager-----onReconnect");
            tv_content.append(Spanny.spanText("服务器重连接中...\n", new ForegroundColorSpan(
                    ContextCompat.getColor(getBaseContext(), android.R.color.holo_red_light))));
        }

        @Override
        public void onClosing(int code, String reason) {
            Log.d(TAG, "WsManager-----onClosing");
            tv_content.append(Spanny.spanText("服务器连接关闭中...\n", new ForegroundColorSpan(
                    ContextCompat.getColor(getBaseContext(), android.R.color.holo_red_light))));
        }

        @Override
        public void onClosed(int code, String reason) {
            Log.d(TAG, "WsManager-----onClosed");
            tv_content.append(Spanny.spanText("服务器连接已关闭\n", new ForegroundColorSpan(
                    ContextCompat.getColor(getBaseContext(), android.R.color.holo_red_light))));
        }

        @Override
        public void onFailure(Throwable t, Response response) {
            Log.d(TAG, "WsManager-----onFailure");
            Log.d(TAG, "WsManager-----onFailure" + response);
            Log.d(TAG, "WsManager-----onFailure" + t);
            tv_content.append(Spanny.spanText("服务器连接失败\n", new ForegroundColorSpan(
                    ContextCompat.getColor(getBaseContext(), android.R.color.holo_red_light))));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_content = findViewById(R.id.tv_content);

        //开启后台socket方式启动
//        Intent intent = new Intent(this, SocketService.class);
//        bindService(intent, mConn, Context.BIND_AUTO_CREATE);

        findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wsUrl = "wss:// url";

                wsManager = new WsManager.Builder(getBaseContext())
                        .client(new OkHttpClient().newBuilder()
                                        .pingInterval(15, TimeUnit.SECONDS)
                                        .retryOnConnectionFailure(true)
                                        .build())
                        .needReconnect(true)
                        .wsUrl(wsUrl)
                        .build();
                wsManager.setWsStatusListener(wsStatusListener);
                wsManager.startConnect();
            }
        });

        findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wsManager != null){
                    wsManager.stopConnect();
                    wsManager = null;
                }
            }
        });
    }

    class Conn implements ServiceConnection {

        WsManager serviceMessenger;

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            SocketService.LocalBinder binder = (SocketService.LocalBinder)iBinder;
            SocketService socketService = binder.getService();
            if (socketService != null) {
                serviceMessenger = socketService.getSocketManager();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }



    private Spanned fromHtmlText(String s) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(s);
        }
        return result;
    }
}
