package com.anukool.websockettest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import okhttp3.*;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {
    private final String SERVER_URL = "wss://ws-feed.pro.coinbase.com";
    String TAG = "CoinWeb";
    private Button startTv;
    private TextView outputTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startTv = (Button) findViewById(R.id.start_tv);
        outputTv = (TextView) findViewById(R.id.output_tv);

        startTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCoinPrice("ETH-USD");
            }
        });
    }

    public void getCoinPrice(final String product) {
        OkHttpClient clientCoinPrice = new OkHttpClient();
        Request requestCoinPrice = new Request.Builder().url(SERVER_URL).build();

        WebSocketListener webSocketListenerCoinPrice = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.e(TAG, "onOpen");
                String request = "{\n" +
                        "    \"type\": \"subscribe\",\n" +
                        "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"" + product + "\"] }]\n" +
                        "}";
                webSocket.send(request);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                outputTv.setText(text);
                Log.e(TAG, "MESSAGE: " + text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                Log.e(TAG, "onMessage: " + bytes.hex());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                webSocket.cancel();
                Log.e(TAG, "onClosing: " + code + " " + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.e("onClosed", "");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e("onFailure", "");
            }


        };

        clientCoinPrice.newWebSocket(requestCoinPrice, webSocketListenerCoinPrice);
        clientCoinPrice.dispatcher().executorService().shutdown();
    }
}

