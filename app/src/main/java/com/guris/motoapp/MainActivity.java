package com.guris.motoapp;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {
    LocationManager locationManager;
    private static final int GPS_TIME_INTERVAL = 1000;
    private static final int GPS_DISTANCE = 1000;

    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    final static int PERMISSION_ALL = 1;

    SensorManager sensorManager;
    Sensor accelerometer;
    Timer timer;
    FileUtil fileUtil;
    File file;
    float x,y,z = 0;
    Location l = new Location("");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        }

        file = new File(getFilesDir()+ "/data.csv");
        timer = new Timer();
        if(!file.exists()) {
            fileUtil.writeStringAsFile("latitude;longitude;timestamp;x;y;z\n", file);
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                requestLocation();
                escreveArquivo();
                handler.postDelayed(this, 1000);
            }
        }, 0);
    }

    private void escreveArquivo() {
        String texto = l.toString() + ";" + System.currentTimeMillis()/1000 + ";" + x + ";" + y + ";" + z + "\n";
        fileUtil.appendStringToFile(texto, file);
//        System.out.println(texto);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        l = location;
        locationManager.removeUpdates(this);
    }

    private void requestLocation() {
        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        GPS_TIME_INTERVAL, GPS_DISTANCE, this);
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        x = sensorEvent.values[0];
        y =sensorEvent.values[1] ;
        z = sensorEvent.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
