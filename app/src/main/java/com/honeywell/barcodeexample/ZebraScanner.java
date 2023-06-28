package com.honeywell.barcodeexample;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ZebraScanner extends Activity {
    //region var
    // DataWedge Sample supporting DataWedge APIs up to DW 7.0

    private static final String EXTRA_PROFILENAME = "DWDataCapture1";

    // DataWedge Extras
    private static final String EXTRA_GET_VERSION_INFO = "com.symbol.datawedge.api.GET_VERSION_INFO";
    private static final String EXTRA_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE";
    private static final String EXTRA_KEY_APPLICATION_NAME = "com.symbol.datawedge.api.APPLICATION_NAME";
    private static final String EXTRA_KEY_NOTIFICATION_TYPE = "com.symbol.datawedge.api.NOTIFICATION_TYPE";
    private static final String EXTRA_SOFT_SCAN_TRIGGER = "com.symbol.datawedge.api.SOFT_SCAN_TRIGGER";
    private static final String EXTRA_RESULT_NOTIFICATION = "com.symbol.datawedge.api.NOTIFICATION";
    private static final String EXTRA_REGISTER_NOTIFICATION = "com.symbol.datawedge.api.REGISTER_FOR_NOTIFICATION";
    private static final String EXTRA_UNREGISTER_NOTIFICATION = "com.symbol.datawedge.api.UNREGISTER_FOR_NOTIFICATION";
    private static final String EXTRA_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG";

    private static final String EXTRA_RESULT_NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
    private static final String EXTRA_KEY_VALUE_SCANNER_STATUS = "SCANNER_STATUS";
    private static final String EXTRA_KEY_VALUE_PROFILE_SWITCH = "PROFILE_SWITCH";
    private static final String EXTRA_KEY_VALUE_CONFIGURATION_UPDATE = "CONFIGURATION_UPDATE";
    private static final String EXTRA_KEY_VALUE_NOTIFICATION_STATUS = "STATUS";
    private static final String EXTRA_KEY_VALUE_NOTIFICATION_PROFILE_NAME = "PROFILE_NAME";
    private static final String EXTRA_SEND_RESULT = "SEND_RESULT";

    private static final String EXTRA_EMPTY = "";

    private static final String EXTRA_RESULT_GET_VERSION_INFO = "com.symbol.datawedge.api.RESULT_GET_VERSION_INFO";
    private static final String EXTRA_RESULT = "RESULT";
    private static final String EXTRA_RESULT_INFO = "RESULT_INFO";
    private static final String EXTRA_COMMAND = "COMMAND";

    // DataWedge Actions
    private static final String ACTION_DATAWEDGE = "com.symbol.datawedge.api.ACTION";
    private static final String ACTION_RESULT_NOTIFICATION = "com.symbol.datawedge.api.NOTIFICATION_ACTION";
    private static final String ACTION_RESULT = "com.symbol.datawedge.api.RESULT_ACTION";

    // private variables
    private Boolean bRequestSendResult = false;
    final String LOG_TAG = "DataCapture1";
    //endregion
    //region variables
    private com.honeywell.aidc.BarcodeReader barcodeReader;
    private ListView barcodeList;
    private Button homeButton;
    private Button settingsButton;
    private ArrayList<ArrayList<String>> scannedData;
    private ArrayList<String> scannedItems;

    private int currCount;
    private int maxCount;
    private TextView counter;
    private TextView timer;
    private int mode;
    private int startTime;
    private Timer myTimer;
    private boolean isTimerOn;
    private boolean soundEnabled;
    private boolean scanOn;

    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("Settings"));
        //setContentView(R.layout.zebra_scan);
        soundEnabled = true;
        mode = getIntent().getIntExtra("mode", 0);
        scannedData = new ArrayList<>();
        scannedItems = new ArrayList<>();
        currCount = 0;
        maxCount = -1;
        startTime = -1;
        isTimerOn = false;
        scanOn = false;
        setContentView(R.layout.scan_screen);
        counter = (TextView) findViewById(R.id.counter);
        timer = (TextView) findViewById(R.id.timer);
        barcodeList = (ListView) findViewById(R.id.listViewBarcodeData);
        // Check selected decoders
        // Use SET_CONFIG: http://techdocs.zebra.com/datawedge/latest/guide/api/setconfig/
        /*final Button btnSetDecoders = (Button) findViewById(R.id.btnSetDecoders);
        btnSetDecoders.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                final CheckBox checkCode128 = (CheckBox) findViewById(R.id.chkCode128);
                String Code128Value = setDecoder(checkCode128);
                final CheckBox checkCode39 = (CheckBox) findViewById(R.id.chkCode39);
                String Code39Value = setDecoder(checkCode39);

                final CheckBox checkEAN13 = (CheckBox) findViewById(R.id.chkEAN13);
                String EAN13Value = setDecoder(checkEAN13);

                final CheckBox checkUPCA = (CheckBox) findViewById(R.id.chkUPCA);
                String UPCAValue = setDecoder(checkUPCA);

                // Main bundle properties
                Bundle profileConfig = new Bundle();
                profileConfig.putString("PROFILE_NAME", EXTRA_PROFILENAME);
                profileConfig.putString("PROFILE_ENABLED", "true");
                profileConfig.putString("CONFIG_MODE", "UPDATE");  // Update specified settings in profile

                // PLUGIN_CONFIG bundle properties
                Bundle barcodeConfig = new Bundle();
                barcodeConfig.putString("PLUGIN_NAME", "BARCODE");
                barcodeConfig.putString("RESET_CONFIG", "true");

                // PARAM_LIST bundle properties
                Bundle barcodeProps = new Bundle();
                barcodeProps.putString("scanner_selection", "auto");
                barcodeProps.putString("scanner_input_enabled", "true");
                barcodeProps.putString("decoder_code128", Code128Value);
                barcodeProps.putString("decoder_code39", Code39Value);
                barcodeProps.putString("decoder_ean13", EAN13Value);
                barcodeProps.putString("decoder_upca", UPCAValue);

                // Bundle "barcodeProps" within bundle "barcodeConfig"
                barcodeConfig.putBundle("PARAM_LIST", barcodeProps);
                // Place "barcodeConfig" bundle within main "profileConfig" bundle
                profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig);

                // Create APP_LIST bundle to associate app with profile
                Bundle appConfig = new Bundle();
                appConfig.putString("PACKAGE_NAME", getPackageName());
                appConfig.putStringArray("ACTIVITY_LIST", new String[]{"*"});
                profileConfig.putParcelableArray("APP_LIST", new Bundle[]{appConfig});
                sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);
                Toast.makeText(getApplicationContext(), "In profile " + EXTRA_PROFILENAME + " the selected decoders are being set: \nCode128=" + Code128Value + "\nCode39="
                        + Code39Value + "\nEAN13=" + EAN13Value + "\nUPCA=" + UPCAValue, Toast.LENGTH_LONG).show();

            }
        });*/

        // Register for status change notification
        // Use REGISTER_FOR_NOTIFICATION: http://techdocs.zebra.com/datawedge/latest/guide/api/registerfornotification/
        Bundle b = new Bundle();
        b.putString(EXTRA_KEY_APPLICATION_NAME, getPackageName());
        b.putString(EXTRA_KEY_NOTIFICATION_TYPE, "SCANNER_STATUS");     // register for changes in scanner status
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE, EXTRA_REGISTER_NOTIFICATION, b);

        registerReceivers();

        // Get DataWedge version
        // Use GET_VERSION_INFO: http://techdocs.zebra.com/datawedge/latest/guide/api/getversioninfo/
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE, EXTRA_GET_VERSION_INFO, EXTRA_EMPTY);    // must be called after registering BroadcastReceiver
        if(mode==0){
            timer.setVisibility(View.VISIBLE);
        }
        ActivitySetting();
    }

    // Create profile from UI onClick() event
    public void CreateProfile (View view){
        String profileName = EXTRA_PROFILENAME;

        // Send DataWedge intent with extra to create profile
        // Use CREATE_PROFILE: http://techdocs.zebra.com/datawedge/latest/guide/api/createprofile/
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE, EXTRA_CREATE_PROFILE, profileName);

        // Configure created profile to apply to this app
        Bundle profileConfig = new Bundle();
        profileConfig.putString("PROFILE_NAME", EXTRA_PROFILENAME);
        profileConfig.putString("PROFILE_ENABLED", "true");
        profileConfig.putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST");  // Create profile if it does not exist

        // Configure barcode input plugin
        Bundle barcodeConfig = new Bundle();
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE");
        barcodeConfig.putString("RESET_CONFIG", "true"); //  This is the default
        Bundle barcodeProps = new Bundle();
        barcodeConfig.putBundle("PARAM_LIST", barcodeProps);
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig);

        // Associate profile with this app
        Bundle appConfig = new Bundle();
        appConfig.putString("PACKAGE_NAME", getPackageName());
        appConfig.putStringArray("ACTIVITY_LIST", new String[]{"*"});
        profileConfig.putParcelableArray("APP_LIST", new Bundle[]{appConfig});
        profileConfig.remove("PLUGIN_CONFIG");

        // Apply configs
        // Use SET_CONFIG: http://techdocs.zebra.com/datawedge/latest/guide/api/setconfig/
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);

        // Configure intent output for captured data to be sent to this app
        Bundle intentConfig = new Bundle();
        intentConfig.putString("PLUGIN_NAME", "INTENT");
        intentConfig.putString("RESET_CONFIG", "true");
        Bundle intentProps = new Bundle();
        intentProps.putString("intent_output_enabled", "true");
        intentProps.putString("intent_action", "com.zebra.datacapture1.ACTION");
        intentProps.putString("intent_delivery", "2");
        intentConfig.putBundle("PARAM_LIST", intentProps);
        profileConfig.putBundle("PLUGIN_CONFIG", intentConfig);
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);

        Toast.makeText(getApplicationContext(), "Created profile.  Check DataWedge app UI.", Toast.LENGTH_LONG).show();
    }

    // Toggle soft scan trigger from UI onClick() event
    // Use SOFT_SCAN_TRIGGER: http://techdocs.zebra.com/datawedge/latest/guide/api/softscantrigger/
    public void ToggleSoftScanTrigger (View view){
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE, EXTRA_SOFT_SCAN_TRIGGER, "TOGGLE_SCANNING");
    }

    // Create filter for the broadcast intent
    private void registerReceivers() {

        Log.d(LOG_TAG, "registerReceivers()");

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RESULT_NOTIFICATION);   // for notification result
        filter.addAction(ACTION_RESULT);                // for error code result
        filter.addCategory(Intent.CATEGORY_DEFAULT);    // needed to get version info

        // register to received broadcasts via DataWedge scanning
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        filter.addAction(getResources().getString(R.string.activity_action_from_service));
        registerReceiver(myBroadcastReceiver, filter);
    }

    // Unregister scanner status notification
    public void unRegisterScannerStatus() {
        Log.d(LOG_TAG, "unRegisterScannerStatus()");
        Bundle b = new Bundle();
        b.putString(EXTRA_KEY_APPLICATION_NAME, getPackageName());
        b.putString(EXTRA_KEY_NOTIFICATION_TYPE, EXTRA_KEY_VALUE_SCANNER_STATUS);
        Intent i = new Intent();
        i.setAction(ACTION);
        i.putExtra(EXTRA_UNREGISTER_NOTIFICATION, b);
        this.sendBroadcast(i);
    }

    public String setDecoder (CheckBox decoder)
    {
        boolean checkValue = decoder.isChecked();
        String value = "false";
        if (checkValue)
        {
            value = "true";
            return value;
        }
        else
            return value;
    }

//region Broadcast
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();

            Log.d(LOG_TAG, "DataWedge Action:" + action);

            // Get DataWedge version info
            if (intent.hasExtra(EXTRA_RESULT_GET_VERSION_INFO))
            {
                Bundle versionInfo = intent.getBundleExtra(EXTRA_RESULT_GET_VERSION_INFO);
                String DWVersion = versionInfo.getString("DATAWEDGE");

               // TextView txtDWVersion = (TextView) findViewById(R.id.txtGetDWVersion);
                //txtDWVersion.setText(DWVersion);
                Log.i(LOG_TAG, "DataWedge Version: " + DWVersion);
            }

            if (action.equals(getResources().getString(R.string.activity_intent_filter_action)))
            {
                //  Received a barcode scan
                try
                {
                    displayScanResult(intent, "via Broadcast");
                }
                catch (Exception e)
                {
                    //  Catch error if the UI does not exist when we receive the broadcast...
                }
            }

            else if (action.equals(ACTION_RESULT))
            {
                // Register to receive the result code
                if ((intent.hasExtra(EXTRA_RESULT)) && (intent.hasExtra(EXTRA_COMMAND)))
                {
                    String command = intent.getStringExtra(EXTRA_COMMAND);
                    String result = intent.getStringExtra(EXTRA_RESULT);
                    String info = "";

                    if (intent.hasExtra(EXTRA_RESULT_INFO))
                    {
                        Bundle result_info = intent.getBundleExtra(EXTRA_RESULT_INFO);
                        Set<String> keys = result_info.keySet();
                        for (String key : keys) {
                            Object object = result_info.get(key);
                            if (object instanceof String) {
                                info += key + ": " + object + "\n";
                            } else if (object instanceof String[]) {
                                String[] codes = (String[]) object;
                                for (String code : codes) {
                                    info += key + ": " + code + "\n";
                                }
                            }
                        }
                        Log.d(LOG_TAG, "Command: "+command+"\n" +
                                "Result: " +result+"\n" +
                                "Result Info: " + info + "\n");
                        Toast.makeText(getApplicationContext(), "Error Resulted. Command:" + command + "\nResult: " + result + "\nResult Info: " +info, Toast.LENGTH_LONG).show();
                    }
                }

            }

            // Register for scanner change notification
            else if (action.equals(ACTION_RESULT_NOTIFICATION))
            {
                if (intent.hasExtra(EXTRA_RESULT_NOTIFICATION))
                {
                    Bundle extras = intent.getBundleExtra(EXTRA_RESULT_NOTIFICATION);
                    String notificationType = extras.getString(EXTRA_RESULT_NOTIFICATION_TYPE);
                    if (notificationType != null)
                    {
                        switch (notificationType) {
                            case EXTRA_KEY_VALUE_SCANNER_STATUS:
                                // Change in scanner status occurred
                                String displayScannerStatusText = extras.getString(EXTRA_KEY_VALUE_NOTIFICATION_STATUS) +
                                        ", profile: " + extras.getString(EXTRA_KEY_VALUE_NOTIFICATION_PROFILE_NAME);
                                //Toast.makeText(getApplicationContext(), displayScannerStatusText, Toast.LENGTH_SHORT).show();
                                //final TextView lblScannerStatus = (TextView) findViewById(R.id.lblScannerStatus);
                               // lblScannerStatus.setText(displayScannerStatusText);
                                Log.i(LOG_TAG, "Scanner status: " + displayScannerStatusText);
                                break;

                            case EXTRA_KEY_VALUE_PROFILE_SWITCH:
                                // Received change in profile
                                // For future enhancement
                                break;

                            case  EXTRA_KEY_VALUE_CONFIGURATION_UPDATE:
                                // Configuration change occurred
                                // For future enhancement
                                break;
                        }
                    }
                }
            }
        }
    };
//endregion
    private void displayScanResult(Intent initiatingIntent, String howDataReceived)
    {
        MediaPlayer.create(getApplicationContext(), R.raw.sonic_sound).start();
        // store decoded data
        String decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        // store decoder type

        String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));

        ArrayList<String> list = new ArrayList<String>();
        list.add(decodedData);
        list.add(decodedLabelType);
        scannedData.add(list);
        scannedItems.add(0, "" + scannedData.size() + ".) " + decodedData);
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ZebraScanner.this, android.R.layout.simple_list_item_1, scannedItems);
        barcodeList.setAdapter(dataAdapter);
        currCount = scannedItems.size();
        setCounter();
    }

    private void sendDataWedgeIntentWithExtra(String action, String extraKey, Bundle extras)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extras);
        if (bRequestSendResult)
            dwIntent.putExtra(EXTRA_SEND_RESULT, "true");
        this.sendBroadcast(dwIntent);
    }

    private void sendDataWedgeIntentWithExtra(String action, String extraKey, String extraValue)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extraValue);
        if (bRequestSendResult)
            dwIntent.putExtra(EXTRA_SEND_RESULT, "true");
        this.sendBroadcast(dwIntent);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceivers();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(myBroadcastReceiver);
        unRegisterScannerStatus();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }
    private void setCounter() {
        if (maxCount != 0) {
            counter.setText("COUNT: " + currCount + "/" + maxCount);
        } else {
            counter.setText("COUNT: " + currCount);
        }
    }

    private String getCurrTime() {
        String time = "";
        int sec = ((int) (System.currentTimeMillis() / 1000)) - startTime;
        if (sec > 60) {
            int min = sec / 60;
            sec = sec % 60;
            time += min + "min " + sec + "s";
        } else {
            time += sec + "s";
        }
        return time;
    }

    private void startTimer() {
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ZebraScanner.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (maxCount > 0 && currCount >= maxCount) {
                            isTimerOn = false;
                            myTimer.cancel();
                            return;
                        }
                        timer.setText("TIME: " + getCurrTime());
                    }
                });
            }
        }, 1000, 1000);
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
                intent.putExtra("timer", timer.getVisibility() == View.VISIBLE);
                intent.putExtra("countAmnt", maxCount);
                intent.putExtra("sound", soundEnabled);
                startActivity(intent);
            }
        });


    }

    //region BroadcastReceiver from settings
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("time", false)) {
                timer.setVisibility(View.VISIBLE);
            } else {
                timer.setVisibility(View.INVISIBLE);
                startTime = -1;
                timer.setText("TIME: " + 0 + "s");
            }
            maxCount = intent.getIntExtra("count", 0);
            if (maxCount != -1) {
                counter.setVisibility(View.VISIBLE);
                setCounter();
            } else {
                counter.setVisibility(View.INVISIBLE);
            }
            soundEnabled = intent.getBooleanExtra("sound", true);
        }
    };
    IntentFilter filter = new IntentFilter();

    //endregion
}
