package com.ljh2017.footprint;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {

    TextView tvAddr;
    EditText etAddr;

    GoogleMap map;

    boolean isGPSEnable = false;

    MapFragment mapFragment;

    LocationManager locationManager;

    double latitude;
    double longitude;

    String address; // 주소(좌표를 주소로)

    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        tvAddr = (TextView) findViewById(R.id.tv_Addr);
        etAddr = (EditText) findViewById(R.id.et_searchAddr);

        //위치관리자 객체 얻어오기
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true); //비용지불을 허용하는가?
        criteria.setAccuracy(Criteria.NO_REQUIREMENT); //정확도가 요하는가?
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT); //배터리 소모량..
        criteria.setAltitudeRequired(false);  //고도에 대한 위치정보.


        //// 나의 위치 ////**08.14
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        location = null;

        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!isGPSEnable) {
            showSettingsAlert();
        }

        // 구글맵에 위치 보여주기**
        FragmentManager manager = getFragmentManager();
        mapFragment = (MapFragment) manager.findFragmentById(R.id.map);

        //showMap();
        //**
        //좌표 --> 주소(역 지오코딩)
        //findAddress();
    }

    //위치정보를 듣는 리스너 객체 생성
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            showMap();

            Log.e("위치", latitude + "," + longitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    void showMap() {
        // 구글맵에 위치 보여주기**
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                LatLng myLocation = new LatLng(latitude, longitude);

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

                //마커 추가하기
                MarkerOptions marker = new MarkerOptions();
                marker.title("My Location");
                marker.position(myLocation);
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_one_1));
                marker.anchor(0.0f, 0.0f);

                map.addMarker(marker);
                findAddress();

                //map세팅
                UiSettings settings = map.getUiSettings();
                settings.setZoomControlsEnabled(true);
            }
        });
        //**
    }

    void findAddress() {
        //!!좌표 --> 주소(역 지오코딩)
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0);
                tvAddr.setText(address);
            } else {
                address = "현재위치를 찾지 못했습니다.";
                tvAddr.setText(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        showMap();
        //!!
    }

    public void clickSearch(View v) {

        onPause();
        // 주소를 좌표로 바꾸기
        String addr = etAddr.getText().toString();

        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        try {
            List<Address> addresses = geocoder.getFromLocationName(addr, 3);

            if (addresses.size() > 0) {
                latitude = addresses.get(0).getLatitude();
                longitude = addresses.get(0).getLongitude();
            }
            //Toast.makeText(this, buffer, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
        findAddress();
        showMap();
    }



    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // TODO : 위치정보듣는 리스너 찾아보기
        if (locationManager.isProviderEnabled("network")) {  //gps사용가능한가? //테스트는 network로..
            locationManager.requestLocationUpdates("network", 10000, 1, locationListener);
            Toast.makeText(this, "네트워크로 현재위치 검색중입니다~", Toast.LENGTH_SHORT).show();
            findAddress();

        } else if (locationManager.isProviderEnabled("gps")) { //network로
            locationManager.requestLocationUpdates("gps", 10000, 1, locationListener);
            Toast.makeText(this, "GPS로 현재위치 검색중입니다~", Toast.LENGTH_SHORT).show();
            findAddress();
        } else {
            Toast.makeText(this, "위치정보를 찾지 못했습니다.", Toast.LENGTH_SHORT).show();
            //tvAddr.setText("위치정보를 찾지 못했습니다.");
        }

    }

    // GPS Dialog
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS 사용 유무");
        alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?");
        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    // 저장하기
    public void clickSave(View v) {
        Intent intent = getIntent();
        intent.putExtra("Addr", address);
        setResult(RESULT_OK, intent);
        finish();
    }
}