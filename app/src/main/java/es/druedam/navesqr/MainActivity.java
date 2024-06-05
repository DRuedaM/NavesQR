package es.druedam.navesqr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;

import es.druedam.navesqr.databinding.ActivityMainBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
    private ActivityMainBinding binding;
    private ApiService apiService;
    private String[] infoMensaje = null;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.app_icon_foreground);
        getSupportActionBar().setTitle("Naves Escaner QR");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));

        apiService = ApiClient.getClient().create(ApiService.class);

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
                openDialog("Se ha cancelado la lectura de invitación", false);
            }
            else
            {
                Date cDate = new Date();
                String fDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(cDate);
                infoMensaje = result.getContents().split("&");
                getCodigoValidado(result.getContents(), new CodigoModel(0,infoMensaje[0], result.getContents(), true, true, fDate));
                Toast.makeText(this, "Enviando...", Toast.LENGTH_LONG).show();
                Log.i("NAVESQR", "el valor escaneado es:" + result.getContents());
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void openDialog(String message, boolean messageState)
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnOk = dialog.findViewById(R.id.btnAceptar);
        TextView textTitle = dialog.findViewById(R.id.textTitle);
        ImageView imageView = dialog.findViewById(R.id.imageDialog);

        if(messageState)
        {
            textTitle.setText("ADELANTE");
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.checkimage));
            TextView textMessage = dialog.findViewById(R.id.textInfo);
            textMessage.setText(message);
        }
        else
        {
            textTitle.setText("ERROR");
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.crossimage));
            TextView textMessage = dialog.findViewById(R.id.textInfo);
            textMessage.setText(message);
        }

        btnOk.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void getCodigoValidado(String codigo, CodigoModel codigoModel)
    {
        Call<Boolean> callBool = apiService.getValidacionCodigo(codigo);
        callBool.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
               if(response.isSuccessful())
               {
                   if(!response.body())
                   {
                       updateCodigo(codigo, codigoModel);
                   }
                   else
                   {
                       openDialog("Invitacion del alumno: " + infoMensaje[1], false);
                   }
               }
               else
               {
                   openDialog("La invitación no existe o no se encuentra registrada en la base de datos", true);
               }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t)
            {
                openDialog("Fallo al llamar a la API " + t.getMessage(), false);
            }
        });
    }

    private void updateCodigo(String codigo, CodigoModel codigoModel)
    {
        Call<CodigoModel> call = apiService.updateCodigo(codigo, codigoModel);

        call.enqueue(new Callback<CodigoModel>() {
            @Override
            public void onResponse(Call<CodigoModel> call, Response<CodigoModel> response)
            {
                if(response.isSuccessful())
                {
                    openDialog("Invitado de: " + infoMensaje[1], true);
                    Log.i("APINAVES", "REGISTRO ACTUALIZADO CORRECTAMENTE");
                }
                else
                {
                    openDialog("La invitación NO es válida", false);
                    Log.e("APINAVES", "ERROR AL ACTUALIZAR EL CODIGO " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CodigoModel> call, Throwable t)
            {
                openDialog("Fallo al llamar a la API " + t.getMessage(), false);
            }
        });
    }



}