package it.android.j940549.myreg_elettronico.model;

public class Compito {
    String alunno;
    String data;
    String argoemnto;
    String compiti;
    String assenze;
    String noteProf;
    String noteDiscip;

    public Compito() {
        alunno = "";
        data = "";
        argoemnto = "";
        compiti= "";
        assenze = "";
    }

    public String getAlunno() {
        return alunno;
    }

    public void setAlunno(String alunno) {
        this.alunno = alunno;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArgoemnto() {
        return argoemnto;
    }

    public void setArgoemnto(String argoemnto) {
        this.argoemnto = argoemnto;
    }

    public String getCompiti() {
        return compiti;
    }

    public void setCompiti(String compiti) {
        this.compiti = compiti;
    }

    public String getAssenze() {
        return assenze;
    }

    public void setAssenze(String assenze) {
        this.assenze = assenze;
    }

    public String getNoteProf() {
        return noteProf;
    }

    public void setNoteProf(String noteProf) {
        this.noteProf = noteProf;
    }

    public String getNoteDiscip() {
        return noteDiscip;
    }

    public void setNoteDiscip(String noteDiscip) {
        this.noteDiscip = noteDiscip;
    }

}