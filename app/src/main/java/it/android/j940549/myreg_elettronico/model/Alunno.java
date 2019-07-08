package it.android.j940549.myreg_elettronico.model;

import java.io.Serializable;

/**
 * Created by J940549 on 30/12/2017.
 */

public class Alunno implements Serializable {
    private String id_alunno,nome_alunno, cod_alunno, classe_sez;
    private String cfIstituto,nome_istituto, tipo,indirizzo,comune;
    private String userid,password;

    public Alunno(){

    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public String getNome_alunno() {
        return nome_alunno;
    }

    public void setNome_alunno(String nome_alunno) {
        this.nome_alunno = nome_alunno;
    }

    public String getCod_alunno() {
        return cod_alunno;
    }

    public void setCod_alunno(String cod_alunno) {
        this.cod_alunno = cod_alunno;
    }

    public String getClasse_sez() {
        return classe_sez;
    }

    public void setClasse_sez(String classe_sez) {
        this.classe_sez = classe_sez;
    }

    public String getCfIstituto() {
        return cfIstituto;
    }

    public void setCfIstituto(String cfIstituto) {
        this.cfIstituto = cfIstituto;
    }

    public String getNome_istituto() {
        return nome_istituto;
    }

    public void setNome_istituto(String nome_istituto) {
        this.nome_istituto = nome_istituto;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId_alunno() {
        return id_alunno;
    }

    public void setId_alunno(String id_alunno) {
        this.id_alunno = id_alunno;
    }
}