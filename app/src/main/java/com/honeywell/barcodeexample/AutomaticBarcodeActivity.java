package com.honeywell.barcodeexample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.ActivityInfo;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.honeywell.aidc.*;

import org.w3c.dom.Text;

public class AutomaticBarcodeActivity extends Activity implements BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener{

    private com.honeywell.aidc.BarcodeReader barcodeReader;
    private ListView barcodeList;
    private Button homeButton;
    private Button settingsButton;
    private ArrayList<ArrayList<String>> scannedData;
    private ArrayList<String> scannedItems;

    private int currCount;
    private TextView counter;
    private TextView timer;
    private int mode;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = getIntent().getIntExtra("mode", 0);
        scannedData = new ArrayList<>();
        scannedItems = new ArrayList<>();
        currCount = 0;
        setContentView(R.layout.scan_screen);
        counter = (TextView) findViewById(R.id.counter);
        timer = (TextView) findViewById(R.id.timer);

        if (Build.MODEL.startsWith("VM1A")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // get bar code instance from MainActivity
        barcodeReader = MainActivity.getBarcodeObject();

        if (barcodeReader != null) {

            // register bar code event listener
            barcodeReader.addBarcodeListener(this);

            // set the trigger mode to client control
            try {//TODO get value passed with activity and determine what trigger mode should be used.
                if (mode == 1) {
                    //This is the default scanner behavior. Pull the trigger to start scanning, release trigger to stop.
                    barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE, BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL);
                } else {
                    //Scanner trigger mode will behave exactly as we specify.
                    barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE, BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL);
                }
            } catch (UnsupportedPropertyException e) {
                Toast.makeText(this, "Failed to apply properties", Toast.LENGTH_SHORT).show();
            }
            // register trigger state change listener
            barcodeReader.addTriggerListener(this);

            Map<String, Object> properties = new HashMap<String, Object>();
            // Set Symbologies On/Off
            properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
            properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, false);
            // Set Max Code 39 barcode length
            properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
            // Turn on center decoding
            properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
            // Enable bad read response
            properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, true);
            // Sets time period for decoder timeout in any mode
            properties.put(BarcodeReader.PROPERTY_DECODER_TIMEOUT, 400);

            //Set the scanning mode to continuous if needed
            //TODO if else statement here for continous scanning
            if (mode == 0) {
                properties.put(BarcodeReader.PROPERTY_TRIGGER_SCAN_MODE, BarcodeReader.TRIGGER_SCAN_MODE_CONTINUOUS);
            }


            // Apply the settings
            barcodeReader.setProperties(properties);
        }

        // get initial list
        barcodeList = (ListView) findViewById(R.id.listViewBarcodeData);
        ActivitySetting();
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                // update UI to reflect the data
                ArrayList<String> list = new ArrayList<String>();
                list.add("Barcode data: " + event.getBarcodeData());
                list.add("Character Set: " + event.getCharset());
                list.add("Code ID: " + event.getCodeId());
                list.add("AIM ID: " + event.getAimId());
                list.add("Timestamp: " + event.getTimestamp());

                scannedData.add(list);
                scannedItems.add("Barcode " + scannedData.size());
                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AutomaticBarcodeActivity.this, android.R.layout.simple_list_item_1, scannedItems);
                barcodeList.setAdapter(dataAdapter);
                currCount = scannedItems.size();
                counter.setText("COUNT: " + currCount);
                if (mode == 1) {
                    try {
                        barcodeReader.aim(false);
                        barcodeReader.light(false);
                        barcodeReader.decode(false);
                    } catch (ScannerUnavailableException e) {
                        e.printStackTrace();
                        //Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
                    } catch (ScannerNotClaimedException e) {
                        //throw new RuntimeException(e);
                        e.printStackTrace();
                        //Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // When using Automatic Trigger control do not need to implement the
    // onTriggerEvent function
    @Override
    public void onTriggerEvent(TriggerStateChangeEvent event) {
        if (mode == 0) {
            if (event.getState()) {
                try {
                    //This will start continuous scanning
                    barcodeReader.aim(true);
                    barcodeReader.light(true);
                    barcodeReader.decode(true);
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
                } catch (ScannerNotClaimedException e) {
                    //throw new RuntimeException(e);
                    e.printStackTrace();
                    Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Add these lines to stop scanning when the application is paused
                try {
                    //This will stop scanning in continuous mode
                    barcodeReader.aim(false);
                    barcodeReader.light(false);
                    barcodeReader.decode(false);
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
                } catch (ScannerNotClaimedException e) {
                    //throw new RuntimeException(e);
                    e.printStackTrace();
                    Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeReader != null) {
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeReader != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            barcodeReader.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (barcodeReader != null) {
            // unregister barcode event listener
            barcodeReader.removeBarcodeListener(this);

            // unregister trigger state change listener
            barcodeReader.removeTriggerListener(this);
        }
    }

    public void ActivitySetting() {
        homeButton = (Button) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
                finish();
            }
        });

        barcodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent("android.intent.action.ANALYSIS");
                intent.putStringArrayListExtra("data", scannedData.get(position));
                startActivity(intent);
            }
        });

        settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent intent = new Intent("android.intent.action.SETTINGS");
                intent.putExtra("frag", mode);
                startActivity(intent);
            }
        });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getBooleanExtra("time", false))
                {
                    timer.setVisibility(View.VISIBLE);
                }
            }
        };
    }
}
/*
if (((Global) getApplication()).isTimerOn()) {
                timer.setVisibility(View.VISIBLE);
            } else {
                timer.setVisibility(View.INVISIBLE);
            }
 */
