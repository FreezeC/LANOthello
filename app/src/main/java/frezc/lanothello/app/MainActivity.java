package frezc.lanothello.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import frezc.lanothello.app.network.CheckRequest;
import frezc.lanothello.app.network.CheckResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class MainActivity extends ActionBarActivity
        implements View.OnClickListener,DialogInterface.OnCancelListener,Searcher.OnSearchListener,
        SearcherDialog.OnClickListenr{
    private String name = "Default";
    private EditText editText;
    private EditText etIp;
    private ProgressDialog loadingDialog = null;
    private Searcher searcher = null;
    private SearcherDialog resultDialog = null;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(MainActivity.this,"Time Out",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(MainActivity.this, "connect successfully(Test)", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("isServer", false);
                    intent.putExtra("serverIp", etIp.getText().toString());
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_server).setOnClickListener(this);
        findViewById(R.id.btn_searcher).setOnClickListener(this);
        findViewById(R.id.btn_dc).setOnClickListener(this);
        findViewById(R.id.btn_local).setOnClickListener(this);
        editText = (EditText) findViewById(R.id.et_name);
        etIp = (EditText) findViewById(R.id.et_ipaddress);
    }

    /**
     * button click event
     * @param v
     */
    @Override
    public void onClick(View v) {
        if(!checkAvailableName()){
            return;
        }
        name = editText.getText().toString();
        switch (v.getId()){
            case R.id.btn_server:
                Intent intent = new Intent(this, GameActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("isServer", true);
                startActivity(intent);
                break;
            case R.id.btn_searcher:
                showLoadingDialog();
                searcher = new Searcher();
                searcher.start();
                break;
            case R.id.btn_dc:
                showLoadingDialog();
//                ping();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final InetAddress inetAddress = InetAddress.getByName(etIp.getText().toString());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, inetAddress.toString(),Toast.LENGTH_SHORT).show();
                                    onFound(inetAddress);
                                }
                            });
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                            Log.e("test",e.getMessage());
                        }
                    }
                }).start();
                break;
            case R.id.btn_local:

                break;
        }
    }

    private void ping() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Client client = new Client();
                client.start();
                try {
                    client.connect(5000, etIp.getText().toString(), 54667, 45888);
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(0);

                }
                Kryo kryo = client.getKryo();
                kryo.register(CheckRequest.class);
                kryo.register(CheckResponse.class);
                client.addListener(new Listener() {
                    @Override
                    public void received(Connection connection, Object object) {
                        if (object instanceof CheckResponse) {
                            CheckResponse checkResponse = (CheckResponse) object;
                            if(checkResponse.isResponse){
                                handler.sendEmptyMessage(1);
                            }
                        }
                    }
                });
                CheckRequest checkRequest = new CheckRequest();
                checkRequest.isPrepare = true;
                client.sendTCP(checkRequest);
            }
        }).start();


    }

    /**
     * show loading dialog
     */
    private void showLoadingDialog() {
        if(loadingDialog == null) {
            loadingDialog = new ProgressDialog(this);
            loadingDialog.setTitle("search server");
            loadingDialog.setMessage("Waiting for Server");
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingDialog.setCancelable(true);
            loadingDialog.setCanceledOnTouchOutside(true);
            loadingDialog.setOnCancelListener(this);
        }
        loadingDialog.show();

    }

    /**
     * check name
     * @return
     */
    private boolean checkAvailableName() {
        String s = editText.getText().toString();
        if(s.length() > 0 && s.length() < 10){
            name = s;
            return true;
        }else {
            Toast.makeText(this,"Your name length cannot be empty and exceed 9",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if(resultDialog != null){
            resultDialog.dismiss();
        }
    }

    /**
     * cancel loading dialog
     * @param dialog
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        if(searcher != null) {
            searcher.stopSearch();
        }
    }

    /**
     * Found server
     * @param address
     */
    @Override
    public void onFound(InetAddress address) {
        loadingDialog.hide();
        if(resultDialog == null){
            resultDialog = new SearcherDialog();
            resultDialog.setOnClickListenr(this);
        }
        Bundle bundle = new Bundle();
        bundle.putString("hostIp", address.getHostAddress());
        resultDialog.setArguments(bundle);
        resultDialog.show(getSupportFragmentManager(), "server");
    }


    /**
     * searcher result dialog positive button click event
     */
    @Override
    public void onPositiveClick() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("isServer", false);
        intent.putExtra("hostIp", etIp.getText().toString());
        startActivity(intent);
    }

    /**
     * negative button click event
     */
    @Override
    public void onNegativeClick() {

    }
}
