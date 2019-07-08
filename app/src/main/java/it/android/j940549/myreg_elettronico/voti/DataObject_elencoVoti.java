package it.android.j940549.myreg_elettronico.voti;

/**
 * Created by J940549 on 30/12/2017.
 */

public class DataObject_elencoVoti {
    private String materia;
    private String data;
    private String tipo;
    private Double voto;

    public DataObject_elencoVoti(String unamateria, String unadata, String untipo,Double unvoto){
        materia= unamateria;
        data = unadata;
        voto=unvoto;
        tipo=untipo;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
}