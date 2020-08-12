package com.appbolsa.Util;

import com.appbolsa.model.Ativo;
import com.appbolsa.model.Carteira;

import io.realm.Realm;
import io.realm.RealmResults;

public class GeradorPrimaryKey {
    private Realm realm;

    public GeradorPrimaryKey() {
        this.realm = Realm.getDefaultInstance();
    }

    public int proxPrimaryKeyAtivos() {
        int key;
        try {
            RealmResults<Ativo> results = realm.where(Ativo.class).findAll();
            if (results.size() > 0) {
                key = realm.where(Ativo.class)
                        .max("codigo").intValue() + 1;
            } else {
                key = 1;
            }
        } finally {
            this.realm.close();
        }
        return key;
    }

    public int proxPrimaryKeyCarteiras() {
        int key;
        try {
            RealmResults<Carteira> results = realm.where(Carteira.class).findAll();
            if (results.size() > 0) {
                key = realm.where(Carteira.class)
                        .max("id").intValue() + 1;
            } else {
                key = 1;
            }
        } finally {
            this.realm.close();
        }
        return key;
    }

}

