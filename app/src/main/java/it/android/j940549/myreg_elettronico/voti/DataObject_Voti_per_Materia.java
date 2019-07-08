package it.android.j940549.myreg_elettronico.voti;

/**
 * Created by J940549 on 30/12/2017.
 */

public class DataObject_Voti_per_Materia {
    private String data;
    private String tipo;
    private Double voto;

    DataObject_Voti_per_Materia(String unadata, String untipo, Double unvoto){
        data= unadata;
        tipo= untipo;
        voto=unvoto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getVoto() {
        return voto;
    }

    public void setVoto(Double voto) {
        this.voto = voto;
    }

    public String getData() {

        return data;
    }

    public void setData(String data) {


        this.data = data;
    }
}