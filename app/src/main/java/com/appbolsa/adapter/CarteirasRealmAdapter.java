package com.appbolsa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.appbolsa.R;
import com.appbolsa.model.Carteira;

import java.text.SimpleDateFormat;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class CarteirasRealmAdapter extends RealmRecyclerViewAdapter<Carteira, CarteirasRealmAdapter.MyViewHolder> {
    private Context context;
    private Realm realm;

    public CarteirasRealmAdapter(Context context, Realm realm, @Nullable OrderedRealmCollection<Carteira> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.context = context;
        this.realm = realm;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_carteira, parent, false);
        return new CarteirasRealmAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Carteira vd = getItem(position);
        holder.carteira = vd;
        final long itemId = vd.id;

        holder.nome.setText(vd.nome);
        holder.data.setText(new SimpleDateFormat("dd/MM/yyyy").format(vd.dtCriacao));

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nome, data;
        Carteira carteira;

        public MyViewHolder(@NonNull View view) {
            super(view);
            nome = view.findViewById(R.id.txt_nome_carteiras);
            data = view.findViewById(R.id.txt_data_carteiras);
        }
    }

    public long getItemId(int position) {
        return getData().get(position).id;
    }
}
