package es.druedam.navesqr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Console;

import es.druedam.navesqr.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
{
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Escanee el QR de la invitación porfavor");
                integrator.setTorchEnabled(false);
                integrator.setBeepEnabled(true);
                integrator.initiateScan();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(result != null)
        {
            if(result.getContents() == null)
            {
                Toast.makeText(this, "Cancelado",Toast.LENGTH_LONG);
                Log.i("NAVESQR", "Cancelado");
            }
            else
            {
                Toast.makeText(this, "El valor escaneado es: " + result.getContents() , Toast.LENGTH_LONG);
                Log.i("NAVESQR", "el valor escaneado es:" + result.getContents());
            }
        }
        else
        {
            Toast.makeText(this,"AAAAAAAA", Toast.LENGTH_LONG);
            super.onActivityResult(requestCode, resultCode, data);
        }

    }



}