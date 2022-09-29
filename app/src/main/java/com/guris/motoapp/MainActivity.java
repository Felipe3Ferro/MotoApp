package com.guris.motoapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Timer timer;
    TimerTask timerTask;
    FileUtil fileUtil;
    File file;
    int Delay = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        file = new File(getFilesDir()+ "/data.csv");
        timer = new Timer();

        if(!file.exists()) {
            fileUtil.writeStringAsFile("latitude;longitude;timestamp;x;y;z\n", file);
        }
        startTimer();
    }

    public void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buscarInformacoesGPS();
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0, Delay);
    }

    public void buscarInformacoesGPS() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            return;
        }

        LocationManager mLocManager = (LocationManager) getSystemService(MainActivity.this.LOCATION_SERVICE);
        LocationListener mLocListener = new MinhaLocalizacaoListener();

        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListener);

        if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if(MinhaLocalizacaoListener.longitude != 0 && MinhaLocalizacaoListener.latitude != 0){
                String texto = MinhaLocalizacaoListener.latitude + ";" + MinhaLocalizacaoListener.longitude + ";" + System.currentTimeMillis()/1000 + "\n";
                fileUtil.appendStringToFile(texto,file);
            }else{
                Toast.makeText(MainActivity.this, "CARREGANDO.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "GPS DESABILITADO.", Toast.LENGTH_SHORT).show();
        }

    }


}

//    public void save(String text) {
//        FileOutputStream fos = null;
//        try {
//            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
//            fos.write(text.getBytes());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    public void load(View v) {
//        FileInputStream fis = null;
//
//        try {
//            fis = openFileInput(FILE_NAME);
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader br = new BufferedReader(isr);
//            StringBuilder sb = new StringBuilder();
//            String text;
//
//            while ((text = br.readLine()) != null) {
//                sb.append(text).append("\n");
//            }
//
//            mEditText.setText(sb.toString());
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fis != null) {
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
