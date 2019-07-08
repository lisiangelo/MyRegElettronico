package it.android.j940549.myreg_elettronico.orari;

/**
 * Created by J940549 on 30/12/2017.
 */

public class DataObject_Materia {

    private String materia;
    private String docente;
    private int id_materia;

    DataObject_Materia(){

    }

    public int getId_materia() {
        return id_materia;
    }

    public void setId_materia(int id_materia) {
        this.id_materia = id_materia;
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


}