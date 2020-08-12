package com.appbolsa.carteira;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appbolsa.R;
import com.appbolsa.Util.RecyclerItemClickListener;
import com.appbolsa.adapter.CarteirasRealmAdapter;
import com.appbolsa.model.Carteira;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import io.realm.Realm;
import io.realm.Sort;

public class CarteiraActivity extends AppCompatActivity {
    private Realm realm;
    private CarteirasRealmAdapter carteirasRealmAdapter;
    private RecyclerView lista;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carteira);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();

        lista = findViewById(R.id.listaAtivos);
        lista.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lista.setLayoutManager(layoutManager);
        lista.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        carteirasRealmAdapter = new CarteirasRealmAdapter(this, realm, realm.where(Carteira.class).findAll().sort("nome", Sort.DESCENDING), true);
        lista.setAdapter(carteirasRealmAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent ctr = new Intent(CarteiraActivity.this, CadastroCarteirasActivity.class);
            startActivity(ctr);
        });

        //ação ao clicar nas carteiras
        lista.addOnItemTouchListener(new RecyclerItemClickListener(this, lista, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Intent addAtivo = new Intent(CarteiraActivity.this, AddAtivoActivity.class);
                        addAtivo.putExtra("id", lista.getAdapter().getItemId(position));
                        startActivity(addAtivo);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Carteira carteiraDelete = realm.where(Carteira.class)
                                .equalTo("id", lista.getAdapter().getItemId(position))
                                .findFirst();

                        realm.executeTransaction(realm -> carteiraDelete.deleteFromRealm());
                        DynamicToast.makeWarning(getApplicationContext(), "Carteira deletada", 0).show();
                    }
                })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}