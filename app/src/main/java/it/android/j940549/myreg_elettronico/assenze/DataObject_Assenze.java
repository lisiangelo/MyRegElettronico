package it.android.j940549.myreg_elettronico.assenze;

/**
 * Created by J940549 on 30/12/2017.
 */

public class DataObject_Assenze {
    private String data;
    private String tipoassenza;
    private String giustificazione;

    public DataObject_Assenze(String unadata, String untipoassenza, String unagiustificazione){
        data= unadata;
        tipoassenza= untipoassenza;
        giustificazione=unagiustificazione;

    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTipoassenza() {
        return tipoassenza;
    }

    public void setTipoassenza(String tipoassenza) {
        this.tipoassenza = tipoassenza;
    }

    public String getGiustificazione() {
        return giustificazione;
    }

    public void setGiustificazione(String giustificazione) {
        this.giustificazione = giustificazione;
    }
}