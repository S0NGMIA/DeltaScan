package com.honeywell.barcodeexample;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SettingsActivity extends Activity {
    private Button backButton;
    private CheckBox timer;
    private CheckBox counter;
    private CheckBox sound;
    private TextView counterText;
    private EditText counterInput;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);
        timer = (CheckBox) findViewById(R.id.timer);
        sound = (CheckBox) findViewById(R.id.sound);
        counter = (CheckBox) findViewById(R.id.counter);
        counterText = (TextView) findViewById(R.id.amntScan);
        counterInput = (EditText) findViewById(R.id.amntScanInput);
        if(getIntent().getIntExtra("frag", 0)==0)
        {
            counter.setVisibility(View.VISIBLE);
        }
        ActivitySetting();
    }

    public void ActivitySetting() {
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*((Global) getApplication()).setTimer(timer.isChecked());
                if(counter.isChecked() && !counterInput.getText().toString().isEmpty()){
                    ((Global) getApplication()).setMaxCount(Integer.parseInt(counterInput.getText().toString()));
                }
                Intent intent = new Intent("android.intent.action.SETTINGSSET");
                sendBroadcast(intent);*/
                Intent intent = new Intent("Settings");
                intent.putExtra("time", timer.isChecked());
                if(counter.isChecked() && !counterInput.getText().toString().isEmpty()){
                    intent.putExtra("count", Integer.parseInt(counterInput.getText().toString()));
                }
                else{
                    intent.putExtra("count", -1);
                }
                LocalBroadcastManager.getInstance(SettingsActivity.this).sendBroadcast(intent);
                finish();
            }
        });
        counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(counter.isChecked()){
                    counterInput.setVisibility(View.VISIBLE);
                    counterText.setVisibility(View.VISIBLE);
                }
                else{
                    counterInput.setVisibility(View.INVISIBLE);
                    counterText.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
