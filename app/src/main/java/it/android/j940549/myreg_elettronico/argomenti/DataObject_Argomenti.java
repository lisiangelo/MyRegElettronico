package it.android.j940549.myreg_elettronico.argomenti;

import it.android.j940549.myreg_elettronico.model.ConvertiData;

/**
 * Created by J940549 on 30/12/2017.
 */

public class DataObject_Argomenti {
    private String data;
    private String argomenti;
    private String note;

    DataObject_Argomenti(String unadata, String unargomento,String unanota){
        data= unadata;
        argomenti= unargomento;
        note=unanota;

    }

    public String getData() {
        return data;
    }

    public void setData(long data) {

        this.data = new ConvertiData().da_Millis_a_String(data);
    }

    public String getArgomenti() {
        return argomenti;
    }

    public void setArgomenti(String argomenti) {
        this.argomenti = argomenti;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}