package com.appbolsa.carteira;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appbolsa.R;
import com.appbolsa.adapter.AtivoRealmRecycleAdapter;
import com.appbolsa.model.Ativo;

import io.realm.Realm;

public class ListagemAtivosActivity extends AppCompatActivity {
    private RecyclerView listaRecyclerView;
    private AtivoRealmRecycleAdapter mAdapter;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem_ativos);

        realm = Realm.getDefaultInstance();
        listaRecyclerView = findViewById(R.id.lista_ativos_view);
        setupRecycler(realm);


    }

    private void setupRecycler(Realm realm) {

        // Configurando o gerenciador de layout para ser uma lista.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listaRecyclerView.setLayoutManager(layoutManager);

        // Adiciona o adapter que irá anexar os objetos à lista.
        // Está sendo criado com lista vazia, pois será preenchida posteriormente.
        mAdapter = new AtivoRealmRecycleAdapter(this, realm, realm.where(Ativo.class).findAll(), true);
        listaRecyclerView.setAdapter(mAdapter);

        // Configurando um dividr entre linhas, para uma melhor visualização.
        listaRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}