package it.android.j940549.myreg_elettronico.model;

import java.util.ArrayList;

/**
 * Created by J940549 on 30/12/2017.
 */

public class DataObject_MediaTotale {
    private ArrayList<Double> elencoVoti;
    private Double sommaVoti;
    private Double mediaVoti;

    DataObject_MediaTotale(){
        mediaVoti=0.0;
        elencoVoti=new ArrayList<>();
    }

    public ArrayList<Double> getElencoVoti() {
        return elencoVoti;
    }

    public void setElencoVoti(ArrayList<Double> elencoVoti) {
        this.elencoVoti = elencoVoti;
    }

    public Double getMediaVoti() {
        return mediaVoti;
    }

    public void setMediaVoti(Double mediaVoti) {
        this.mediaVoti = mediaVoti;
    }

    public void addVoto_a_media(Double voto){
        elencoVoti.add(voto);
        sommaVoti+=voto;
        mediaVoti=((sommaVoti/elencoVoti.size())*Math.pow(10,2))/100;


    }
}