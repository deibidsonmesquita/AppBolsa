package com.appbolsa.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Carteira extends RealmObject {
    @PrimaryKey
    public long id;
    public String nome;
    public Date dtCriacao;
    public RealmList<Ativo> ativos;
    public boolean display;
}
