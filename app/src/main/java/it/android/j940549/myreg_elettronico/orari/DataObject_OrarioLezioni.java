package it.android.j940549.myreg_elettronico.orari;

/**
 * Created by J940549 on 30/12/2017.
 */

public class DataObject_OrarioLezioni {

    private String materia;
    private String docente;
    private int girono;

    DataObject_OrarioLezioni(){

    }
    DataObject_OrarioLezioni(String unamateria, String undocente){
        materia= unamateria;
        docente = undocente;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getDocente() {
        return docente;
    }

    public void setDocente(String docente) {
        this.docente = docente;
    }

    public int getGirono() {
        return girono;
    }

    public void setGirono(int girono) {
        this.girono = girono;
    }
}