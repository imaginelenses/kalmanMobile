package com.imaginelenses.kalman;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Collections;
import java.util.regex.Pattern;

public class Scanner extends AppCompatActivity {

    private final Pattern address = Pattern.compile("(http)s*:\\/\\/[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}:[0-9]{2,4}");
    private DecoratedBarcodeView scanner;

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null) {
                return;
            }

            if (!address.matcher(result.getText()).matches()) {
                return;
            }

            scanner.setStatusText(result.getText());

            Intent i = new Intent(getApplicationContext(), Home.class);
            i.putExtra("address", result.getText());
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);

        scanner = findViewById(R.id.scanner);
        scanner.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(Collections.singleton(BarcodeFormat.QR_CODE)));
        scanner.setStatusText("Scan Kalman QR code");
        scanner.initializeFromIntent(getIntent());
        scanner.decodeContinuous(callback);

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        scanner.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanner.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return scanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}


