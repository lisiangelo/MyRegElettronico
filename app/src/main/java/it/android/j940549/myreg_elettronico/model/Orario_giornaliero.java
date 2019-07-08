package it.android.j940549.myreg_elettronico.model;

public class Orario_giornaliero {
    private int giorno;
    private int ora;
    private String materia, insegnante;

    public Orario_giornaliero(){

    }

    public int getGiorno() {
        return giorno;
    }

    public void setGiorno(int giorno) {
        this.giorno = giorno;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getInsegnante() {
        return insegnante;
    }

    public void setInsegnante(String insegnante) {
        this.insegnante = insegnante;
    }

    public int getOra() {
        return ora;
    }

    public void setOra(int ora) {
        this.ora = ora;
    }
}
