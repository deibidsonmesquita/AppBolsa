package com.appbolsa.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Indices extends RealmObject {
    @PrimaryKey
    private int codigo;
    public Double ibov;
    public Double ewz;
    public Double bitcoin;
    public Double sp500;
    public Double nasdaq;
    public Double ifix;

    public Indices() {
    }

    public Indices(int codigo) {
        this.codigo = codigo;
    }
}
