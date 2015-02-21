package de.spannpratze.vmrolltest;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {

    EditText txtFahrer;
    EditText txtFahrzeug;
    EditText txtBemerkungen;
    Button btnStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtFahrer = (EditText) findViewById(R.id.txtFahrer);
        txtFahrer.setHint("Fahrer");
        txtFahrzeug = (EditText) findViewById(R.id.txtFahrzeug);
        txtFahrzeug.setHint("Fahrzeug");
        txtBemerkungen = (EditText) findViewById(R.id.txtBemerkungen);
        txtBemerkungen.setHint("Bemerkungen");

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);

                intent.putExtra("Fahrer", txtFahrer.getText().toString());
                intent.putExtra("Fahrzeug", txtFahrzeug.getText().toString());
                intent.putExtra("Bemerkungen", txtBemerkungen.getText().toString());
                Log.e("n", txtFahrer.getText() + "." + txtFahrzeug.getText() + "." + txtBemerkungen);

                startActivity(intent);
                finish();
            }// onClick
        });
    }
}