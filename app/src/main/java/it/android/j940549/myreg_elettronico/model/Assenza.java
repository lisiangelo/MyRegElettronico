package it.android.j940549.myreg_elettronico.model;

public class Assenza {
    String alunno;
    String annosc;
    long data;
    String tipoAssenza;
    String giustificazione;

    public Assenza() {
        alunno = "";
        annosc="";
        data = 0;
        tipoAssenza = "";
        giustificazione = "";

    }

    public String getAlunno() {
        return alunno;
    }

    public void setAlunno(String alunno) {
        this.alunno = alunno;
    }

    public String getAnnosc() {
        return annosc;
    }

    public void setAnnosc(String annosc) {
        this.annosc = annosc;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getTipoAssenza() {
        return tipoAssenza;
    }

    public void setTipoAssenza(String tipoAssenza) {
        this.tipoAssenza = tipoAssenza;
    }

    public String getGiustificazione() {
        return giustificazione;
    }

    public void setGiustificazione(String giustificazione) {
        this.giustificazione = giustificazione;
    }


}

