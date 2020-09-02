# OkSocketManager
okhttp socket 封装
修改自 https://github.com/Rabtman/WsManager   
更新至androidX，增加Service例子，修复断网后，不能断线重连问题  

运行环境Android Studio 3.6.3 ,build gradle 3.5.3  

## 引入  
1.在根目录的build.gradle 
```
allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
```
2.在项目build.gradle 
```
implementation 'com.github.sooola:OkSocketManager:2.0'
```
## How to use

Instantiate a WsManager object:

```
OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                      .pingInterval(15, TimeUnit.SECONDS)
                      .retryOnConnectionFailure(true)
                      .build();
WsManager wsManager = new WsManager.Builder(this)
                .wsUrl("ws://localhost:2333/")
                .needReconnect(true)
                .client(okHttpClient)
                .build();
```

Establish a connection with the server:

```
wsManager.startConnect();
```

Listens for server connection status:

```
wsManager.setWsStatusListener(new WsStatusListener() {
            @Override
            public void onOpen(Response response) {
                super.onOpen(response);
            }

            @Override
            public void onMessage(String text) {
                super.onMessage(text);
            }

            @Override
            public void onMessage(ByteString bytes) {
                super.onMessage(bytes);
            }

            @Override
            public void onReconnect() {
                super.onReconnect();
            }

            @Override
            public void onClosing(int code, String reason) {
                super.onClosing(code, reason);
            }

            @Override
            public void onClosed(int code, String reason) {
                super.onClosed(code, reason);
            }

            @Override
            public void onFailure(Throwable t, Response response) {
                super.onFailure(t, response);
            }
        });
```

Send message to the server:

```
//String msg or ByteString byteString
wsManager.sendMessage();
```

Close the connection to the server:

```
wsManager.stopConnect();
```

## Preview

![](https://github.com/Rabtman/WsManager/raw/master/screenshots/ws.gif)

## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
