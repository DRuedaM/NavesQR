package es.druedam.navesqr;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient
{
    private static final String URL = "http://192.168.1.13:8080";
    private static Retrofit retrofit;

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
