package com.example.projectsmartmetro;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Intent intent = getIntent();

        String stationName = intent.getStringExtra("stationName"); //호출한 액티비티로부터 역의 이름을 할당받는다.
        double longitude = intent.getDoubleExtra("stationX",0); //호출한 액티비티로 부터 x좌표를 전달받는다
        double latitude = intent.getDoubleExtra("stationY",0);//호출한 액티비티로 부터 y좌표를 전달받는다


        LatLng station = new LatLng(latitude,longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(station);
        markerOptions.title(stationName);
        markerOptions.snippet(stationName);
        googleMap.addMarker(markerOptions);


        googleMap.moveCamera(CameraUpdateFactory.newLatLng(station));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }
}
