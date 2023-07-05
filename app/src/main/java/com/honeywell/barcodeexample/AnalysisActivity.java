package com.honeywell.barcodeexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.sql.Array;
import java.util.ArrayList;

public class AnalysisActivity extends Activity {
    private Button backButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis_screen);
        final ArrayAdapter<String> analysisData = new ArrayAdapter<String>(AnalysisActivity.this, R.layout.list_layout, getIntent().getStringArrayListExtra("data"));
        ListView barcodeList = (ListView) findViewById(R.id.listViewBarcodeData);
        barcodeList.setAdapter(analysisData);
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
