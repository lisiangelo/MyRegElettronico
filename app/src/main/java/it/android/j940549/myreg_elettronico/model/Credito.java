package it.android.j940549.myreg_elettronico.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by J940549 on 21/03/2018.
 */

public class Credito {
    private int anno,anno_prev;
    private int voto;
    private String credito;
    private Map<Integer,String> annoI;
    private Map<Integer,String> annoII;
    private Map<Integer,String> annoIII;

    private Map<Integer,String> annoI_pre;
    private Map<Integer,String> annoII_pre;
    private Map<Integer,String> annoIII_pre;

    public Credito(){
        anno=0;
        voto=0;
        credito="0";
        annoI=new HashMap<>();
        annoII=new HashMap<>();
        annoIII=new HashMap<>();
        annoI_pre=new HashMap<>();
        annoII_pre=new HashMap<>();
        annoIII_pre=new HashMap<>();
        popolaMappe();
    }

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }
    public int getAnno_prev() {
        return anno_prev;
    }

    public void setAnno_prev(int anno_prev) {
        this.anno_prev = anno_prev;
    }

    public int getVoto() {
        return voto;
    }

    public void setVoto(int voto) {
        this.voto = voto;
    }

    public String getCredito() {
        return credito;
    }

    public void setCredito(String credito) {
        this.credito = credito;
    }

    public String calcolaCredito(){

        if(voto!=0&&anno!=0){
            if(anno==3){
                credito=annoI.get(voto);
            }
            if(anno==4){
                credito=annoII.get(voto);
            }
            if(anno==5){
                credito=annoIII.get(voto);
            }

        }
        if(voto!=0&&anno_prev!=0&&anno==0){
            if(anno_prev==3){
                credito=annoI_pre.get(voto);
            }
            if(anno_prev==4){
                credito=annoII_pre.get(voto);
            }
            if(anno_prev==5){
                credito=annoIII_pre.get(voto);
            }

        }

        return credito;
    }

public void popolaMappe(){

    annoI.put(1,"7-8 di 13");
    annoI.put(2,"8-9 di 13");
    annoI.put(3,"10-11 di 13");
    annoI.put(4,"11-12 di 13");
    annoI.put(5,"12-13 di 13");

    annoII.put(1,"8-9 di 13");
    annoII.put(2,"9-10 di 13");
    annoII.put(3,"10-11 di 13");
    annoII.put(4,"11-12 di 13");
    annoII.put(5,"12-13 di 13");

    annoIII.put(1,"9-10 di 15");
    annoIII.put(2,"10-11 di 15");
    annoIII.put(3,"11-12 di 15");
    annoIII.put(4,"13-14 di 15");
    annoIII.put(5,"14-15 di 15");

    annoI_pre.put(1,"3-4 di 8");
    annoI_pre.put(2,"4-5 di 8");
    annoI_pre.put(3,"5-6 di 8");
    annoI_pre.put(4,"6-7 di 8");
    annoI_pre.put(5,"7-8 di 8");

    annoII_pre.put(1,"3-4 di 8");
    annoII_pre.put(2,"4-5 di 8");
    annoII_pre.put(3,"5-6 di 8");
    annoII_pre.put(4,"6-7 di 8");
    annoII_pre.put(5,"7-8 di 8");

    annoIII_pre.put(1,"4-5 di 9");
    annoIII_pre.put(2,"5-6 di 9");
    annoIII_pre.put(3,"6-7 di 9");
    annoIII_pre.put(4,"7-8 di 9");
    annoIII_pre.put(5,"8-9 di 9");

}
}
