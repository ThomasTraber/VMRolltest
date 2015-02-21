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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Felix on 20.02.2015.
 */
public class SecondActivity extends Activity {

    Button btnStop;
    File myFile;
    float[] werte;
    int i = 0;
    private SensorEventListener listener;
    private SensorManager mSensorManager;
    private Sensor sensor;
    private final String geti = gt();
    String fileName = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
    String path = Environment.getExternalStorageDirectory().getPath() + "/vmrolltest/" + fileName + ".csv";
    private static final String TAG = SecondActivity.class.getSimpleName();

    public String gt() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return timestamp.toString();
    }

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

        try {
            save("sep=;\n" +"Name;Fahrzeug;Bemerkungen\n"+fahrer+";"+fahrzeug+";"+bemerkungen+"\n"+
                    "MAGNETIC FIELD X (μT);MAGNETIC FIELD Y (μT);MAGNETIC FIELD Z (μT);YYYY-MO-DD HH-MI-SS_SSS");
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
            Log.d(TAG, "kein Sensor vorhangen");
            // Activity beenden
            finish();
        }
        listener = new SensorEventListener() {

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                i++;
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                    werte = event.values.clone();
                    String x = String.valueOf(werte[0]);
                    String y = String.valueOf(werte[1]);
                    String z = String.valueOf(werte[2]);
                    save(x + ";" + y + ";" + z + ";" + gt());
                }
            }
        };
        mSensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);

        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void save (String s) {
        try {
            myFile = new File(path);
            System.out.println(path);
            //wenn das File nicht existiert -> Folders und File erstellen
            if(!myFile.exists()) {
                myFile.getParentFile().mkdirs();
                myFile.createNewFile();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(myFile, true));
            out.write(s);
            out.write("\n");
            out.close();
            MediaScannerConnection.scanFile(this, new String[] {myFile.getAbsolutePath()}, null, null);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (sensor != null) {
            mSensorManager.unregisterListener(listener);
        }
    }
}