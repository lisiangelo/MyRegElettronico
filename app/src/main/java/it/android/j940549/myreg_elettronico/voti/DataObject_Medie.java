package it.android.j940549.myreg_elettronico.voti;

/**
 * Created by J940549 on 30/12/2017.
 */

public class DataObject_Medie {
    private String materia;
    private String docente;
    private Double mediaVoti;

    DataObject_Medie(String unamateria, String undocente, Double unamedia){
        materia= unamateria;
        docente = undocente;
        mediaVoti=unamedia;
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

    public Double getMediaVoti() {
        return mediaVoti;
    }

    public void setMediaVoti(Double mediaVoti) {
        this.mediaVoti = mediaVoti;
    }
}