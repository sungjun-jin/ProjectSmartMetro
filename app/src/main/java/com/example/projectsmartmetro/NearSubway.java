package com.example.projectsmartmetro;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class NearSubway extends AppCompatActivity {


    String strLatitude;
    String strLongitude;
    RecyclerView recyclerViewMap;

    ArrayList<Station> stations = new ArrayList<>();

    // 주변 역정보를 보여주는 액티비티
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_subway);
        recyclerViewMap = findViewById(R.id.recyclerViewMap);
        setTitle("주변역 찾기");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //위치 관리자 객체 참조
        ODsayService odsayService = ODsayService.init(getApplicationContext(), "gbiQT+7PY6xA5hGIvtMCF6z6FQmpdiMI0D/MLvDaY3Y");
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);

        Location userLocation = getMyLocation(locationManager);

        if (userLocation != null) {

            strLatitude = Double.toString(userLocation.getLatitude());
            strLongitude = Double.toString(userLocation.getLongitude());


        }

        // API 호출
        odsayService.requestPointSearch(strLongitude, strLatitude, "1000", "2", onResultCallbackListener);
    }


    // 콜백 함수 구현
    OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        // 호출 성공 시 실행

        @Override
        public void onSuccess(ODsayData odsayData, API api) {
            try {

                // API Value 는 API 호출 메소드 명을 따라갑니다.
                if (api == API.POINT_SEARCH) {

                    String stationList = odsayData.getJson().getJSONObject("result").getString("station");
                    Log.d("DEBUG_CODE", stationList);
                    Log.d("DEBUG_CODE", "success");
                    stationListjsonParser(stationList);
                    CustomAdapter customAdapter = new CustomAdapter(stations);
                    recyclerViewMap.setAdapter(customAdapter);
                    recyclerViewMap.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            } catch (JSONException e) {
                e.printStackTrace(); //오류메시지 출력
                Log.d("DEBUG_CODE", e.getMessage());
            }
        }

        // 호출 실패 시 실행
        @Override
        public void onError(int i, String s, API api) {
            if (api == API.POINT_SEARCH) {

                Toast.makeText(getApplicationContext(), "호출 실패 " + s, Toast.LENGTH_SHORT).show();
                Log.d("DEBUG_CODE", "longitude :" + strLongitude + " latitude : " + strLatitude);
            }
        }

    };

    private Location getMyLocation(LocationManager locationManager) {

        Location currentLocation = null;

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //권한 요청 승인이 없을 시
            //사용자에게 권한 요청을 한다.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 001);
            getMyLocation(locationManager);

        } else {

            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);

        }
        return currentLocation;
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

                //데이터들을 담아준다
                station.stationName = jsonObject.getString("stationName");
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

