package com.guris.motoapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    MyDatabaseHelper myDB;
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        myDB = new MyDatabaseHelper(MainActivity.this);
        super.onCreate(savedInstanceState);
        timer = new Timer();
        startTimer();
    }

    public void buscarInformacoesGPS() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)   != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            return;
        }

        LocationManager  mLocManager  = (LocationManager) getSystemService(MainActivity.this.LOCATION_SERVICE);
        LocationListener mLocListener = new MinhaLocalizacaoListener();

        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListener);

        if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            String lat = "" + MinhaLocalizacaoListener.latitude;
            String longi = "" + MinhaLocalizacaoListener.longitude;

            AddData(lat,longi,"0");

//            save(texto);

        } else {
            toastMessage("GPS DESABILITADO.");
        }
    }

    public void startTimer()
    {
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        time++;
                        buscarInformacoesGPS();
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }

//    public void save(String text){
//        FileOutputStream fos =null;
//
//        try {
//            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
//            fos.write(text.getBytes());
//
//            Toast.makeText(this, "Saved to "+ getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_LONG).show();
//        } catch (FileNotFoundException e) {
//            System.out.println("------------------->Erro1");
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("------------------->Erro2");
//            e.printStackTrace();
//        } finally {
//            if(fos != null){
//                try {
//                    fos.close();
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    public void AddData(String lat, String longi, String g) {
        myDB.addData(lat,longi,g);
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }


}