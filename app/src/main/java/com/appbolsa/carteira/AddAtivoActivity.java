package com.appbolsa.carteira;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.appbolsa.R;
import com.appbolsa.Util.GeradorPrimaryKey;
import com.appbolsa.model.Ativo;
import com.appbolsa.model.Carteira;
import com.google.android.material.textfield.TextInputEditText;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.travijuu.numberpicker.library.NumberPicker;

import io.realm.Realm;
import io.realm.RealmResults;

public class AddAtivoActivity extends AppCompatActivity {
    private NumberPicker numberPicker;
    private TextInputEditText ticket, preco;
    private Realm realm;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ativo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        realm = Realm.getDefaultInstance();

        numberPicker = findViewById(R.id.number_picker);
        preco = findViewById(R.id.txt_preco_ativo);
        ticket = findViewById(R.id.txt_ticket_ativo);
        spinner = findViewById(R.id.spinner_carteiras);

        numberPicker.setMax(15000);
        numberPicker.setMin(100);
        numberPicker.setUnit(100);
        numberPicker.setValue(100);

        RealmResults<Carteira> carteiras = realm.where(Carteira.class).findAll();
        String[] nomes = new String[carteiras.size()];
        for (int i = 0; i < carteiras.size(); i++) {
            nomes[i] = carteiras.get(i).nome;
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, nomes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    public void adicionarAtivo(View view) {
        if (!ticket.getText().toString().isEmpty()) {

            final Carteira carteira = realm.where(Carteira.class)
                    .equalTo("nome", spinner.getSelectedItem().toString())
                    .findFirst();

            realm.executeTransaction(realm -> {
                Ativo ativo = realm.createObject(Ativo.class, new GeradorPrimaryKey().proxPrimaryKeyAtivos());
                ativo.ticket = ticket.getText().toString().toUpperCase();
                ativo.preco = Double.parseDouble(preco.getText().toString());
                ativo.qtde = numberPicker.getValue();
                ativo.cotacao = 0.0;

                carteira.ativos.add(ativo);
            });
            DynamicToast.makeSuccess(this, "Ativo adicionado", 0).show();
            finish();
        } else {
            DynamicToast.makeWarning(this, "Informe todos os dados", 0).show();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}