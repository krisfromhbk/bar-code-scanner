package barcodescanner.redmi.my.barcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.esotericsoftware.kryonet.Client;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.ArrayList;

public class SendActivity extends AppCompatActivity {

    private static final String TAG = "SENDING LOG";

    private ArrayList<String> barcodes;
    private String login;
    private Client client;
    private NetworkTask networkTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        Intent sendActivityIntent = getIntent();
        barcodes = sendActivityIntent.getStringArrayListExtra("barcodes");
        Log.d(TAG, "Got barcodes from previous activity, size: " + barcodes.size());
        login = sendActivityIntent.getStringExtra("login");
        Log.d(TAG, "Got login from previous activity: " + login);

        Log.d(TAG, "Creating a client");
        client = new Client();
        client.getKryo().register(java.util.ArrayList.class);
    }

    private class NetworkTask extends AsyncTask<Void, Void, Void> {

        private static final String NET_TAG = "NETWORK LOG";

        private int tcpPort = 27960, udpPort = 27960;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "Starting client");
            client.start();
            try {
                Log.d(NET_TAG, "Connection to server");
                client.connect(5000, getServerIp(client), tcpPort, udpPort);
                Log.d(NET_TAG, "Successful connection!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(NET_TAG, "Adding login to the end of bercodes array");
            barcodes.add(login);
            Log.d(NET_TAG, "Sending barcodes ArrayList");
            client.sendTCP(barcodes);
            Log.d(NET_TAG, "Sending successful");
            Log.d(NET_TAG, "Removing login from the end");
            barcodes.remove(barcodes.size() - 1);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        private String getServerIp(Client client) {
            Log.d(NET_TAG, "Trying to find server");
            String ipRaw = String.valueOf(client.discoverHost(27960, 1000));
            Log.d(NET_TAG, "Server found on: " + ipRaw.substring(1, ipRaw.length()));
            return ipRaw.substring(1, ipRaw.length());
        }
    }
}
