package it.android.j940549.myreg_elettronico.app_widget;

public class WidgetItem {
    private String nomealunno;
    private String voto;
    private String datavoto;
    private String materia;
    private String dataAss;
    private String tipoAssenza;
    private String giustifica;
    private double media;

    public WidgetItem() {
        nomealunno="";
        voto="";
        materia="";
        dataAss="";
        tipoAssenza="";
        giustifica="";
        media=0;
    }

    public String getNomealunno() {
        return nomealunno;
    }

    public void setNomealunno(String nomealunno) {
        this.nomealunno = nomealunno;
    }

    public String getVoto() {
        return voto;
    }

    public void setVoto(String voto) {
        this.voto = voto;
    }

    public String getDatavoto() {
        return datavoto;
    }

    public void setDatavoto(String datavoto) {
        this.datavoto = datavoto;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getDataAss() {
        return dataAss;
    }

    public void setDataAss(String dataAss) {
        this.dataAss = dataAss;
    }

    public String getTipoAssenza() {
        return tipoAssenza;
    }

    public void setTipoAssenza(String tipoAssenza) {
        this.tipoAssenza = tipoAssenza;
    }

    public String getGiustifica() {
        return giustifica;
    }

    public void setGiustifica(String giustifica) {
        this.giustifica = giustifica;
    }

    public double getMedia() {
        return media;
    }

    public void setMedia(double media) {
        this.media = media;
    }
}