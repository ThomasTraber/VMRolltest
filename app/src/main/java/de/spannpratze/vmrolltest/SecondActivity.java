package de.spannpratze.vmrolltest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Felix on 20.02.2015.
 */
public class SecondActivity extends Activity {

    Button btnStop;
    File myFile;
    BufferedWriter out;
    float[] werte;
    private SensorEventListener listener;
    private SensorManager mSensorManager;
    private Sensor sensor;
    int sensordelay = SensorManager.SENSOR_DELAY_FASTEST;
    //int sensordelay = SensorManager.SENSOR_DELAY_NORMAL;      //200ms. Zu langsam für Rolltesting
    //int sensordelay = SensorManager.SENSOR_DELAY_UI;          //60 ms 
    //int sensordelay = SensorManager.SENSOR_DELAY_GAME;        //20 ms
    String fileName = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
    String path = Environment.getExternalStorageDirectory().getPath() + "/vmrolltest/" + fileName + ".csv";
    private static final String TAG = SecondActivity.class.getSimpleName();
    String savedata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView Fahrer = (TextView) findViewById(R.id.Fahrer);
        TextView Fahrzeug = (TextView) findViewById(R.id.Fahrzeug);
        TextView Bemerkungen = (TextView) findViewById(R.id.Bemerkungen);

        Intent in = getIntent();
        String fahrer = in.getStringExtra("Fahrer");
        String fahrzeug = in.getStringExtra("Fahrzeug");
        String bemerkungen = in.getStringExtra("Bemerkungen");
        Log.e("zweite Activity", fahrer + "." + fahrzeug + "." + bemerkungen);

        Fahrer.setText(fahrer);
        Fahrzeug.setText(fahrzeug);
        Bemerkungen.setText(bemerkungen);
        try{
        myFile=open(path);
        out = new BufferedWriter(new FileWriter(myFile, true));
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }

        try {
            saveln("#Fahrer:\t"+fahrer);
            saveln("#Fahrzeug:\t"+fahrzeug);
            saveln("#Bemerkungen:\t"+bemerkungen);
            saveln("#Time/ns\tMAGNETIC FIELD X /μT\tMAGNETIC FIELD Y /μT\tMAGNETIC FIELD Z /μT");
            Toast.makeText(getBaseContext(),
                    "Auf geht's",
                    Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (sensor == null) {
            Log.d(TAG, "kein Sensor vorhanden");
            // Activity beenden
            finish();
        }
        listener = new SensorEventListener() {

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                    savedata = String.valueOf(event.timestamp) + "\t"
                        + String.valueOf(event.values[0]) + "\t" 
                        + String.valueOf(event.values[1]) + "\t"
                        + String.valueOf(event.values[2]);
                    saveln(savedata);
                }
            }
        };
        mSensorManager.registerListener(listener, sensor, sensordelay);

        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private File open (String path) {
        try {
            myFile = new File(path);
            //wenn das File nicht existiert -> Folders und File erstellen
            if(!myFile.exists()) {
                myFile.getParentFile().mkdirs();
                myFile.createNewFile();
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
        return myFile;
    }


    private void saveln (String s) {
        try {
            out.write(s + "\n");
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    protected void onDestroy() {
        try{
        out.close();     //ist zwar die falsche Stelle, aber fuer den Anfang sollte es reichen
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
        super.onDestroy();
        if (sensor != null) {
            mSensorManager.unregisterListener(listener);
        }
    }
}
