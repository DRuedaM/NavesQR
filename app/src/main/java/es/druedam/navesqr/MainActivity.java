package es.druedam.navesqr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                showDialog("Se ha cancelado la lectura de invitación");
                Toast.makeText(this, "Cancelado",Toast.LENGTH_LONG);
            }
            else
            {
                Date cDate = new Date();
                String fDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(cDate);
                infoMensaje = result.getContents().split("&");
                updateCodigoValidado(result.getContents(), new CodigoModel(0,infoMensaje[0], result.getContents(), true, true, fDate));
                Toast.makeText(this, "El valor escaneado es: " + result.getContents() , Toast.LENGTH_LONG);
                Log.i("NAVESQR", "el valor escaneado es:" + result.getContents());
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


    private void showDialog(String message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Codigo verificado")
                .setMessage(message)
                .setPositiveButton("Vale", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        alertDialog.show();

    }
    private void updateCodigoValidado(String codigo, CodigoModel codigoModel)
    {
        Call<CodigoModel> call = apiService.updateCodigo(codigo, codigoModel);

        call.enqueue(new Callback<CodigoModel>() {
            @Override
            public void onResponse(Call<CodigoModel> call, Response<CodigoModel> response)
            {
                if(response.isSuccessful())
                {
                    showDialog("Invitado de: " + infoMensaje[1] +
                            "\nLa invitación es válida");
                    Log.i("APINAVES", "REGISTRO ACTUALIZADO CORRECTAMENTE");
                }
                else
                {
                    showDialog("La invitación NO es válida");
                    Log.e("APINAVES", "ERROR AL ACTUALIZAR EL CODIGO " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CodigoModel> call, Throwable t)
            {
                Log.e("APINAVES", "");
                Log.e("APINAVES", "FALLO AL LLAMAR LA API " + t.getMessage());
            }
        });
    }



}