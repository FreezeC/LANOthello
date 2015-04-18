package frezc.lanothello.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import frezc.lanothello.app.game.Othello;
import frezc.lanothello.app.network.CheckRequest;
import frezc.lanothello.app.network.CheckResponse;

import java.io.IOException;


public class GameActivity extends ActionBarActivity {
    private RelativeLayout chessboard,layoutLoading;
    private OthelloView othelloView;

    private Othello othello;
    private int playerNO;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(GameActivity.this,"connect successfully(Server)", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(GameActivity.this, "connect successfully(Client)", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(GameActivity.this, "connection fail", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private String playerName = "";

    private Server server = null;
    private Client client = null;
    private Listener receiveListener = new Listener(){
        @Override
        public void received(Connection connection, Object object) {
            if(object instanceof CheckRequest){
                CheckRequest checkRequest = (CheckRequest) object;
                if(checkRequest.isPrepare){
                    handler.sendEmptyMessage(0);
                    CheckResponse response = new CheckResponse();
                    response.isResponse = true;
                    connection.sendTCP(response);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            startGame();
                        }
                    });

                }
            }else if (object instanceof CheckResponse){
                CheckResponse checkResponse = (CheckResponse) object;
                if(checkResponse.isResponse){
                    handler.sendEmptyMessage(1);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            startGame();
                        }
                    });

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        othelloView = (OthelloView) findViewById(R.id.othelloview);
        chessboard = (RelativeLayout) findViewById(R.id.chessboard);
        layoutLoading = (RelativeLayout) findViewById(R.id.layout_loading);

        playerName = getIntent().getStringExtra("name");

        if(getIntent().getBooleanExtra("isServer", true)){
            startServer();
            playerNO = 0;
        }else {
            playerNO = 1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startClient(getIntent().getStringExtra("hostIp"));
                }
            }).start();

        }
    }

    private void startClient(final String ip) {
        client = new Client();
        client.start();
        try {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GameActivity.this, ip, Toast.LENGTH_SHORT).show();
                }
            });
            client.connect(5000, ip, 54667, 45888);
        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(2);
            finish();
        }

        Kryo kryo = client.getKryo();
        kryo.register(CheckRequest.class);
        kryo.register(CheckResponse.class);

        client.addListener(receiveListener);
        CheckRequest checkRequest = new CheckRequest();
        checkRequest.isPrepare = true;
        client.sendTCP(checkRequest);
    }

    private void startServer() {
        server = new Server();
        server.start();
        try {
            server.bind(54667,45888);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"start Server fail:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }

        Kryo kryo = server.getKryo();
        kryo.register(CheckRequest.class);
        kryo.register(CheckResponse.class);

        server.addListener(receiveListener);
    }

    public void startGame(){

        layoutLoading.setVisibility(View.GONE);
        chessboard.setVisibility(View.VISIBLE);
    }

    public void stopGame(){
        layoutLoading.setVisibility(View.VISIBLE);
        chessboard.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(server != null){
            server.close();
        }
        if(client != null){
            client.close();
        }
    }
}
