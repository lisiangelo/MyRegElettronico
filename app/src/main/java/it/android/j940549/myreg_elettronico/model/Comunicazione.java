package it.android.j940549.myreg_elettronico.model;

public class Comunicazione {
    String alunno;
    long data;
    String testo;

    public Comunicazione() {
        alunno = "";
    }

    public String getAlunno() {
        return alunno;
    }

    public void setAlunno(String alunno) {
        this.alunno = alunno;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
}

