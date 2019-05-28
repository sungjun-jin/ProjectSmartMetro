package com.example.projectsmartmetro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //주변 지하철역 검색
    TextView textStationSearch;
    Button btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("SmartMetro");


        textStationSearch = findViewById(R.id.textStationSearch);
        btnMap = findViewById(R.id.btnMap);

        goMenu();
    }

    private void goMenu() {


        textStationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //지하철역 검색
                Intent intent = new Intent(getApplicationContext(), StationSearch.class);
                startActivity(intent);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),GoogleMapActivity.class);
                startActivity(intent);
            }
        });


    }
}


