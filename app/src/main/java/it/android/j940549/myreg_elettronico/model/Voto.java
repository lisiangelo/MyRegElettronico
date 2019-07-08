package it.android.j940549.myreg_elettronico.model;

import android.util.Log;

/**
 * Created by J940549 on 30/12/2017.
 */

public class Voto {

    String alunno, quadrim;
    long data;
    String materia;
    String tipo;
    String voto;
    String commento;
    String docente;
    String TAG_LOG="voto_oggetto";
    public Voto() {
        alunno = "";
        quadrim="";
        data = 0;
        materia = "";
        tipo = "";
        voto = "";
        commento = "";
        docente = "";
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

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        if (materia.contains("'")) {
            System.out.println(materia);
            materia=materia.replaceAll("\'", " ");
            System.out.println(materia);
        }
        this.materia = materia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getVoto() {
        return voto;
    }

    public void setVoto(String voto) {
        Log.i(TAG_LOG,"voto.."+voto);
        if (isDoubleOrInt(voto)) {
            if (voto.contains(",")) {
                voto = voto.replaceAll(",", ".").trim();
            }
            if (voto.contains("+")) {
                voto = voto.replaceAll("\\+", " ").trim();
                float votox = Float.parseFloat(voto);
                votox = (float) (votox + 0.25);
                voto = "" + votox;
                Log.i(TAG_LOG,"voto + corretto  "+ voto);
            }
            if (voto.contains("-")||voto.contains("–")) {
                voto = voto.replaceAll("-", " ").trim();
                voto = voto.replaceAll("–", " ").trim();
                float votox = Float.parseFloat(voto);
                votox = (float) (votox - 0.25);
                voto = "" + votox;
                Log.i(TAG_LOG,"voto - corretto  "+ voto);
            }
        } else {
            voto = "0";
        }
        this.voto = voto;
    }

    public boolean isDoubleOrInt(String num) {
        boolean result=false;

        try{
            if (num.contains(",")) {
                num = num.replaceAll(",", ".");
            }
            if (num.contains("+")) {
                num = num.replaceAll("\\+", " ").trim();

            }
            if (num.contains("-")||num.contains("–")) {
                num = num.replaceAll("\\-", " ").trim();
                num = num.replaceAll("–", " ").trim();
            }
            Log.i(TAG_LOG,"numero dopo i controlli..  " + num);
        }catch(Exception e){
            Log.i(TAG_LOG,"errore in isDoubleOrInt..  " + e.toString());
        }
        try {
            Integer.parseInt(num);
            result= true;
        } catch (Exception e) {
            try {
                Double.parseDouble(num);
                result=true;
            } catch (Exception ex) {
                result=false;
                System.out.println(ex.toString());
            }
        }
        Log.i(TAG_LOG,"numero da valutarre..  " + num+" vero/falso "+result);
        return result;
    }

    public String getCommento() {
        return commento;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public String getDocente() {
        return docente;
    }

    public void setDocente(String docente) {
        this.docente = docente;
    }

    public String getQuadrim() {
        return quadrim;
    }

    public void setQuadrim(String quadrim) {
        this.quadrim = quadrim;
    }
}
