package frezc.lanothello.app;

import android.util.Log;
import com.esotericsoftware.kryonet.Client;

import java.net.InetAddress;

/**
 * Created by freeze on 2015/4/18.
 */
public class Searcher extends Thread{

    private boolean searching;
    private OnSearchListener onSearchListener;

    public interface OnSearchListener{
        void onFound(InetAddress address);
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    public void stopSearch(){
        searching = false;
    }

    @Override
    public void run() {
        searching = true;
        Client searcher = new Client();
        while (searching){
            InetAddress address = searcher.discoverHost(45888, 5000);
            Log.i("test","timeout");
            if(onSearchListener != null){
                onSearchListener.onFound(address);
                searching = false;
            }
        }
    }
}