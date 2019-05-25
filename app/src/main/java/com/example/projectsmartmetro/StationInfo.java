package com.example.projectsmartmetro;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StationInfo extends AppCompatActivity {

    Station station = new Station(); //현재역 정보를 담을 클래스
    Station prevStation = new Station(); //이전역 정보를 담을 클래스
    Station nextStation = new Station(); //다음역 정보를 담을 클래스

    TextView textPrevious, textStation, textNext, textAddress, textTel, textLaneName; //TextView 위젯 선언

    //OKHttp - > 안드로이드 http 커넥션을 쉽게 도와주는 라이브러리 사용
    OkHttpClient okHttpClient;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_info);
        setTitle("역 세부정보");

        textPrevious = findViewById(R.id.textPrevious); //이전역
        textStation = findViewById(R.id.textStation); //현재역
        textNext = findViewById(R.id.textNext); //다음역역
        textAddress = findViewById(R.id.textAddress); //현재역 도로명 주소
        textTel = findViewById(R.id.textTel); //현재역 전화번호
        textLaneName = findViewById(R.id.textLaneName); //현재역 호선명

        okHttpClient = new OkHttpClient();




        Intent intent = getIntent();
        //넘겨받은 지하철의 ID를 담을 변수, 이걸 토대로 정류장 정보를 가져올거임
        int ID = intent.getIntExtra("stationID", 0);
        Log.d("DEBUG_CODE", "넘어온 정류장 ID: " + ID);
        ODsayService odsayService = ODsayService.init(getApplicationContext(), "gbiQT+7PY6xA5hGIvtMCF6z6FQmpdiMI0D/MLvDaY3Y");
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);

        odsayService.requestSubwayStationInfo(Integer.toString(ID), onResultCallbackListener);

        textNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이전 역 클릭시 이전역의 세부정보를 보여주는 액티비티로 이동

                Intent intent = new Intent(getApplicationContext(), StationInfo.class); //현재 액티비티와 동일한 액티비티를 호출하지만 데이터가 다르다
                intent.putExtra("stationID", nextStation.stationID);

                startActivity(intent);
                finish(); //현재 액티비티 종료

            }
        });

        textPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //이전 역 클릭시 이전역의 세부정보를 보여주는 액티비티로 이동

                Intent intent = new Intent(getApplicationContext(), StationInfo.class); //현재 액티비티와 동일한 액티비티를 호출하지만 데이터가 다르다
                intent.putExtra("stationID", prevStation.stationID);

                startActivity(intent);
                finish();  //현재 액티비티 종료
            }
        });

        getSubwayArrivalTime("http://swopenapi.seoul.go.kr/api/subway/6f4547614b64616437345459424244/json/realtimeStationArrival/0/5/%EC%9D%B8%EC%B2%9C");

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    public String getStationArrivalUrl(String url) throws IOException {
        //okhttp 실행
        //MainThread에서 동작하기 때문에 사용하기 위해서는 AsyncTask같은 쓰레드 요구를 써야함
        Request request = new Request.Builder()
                .url(url)
                .build();
        //내부적으로 httpUrlConnection 클래스를 처리
        //Reponse -> 응답 정보 클래스
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private  void getSubwayArrivalTime(final String urlString) {

        //지하철역의 도착시간을 알려주는 함수
        //서울열린데이터광장 지하철 도착 정보 api사용 (OdsayAPI 미사용)

        AsyncTask asyncTask = new AsyncTask<Object, Void, String>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            protected String doInBackground(Object... objects) {

                //백그라운드에서 동작
                String data = "";

                try {
                     data = getStationArrivalUrl(urlString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(String str) {

                Log.d("DEBUG_CODE","지하철 도착정보" + str);



            }
        };
        asyncTask.execute(urlString);
    }

    OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        // 호출 성공 시 실행

        @Override
        public void onSuccess(ODsayData odsayData, API api) {
            try {

                // API Value 는 API 호출 메소드 명을 따라갑니다.
                if (api == API.SUBWAY_STATION_INFO) {
                    //현재 역의 정보를 가져옴
                    station.stationName = odsayData.getJson().getJSONObject("result").getString("stationName");//역 명
                    station.laneName = odsayData.getJson().getJSONObject("result").getString("laneName"); //호선 명
                    station.stationID = odsayData.getJson().getJSONObject("result").getInt("stationID"); //역 ID
                    station.latitude = odsayData.getJson().getJSONObject("result").getDouble("x"); //역 위도
                    station.longitude = odsayData.getJson().getJSONObject("result").getDouble("y"); //역 경도
                    String stationDefaultInfo = odsayData.getJson().getJSONObject("result").getString("defaultInfo"); //현재 지하철역의 추가정보 (주소, 전화번호)를 담은 JSON
                    Log.d("DEBUG_CODE", "success");
                    stationListjsonParser(stationDefaultInfo);

                    textStation.setText(station.stationName);
                    textLaneName.setText(station.laneName);
                    textAddress.setText(station.address);
                    textTel.setText(station.tel);
                    //여기까지 현재역

                    //이전역의 정보를 가저옴

                    String previousStationInfo = odsayData.getJson().getJSONObject("result").getJSONObject("prevOBJ").getString("station"); //이전 지하철역의 정보를 담은 JSON
                    Log.d("DEBUG_CODE", "previousInfo : " + previousStationInfo);
                    previousStationListjsonParser(previousStationInfo);

                    textPrevious.setText(prevStation.stationName);
                    //여기까지 이전역

                    //다음역의 정보를 가저옴

                    String nextStationInfo = odsayData.getJson().getJSONObject("result").getJSONObject("nextOBJ").getString("station"); //이전 지하철역의 정보를 담은 JSON
                    Log.d("DEBUG_CODE", "nextInfo : " + nextStationInfo);
                    nextStationListjsonParser(nextStationInfo);

                    textNext.setText(nextStation.stationName);
                    //여기까지 다음역

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("DEBUG_CODE", e.getMessage());
            }
        }

        // 호출 실패 시 실행
        @Override
        public void onError(int i, String s, API api) {
            if (api == API.SUBWAY_STATION_INFO) {

                Toast.makeText(getApplicationContext(), "호출 실패 " + s, Toast.LENGTH_SHORT).show();
            }
        }

    };

    private void stationListjsonParser(String stationDefaultInfo) {
        //현재역
        //API를 호출하여 가져온 JSON형식의 데이터들을 역 정보 데이터 클래스(Station)에 넣어준다.

        try {
            //역 정보 데이터 클래스
            JSONObject jsonObject = new JSONObject(stationDefaultInfo);
            //데이터들을 담아준다
            //현재역에 대한 정보는 이미 있으므로 추가정보(defaultInfo)즉 주소, 전화번호만 받아준다.
            station.address = jsonObject.getString("new_address"); //도로명 주소
            station.tel = jsonObject.getString("tel"); //전화번호

            Log.d("DEBUG_CODE", "parsing success");


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("DEBUG_CODE", "parsing failed " + "error Code : " + e.getMessage());

        }
    }

    private void previousStationListjsonParser(String previousStationInfo) {
        //이전역
        //API를 호출하여 가져온 JSON형식의 데이터들을 역 정보 데이터 클래스(Station)에 넣어준다.
        //이전역 다음역 switch 처리 필요

        try {
            //역 정보 데이터 클래스

            JSONArray jarray = new JSONArray(previousStationInfo);

            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jsonObject = jarray.getJSONObject(i);

                //데이터들을 데이터 클래스 (Station)에 담아준다
                prevStation.stationName = jsonObject.getString("stationName"); //이전역의 이름
                prevStation.laneName = jsonObject.getString("laneName"); //이전역의 노선
                prevStation.stationID = jsonObject.getInt("stationID"); //이전역의 ID
                prevStation.latitude = jsonObject.getDouble("x"); //이전역의 위도
                prevStation.longitude = jsonObject.getDouble("y"); //이전역의 경도

            }
            Log.d("DEBUG_CODE", "parsing success");


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("DEBUG_CODE", "parsing failed");


        }
    }

    private void nextStationListjsonParser(String nextStationInfo) {
        //다음역
        //API를 호출하여 가져온 JSON형식의 데이터들을 역 정보 데이터 클래스(Station)에 넣어준다.
        //이전역 다음역 switch 처리 필요

        try {
            //역 정보 데이터 클래스

            JSONArray jarray = new JSONArray(nextStationInfo);

            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jsonObject = jarray.getJSONObject(i);


                //데이터들을 데이터 클래스 (Station)에 담아준다
                nextStation.stationName = jsonObject.getString("stationName"); //다음역의 이름
                nextStation.laneName = jsonObject.getString("laneName"); //다음역의 노선
                nextStation.stationID = jsonObject.getInt("stationID"); //다음역의 ID
                nextStation.latitude = jsonObject.getDouble("x"); //다음역의 위도
                nextStation.longitude = jsonObject.getDouble("y"); //다음역의 경도

            }
            Log.d("DEBUG_CODE", "parsing success");


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("DEBUG_CODE", "parsing failed");


        }
    }


}

