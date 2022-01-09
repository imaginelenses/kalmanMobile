package com.imaginelenses.kalman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;

public class Home extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor mAccelerometer;
    private final DecimalFormat f = new DecimalFormat("#0.000000");
    private boolean move = false;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Intent scanner = getIntent();
        String address = scanner.getStringExtra("address");

//        Move Switch
        SwitchCompat moveSwitch = findViewById(R.id.move);
        moveSwitch.setChecked(this.move);
        moveSwitch.setOnCheckedChangeListener((compoundButton, isActivated) -> move = isActivated);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        TextView vendor = findViewById(R.id.vendor);
        String out = String.format("%s \nVersion: %s", mAccelerometer.getVendor(), mAccelerometer.getVersion());
        vendor.setText(out);


//        Web Sockets (Client version 1.0.1 -> Server version 1.7.19)
        IO.Options options = new IO.Options();
        options.forceNew = false;
        options.transports = new String[]{ WebSocket.NAME };
        try {
            socket = IO.socket(address, options);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView x = findViewById(R.id.x);
        x.setText(f.format(event.values[0]));
        TextView y = findViewById(R.id.y);
        y.setText(f.format(event.values[1]));
        TextView z = findViewById(R.id.z);
        z.setText(f.format(event.values[2]));

        JSONObject data = new JSONObject();
        try {
            data.put("x", f.format(event.values[0]));
            data.put("y", f.format(event.values[1]));
            data.put("z", f.format(event.values[2]));
            data.put("timestamp", event.timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (move){
            socket.emit("move_event", data);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}