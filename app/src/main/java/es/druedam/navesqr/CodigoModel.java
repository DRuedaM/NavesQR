package es.druedam.navesqr;

import java.util.Date;

//Clase que contiene el modelo y estructura de la tabla codigo de la base de datos
public class CodigoModel
{
    int id_codigo;
    String correo;
    String codigo;
    boolean validado;
    boolean enviado;
    String fecha_validacion;

    public CodigoModel(int id_codigo ,String correo, String codigo, boolean validado, boolean enviado, String fecha_validado) {
        this.id_codigo = id_codigo;
        this.correo = correo;
        this.codigo = codigo;
        this.validado = validado;
        this.enviado = enviado;
        this.fecha_validacion = fecha_validado;
    }



    public String getCorreo() {
        return correo;
    }

    public String getCodigo() {
        return codigo;
    }

    public boolean isValidado() {
        return validado;
    }

    public boolean isEnviado() {
        return enviado;
    }

    public String getFecha_validacion() {
        return fecha_validacion;
    }
}
