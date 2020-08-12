package com.appbolsa.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.appbolsa.R;
import com.appbolsa.model.Ativo;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;


public class AtivoRealmRecycleAdapter extends RealmRecyclerViewAdapter<Ativo, AtivoRealmRecycleAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private Realm realm;

    public AtivoRealmRecycleAdapter(Context context, Realm realm, @Nullable OrderedRealmCollection data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.context = context;
        this.realm = realm;

        setHasStableIds(true);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_carteira_ativos, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final Ativo vd = getItem(position);
        holder.ativo = vd;
        final long itemId = vd.codigo;

        holder.ticket.setText(vd.ticket);
        holder.preco.setText(new DecimalFormat().getCurrencyInstance().format(vd.preco));
        holder.cotacao.setText(new DecimalFormat().getCurrencyInstance().format(vd.cotacao));
        holder.qtde.setText(String.valueOf(vd.qtde));

        NumberFormat df = DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        df.setRoundingMode(RoundingMode.DOWN);
        holder.variacao.setText(df.format((vd.cotacao - vd.preco) / vd.preco * 100) + "%");

        if (vd.cotacao > vd.preco) {
            Drawable drawable = context.getDrawable(R.drawable.up);
            drawable.setTint(Color.GREEN);
            holder.status.setImageDrawable(drawable);
        } else {
            Drawable drawable = context.getDrawable(R.drawable.down);
            drawable.setTint(Color.RED);
            holder.status.setImageDrawable(drawable);
        }

    }


    @Override
    public long getItemId(int position) {
        return getItem(position).codigo;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public void filterResults(String text) {
        text = text == null ? null : text.toLowerCase().trim();
        RealmQuery<Ativo> query = realm.where(Ativo.class);
        if (!(text == null || "".equals(text))) {
            query.contains("ticket", text, Case.INSENSITIVE);
        }
        updateData(query.findAllAsync());
    }

    public Filter getFilter() {
        VendaFilter filter = new VendaFilter(this);
        return filter;
    }

    private class VendaFilter extends Filter {
        private final AtivoRealmRecycleAdapter adapter;

        private VendaFilter(AtivoRealmRecycleAdapter adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filterResults(constraint.toString());
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ticket;
        TextView qtde;
        TextView preco;
        TextView cotacao, variacao;
        ImageView logo;
        ImageView status;
        public Ativo ativo;


        MyViewHolder(View view) {
            super(view);
            ticket = view.findViewById(R.id.txt_ticket);
            cotacao = view.findViewById(R.id.txt_cotacao);
            preco = view.findViewById(R.id.txt_preco);
            logo = view.findViewById(R.id.img_logo);
            status = view.findViewById(R.id.img_status_cotacao);
            qtde = view.findViewById(R.id.txt_qtde);
            variacao = view.findViewById(R.id.txt_variacao);

        }
    }


}
