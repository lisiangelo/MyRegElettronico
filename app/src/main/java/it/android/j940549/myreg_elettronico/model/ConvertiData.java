package it.android.j940549.myreg_elettronico.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by J940549 on 18/05/2017.
 */

public class ConvertiData {
    long datainmill;
    GregorianCalendar data;
    String datainSting="";

    public ConvertiData(){
        datainmill=0;
        data= new GregorianCalendar();
    }
    public String da_Millis_a_String(long mill){
        data.setTimeInMillis(mill*1000);
        datainSting=""+data.get(Calendar.DAY_OF_MONTH)+"/"+(data.get(Calendar.MONTH)+1)+"/"+data.get(Calendar.YEAR);
        return datainSting;
    }
    public long da_String_a_Millis(String data_string){
        String[]dataarray=data_string.split("/");
        String gg = dataarray[0];
        String mm = ""+ (Integer.parseInt(dataarray[1])-1);
        String aa = dataarray[2];
        int  anno = Integer.parseInt(aa);
        int mese = Integer.parseInt(mm);
        int giorno = Integer.parseInt(gg);
        data= new GregorianCalendar (anno,mese,giorno);
        datainmill=data.getTimeInMillis()/1000;
        return datainmill;
    }

}
