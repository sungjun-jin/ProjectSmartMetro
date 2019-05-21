package com.example.projectsmartmetro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class StationSearch extends AppCompatActivity {

    ArrayList<Station> stations = new ArrayList<>();


    EditText editSearch;
    RecyclerView recyclerView;

    Button btnSearch; //검색 버튼
    String searchStation; //검색


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_search);
        setTitle("지하철역 검색");

        editSearch = findViewById(R.id.editSearch);
        btnSearch = findViewById(R.id.btnSearch);
        recyclerView = findViewById(R.id.recyclerView);

        //위치 관리자 객체 참조
        final ODsayService odsayService = ODsayService.init(getApplicationContext(), "gbiQT+7PY6xA5hGIvtMCF6z6FQmpdiMI0D/MLvDaY3Y");
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);


        final OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            // 호출 성공 시 실행


            @Override
            public void onSuccess(ODsayData odsayData, API api) {
                try {

                    // API Value 는 API 호출 메소드 명을 따라갑니다.
                    if (api == API.SEARCH_STATION) {
                        String stationList = odsayData.getJson().getJSONObject("result").getString("station");
                        Log.d("DEBUG_CODE", stationList + "");
                        Log.d("DEBUG_CODE", "success");
                        stationListjsonParser(stationList); //JSON 파싱 함수 호출
                        CustomAdapter customAdapter = new CustomAdapter(stations); //어답터로 데이터 전송 및 생성자 호출
                        recyclerView.setAdapter(customAdapter); //어답터를 리사이클러뷰에 세팅
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext())); //어답터를 리사이클러뷰에 세팅
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("DEBUG_CODE", e.getMessage());
                }
            }

            // 호출 실패 시 실행
            @Override
            public void onError(int i, String s, API api) {
                if (api == API.SEARCH_STATION) {

                    Toast.makeText(getApplicationContext(), "호출 실패", Toast.LENGTH_SHORT).show();
                }
            }

        };


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //사용자가 검색 버튼을 눌렀을 시 API 호출
                searchStation = editSearch.getText().toString();
                odsayService.requestSearchStation(searchStation, "", "2", "", "", "", onResultCallbackListener);

            }
        });
    }

    public void stationListjsonParser(String jsonString) {

        //API를 호출하여 가져온 JSON형식의 데이터들을 역 정보 데이터 클래스(Station)에 넣어준다.

        try {
            //역 정보 데이터 클래스

            JSONArray jarray = new JSONArray(jsonString);
            stations.clear();

            for (int i = 0; i < jarray.length(); i++) {
                Station station = new Station();
                JSONObject jsonObject = jarray.getJSONObject(i);

                //데이터들을 데이터 클래스 (Station)에 담아준다
                station.stationName = jsonObject.getString("stationName");
                station.cityName = jsonObject.getString("do");
                station.dong = jsonObject.getString("dong");
                station.gu = jsonObject.getString("gu");
                station.laneName = jsonObject.getString("laneName");
                station.stationID = jsonObject.getInt("stationID");
                station.latitude = jsonObject.getDouble("x");
                station.longitude = jsonObject.getDouble("y");

                stations.add(station);

            }
            Log.d("DEBUG_CODE", "parsing success");


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("DEBUG_CODE", "parsing failed");


        }
    }

}


