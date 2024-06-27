package es.druedam.navesqr;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient
{
    //Direccion ip de la API
    private static final String URL = "http://direccion:puerto";
    private static Retrofit retrofit;

    //Metodo que devuelve la comunicacion con la api
    public static Retrofit getClient()
    {
        try
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return retrofit;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return  null;
    }
}
