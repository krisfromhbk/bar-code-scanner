package barcodescanner.redmi.my.barcodescanner;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.esotericsoftware.kryonet.Client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN LOG";

    final Activity activity = this;
    private EditText code_edit;
    private ListView barcodes_listView;

    ArrayList<String> barcodes = new ArrayList<>();
    ArrayAdapter<String> barcodesAdapter;
    private Intent sendActivityIntent = new Intent(this, SendActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent mainActivityIntent = getIntent();
        Log.d("INTENT LOG", "logged as: " + mainActivityIntent.getStringExtra("login"));
        final String login = mainActivityIntent.getStringExtra("login");

        Button scan_button = (Button) findViewById(R.id.scan_button);
        code_edit = (EditText)findViewById(R.id.code_edit);
        Button add_button = (Button) findViewById(R.id.add_button);
        barcodes_listView = (ListView)findViewById(R.id.codes_list);
        Button remove_button = (Button) findViewById(R.id.remove_button);
        Button select_all_button = (Button) findViewById(R.id.select_all_button);
        Button generate_request_button = (Button) findViewById(R.id.generate_request_button);

        barcodesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, barcodes);
        barcodes_listView.setAdapter(barcodesAdapter);

        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.initiateScan();
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code_edit.getText().toString().matches("[0-9]{13}")) {
                    barcodes.add(0, code_edit.getText().toString());
                    barcodesAdapter.notifyDataSetChanged();
                    code_edit.setText("");
                } else {
                    Toast.makeText(activity, "Wrong code!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = barcodes.size() - 1; i >= 0; i--) {
                    if (barcodes_listView.isItemChecked(i)) barcodesAdapter.remove(barcodesAdapter.getItem(i));
                }
                barcodes_listView.clearChoices();
            }
        });

        select_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < barcodes.size(); i++) {
                    barcodes_listView.setItemChecked(i, true);
                }
            }
        });

        generate_request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActivityIntent.putExtra("login", login);
                sendActivityIntent.putExtra("barcodes", barcodes);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                //Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                //Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                code_edit.setText(result.getContents());

                if (code_edit.getText().toString().matches("[0-9]{13}")) {
                    barcodes.add(0, code_edit.getText().toString());
                    barcodesAdapter.notifyDataSetChanged();
                    code_edit.setText("");
                } else {
                    Toast.makeText(activity, "Wrong code!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
