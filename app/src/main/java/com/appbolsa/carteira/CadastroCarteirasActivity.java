package com.appbolsa.carteira;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.appbolsa.R;
import com.appbolsa.Util.GeradorPrimaryKey;
import com.appbolsa.model.Carteira;
import com.google.android.material.textfield.TextInputEditText;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class CadastroCarteirasActivity extends AppCompatActivity {
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_carteiras);

        realm = Realm.getDefaultInstance();
    }

    public void cadastraNovaCarteira(View view) {
        TextInputEditText txt_carteira = findViewById(R.id.txt_nome_carteira);

        RealmResults<Carteira> carteiras = realm.where(Carteira.class).findAll();

        realm.executeTransaction(realm1 -> {
            Carteira carteira = realm1.createObject(Carteira.class, new GeradorPrimaryKey().proxPrimaryKeyCarteiras());
            carteira.nome = txt_carteira.getText().toString();
            carteira.dtCriacao = new Date();

            System.out.println("Tamanho da carteira " + carteiras.size());

            if (carteiras.size() == 1) {
                carteira.display = true;
            } else {
                carteira.display = false;
            }

        });

        DynamicToast.makeSuccess(this, "Carteira criada", 0).show();
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}