package com.example.projectsmartmetro;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class BluetoothActivity extends AppCompatActivity {

    private BluetoothSPP bt;
    ImageView imageView1, imageView2, imageView3, imageView4;
    TextView textStationName;
    Button btnNextStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        setTitle("지하철 좌석 보기");
        bt = new BluetoothSPP(this); //Initializing

        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView4);
        imageView3 = findViewById(R.id.imageView2);
        imageView4 = findViewById(R.id.imageView8);

        textStationName = findViewById(R.id.textStationName);

        btnNextStation = findViewById(R.id.btnNextStation);

        final Intent intent = getIntent();

        final String stationName = intent.getStringExtra("stationName");
        textStationName.setText(stationName + "행"); //다음역 + 행 으로 문자열 합치기 ex)강남행


        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {

                Log.d("DEBUG_CODE", "message : " + message);

                //블루투스 데이터 수신
                //1 - 착석 0 - 공석

                if (message.equals("1")) {
                    Log.d("DEBUG_CODE", "A1 seated");
                    imageView1.setImageResource(R.drawable.seated_left);

                }
                if (message.equals("2")) {
                    Log.d("DEBUG_CODE", "A2 seated");
                    imageView2.setImageResource(R.drawable.seated_right);

                }

                if (message.equals("3")) {
                    Log.d("DEBUG_CODE", "A3 seated");
                    imageView3.setImageResource(R.drawable.seated_left);

                }

                if (message.equals("4")) {
                    Log.d("DEBUG_CODE", "A4 seated");
                    imageView4.setImageResource(R.drawable.seated_right);

                }


                //착석 코드


                if (message.equals("a")) {
                    Log.d("DEBUG_CODE", "A1 vacant");
                    imageView1.setImageResource(R.drawable.vacant_left);

                }
                if (message.equals("b")) {

                    Log.d("DEBUG_CODE", "A2 vacant");
                    imageView2.setImageResource(R.drawable.vacant_right);

                }

                if (message.equals("c")) {
                    Log.d("DEBUG_CODE", "A3 vacant");
                    imageView3.setImageResource(R.drawable.vacant_left);

                }

                if (message.equals("d")) {
                    Log.d("DEBUG_CODE", "A4 vacant");
                    imageView4.setImageResource(R.drawable.vacant_right);

                }
                //왼쪽
            }
        });

        btnNextStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentNext = new Intent(getApplicationContext(), BluetoothActivity2.class);
                intentNext.putExtra("stationName", stationName);
                startActivity(intentNext);
                bt.stopService(); //블루투스 중지

            }
        });


        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton btnConnect = findViewById(R.id.btnConnect); //연결시도
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
    }

    public void setup() {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}


