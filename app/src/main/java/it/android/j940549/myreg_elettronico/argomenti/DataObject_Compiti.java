package it.android.j940549.myreg_elettronico.argomenti;

/**
 * Created by J940549 on 30/12/2017.
 */

public class DataObject_Compiti {
    private String data;
    private String compiti;

    DataObject_Compiti(String unadata, String uncompito){
        data= unadata;
        compiti= uncompito;

    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCompiti() {
        return compiti;
    }

    public void setCompiti(String compiti) {
        this.compiti = compiti;
    }
}