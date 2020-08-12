package com.appbolsa.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Ativo extends RealmObject {
    @PrimaryKey
    public long codigo;
    public String ticket;
    public Double preco;
    public Double cotacao;
    public int qtde;
}
