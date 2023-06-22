package com.honeywell.barcodeexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AnalysisActivity extends Activity {
    private Button backButton;
    private ArrayList<String> lis;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AnalysisActivity.this, android.R.layout.simple_list_item_1, getIntent().getStringArrayListExtra("data"));
        ListView barcodeList = (ListView) findViewById(R.id.listViewBarcodeData);
        barcodeList.setAdapter(dataAdapter);
        lis=new ArrayList<String>();
        lis.add("hi");
        lis.add("bye");
        ActivitySetting();
    }

    public void ActivitySetting() {
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
