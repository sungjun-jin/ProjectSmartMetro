package com.example.projectsmartmetro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //주변 지하철역 검색
    TextView textStationSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("SmartMetro");


        textStationSearch = findViewById(R.id.textStationSearch);
        goMenu();
    }

    private void goMenu() {

        //메뉴
        textStationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //지하철역 검색
                Intent intent = new Intent(getApplicationContext(), StationSearch.class);
                startActivity(intent);
                finish();
            }
        });
    }
}


