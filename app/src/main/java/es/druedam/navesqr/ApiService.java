package es.druedam.navesqr;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService
{
    //Metodo que envia el codigo a la api para actualizarlo en la base de datos y registrarlo
    @PUT("/api-lasnaves/{codigo}")
    Call<CodigoModel> updateCodigo(@Path("codigo") String codigo, @Body CodigoModel codigoModel);

    //Metodo para obtener un codigo y saber si ya ha sido validado anteriormente
    @GET("/api-lasnaves/get-code/{codigo}")
    Call<Boolean> getValidacionCodigo(@Path("codigo") String codigo);
}
