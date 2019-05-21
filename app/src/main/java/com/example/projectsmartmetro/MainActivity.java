package com.example.projectsmartmetro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //주변 지하철역 검색
    TextView textPointSearch, textTimeTable, textStationSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("메뉴");

        textPointSearch = findViewById(R.id.textPointSearch);
        textTimeTable = findViewById(R.id.textTimeTable);
        textStationSearch = findViewById(R.id.textStationSearch);

        goMenu();
    }

    private void goMenu() {

        textPointSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //주변 지하철 역 검색
                Intent intent = new Intent(getApplicationContext(), NearSubway.class);
                startActivity(intent);
            }
        });

        textStationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //지하철역 검색
                Intent intent = new Intent(getApplicationContext(), StationSearch.class);
                startActivity(intent);
            }
        });


    }
}


