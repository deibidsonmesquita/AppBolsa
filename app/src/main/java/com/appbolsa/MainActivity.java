package com.appbolsa;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appbolsa.Util.GraficoActivity;
import com.appbolsa.carteira.AddAtivoActivity;
import com.appbolsa.carteira.CarteiraActivity;
import com.appbolsa.carteira.ListagemAtivosActivity;
import com.appbolsa.model.Ativo;
import com.appbolsa.model.Carteira;
import com.appbolsa.model.Indices;
import com.appbolsa.services.CotacaoService;
import com.appbolsa.services.MyResultReceiver;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class MainActivity extends AppCompatActivity {
    private Realm realm;
    public MyResultReceiver receiver;

    private TextView txt_capital,
            txt_vl_ibov,
            txt_vl_sp,
            txt_vl_bitcoin,
            txt_vl_ewz,
            txt_vl_nasdaq,
            txt_vl_ifix;

    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        preparaUI();

        cotacaoIndices();
        geraComposicaoCarteiraPieChart();

        FloatingActionButton fab1 = findViewById(R.id.fab_add);
        fab1.setOnClickListener(view -> {
            Intent add = new Intent(this, AddAtivoActivity.class);
            startActivity(add);
        });

        //inicia o serviço de cotação historica
        setupServiceReceiver();
        onStartServiceCotacao();

    }

    // Starts the IntentService
    public void onStartServiceCotacao() {
        Intent i = new Intent(this, CotacaoService.class);
        i.putExtra("receiver", receiver);
        startService(i);
    }

    // Setup the callback for when data is received from the service
    public void setupServiceReceiver() {
        receiver = new MyResultReceiver(new Handler());

        receiver.setReceiver(new MyResultReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == RESULT_OK) {
                    cotacaoIndices();
                }
            }
        });
    }


    private void preparaUI() {
        txt_vl_ibov = findViewById(R.id.txt_vl_ibov);
        txt_vl_bitcoin = findViewById(R.id.txt_vl_bitcoin);
        txt_vl_ewz = findViewById(R.id.txt_vl_ewz);
        txt_vl_sp = findViewById(R.id.txt_vl_sp);
        txt_vl_nasdaq = findViewById(R.id.txt_vl_nasdaq);
        txt_vl_ifix = findViewById(R.id.txt_vl_ifix);
        txt_capital = findViewById(R.id.txt_capital);
        pieChart = findViewById(R.id.piechart);
    }

    private void cotacaoIndices() {
        new Cotacao().execute();
        TextView update = findViewById(R.id.txt_update_info);
        update.setText("Última atualização " + new SimpleDateFormat("dd/MM/yyyy hh:MM:ss").format(new Date()));
    }

    public void geraComposicaoCarteiraPieChart() {
        ArrayList<PieEntry> NoOfEmp = new ArrayList();

        Carteira carteira = realm.where(Carteira.class).equalTo("display", true).findFirst();

        if (carteira != null) {
            for (Ativo a : carteira.ativos) {
                NoOfEmp.add(new PieEntry(a.qtde, a.ticket));
            }
        }
        PieDataSet dataSet = new PieDataSet(NoOfEmp, "");
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setValueTextSize(8f);
        dataSet.setValueTextColor(Color.GRAY);

        PieData data = new PieData(dataSet);

        pieChart.setCenterTextSize(10f);
        pieChart.setCenterTextColor(Color.GRAY);
        pieChart.setCenterText("Ativos");
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateXY(2000, 2000);

    }

    private void updateValoresIndices() {

        final Indices index = realm.where(Indices.class).findFirst();

        if (index != null) {
            txt_vl_ibov.setText(String.valueOf(index.ibov));
            txt_vl_bitcoin.setText("$ " + index.bitcoin);
            txt_vl_ewz.setText(String.valueOf(index.ewz));
            txt_vl_sp.setText(String.valueOf(index.sp500));
            txt_vl_ifix.setText(String.valueOf(index.ifix));
            txt_vl_nasdaq.setText(String.valueOf(index.nasdaq));
            txt_capital.setText(DecimalFormat.getCurrencyInstance().format(totalCapitalInvestido()));
        }
    }

    private BigDecimal totalCapitalInvestido() {
        RealmResults<Ativo> ativos = realm.where(Ativo.class).findAll();
        return new BigDecimal(ativos.stream().mapToDouble(a -> a.qtde * a.preco).sum());
    }

    public void updateCotacaoAndCapital(View view) {
        cotacaoIndices();
    }

    public void mostrarDetalhesAtivosGrafico(View view) {
        Intent i = new Intent(this, ListagemAtivosActivity.class);
        startActivity(i);
    }


    public class Cotacao extends AsyncTask<String, Void, Void> {

        private Realm realm;

        @Override
        protected Void doInBackground(String... strings) {
            if (isConnected()) {
                try {

                    realm = Realm.getDefaultInstance();

                    String[] symbols = new String[]{"^BVSP", "EWZ", "BTC-USD", "^GSPC", "^IXIC", "IFIX.SA"};
                    Map<String, Stock> stocks = YahooFinance.get(symbols);

                    Indices idx = new Indices(1);
                    realm.executeTransaction(realm1 -> {
                        Indices indices = realm.copyToRealmOrUpdate(idx);
                        indices.bitcoin = stocks.get("BTC-USD").getQuote().getPrice().doubleValue();
                        indices.ewz = stocks.get("EWZ").getQuote().getPrice().doubleValue();
                        indices.ibov = stocks.get("^BVSP").getQuote().getPrice().doubleValue();
                        indices.ifix = stocks.get("IFIX.SA").getQuote().getPrice().doubleValue();
                        indices.nasdaq = stocks.get("^IXIC").getQuote().getPrice().doubleValue();
                        indices.sp500 = stocks.get("^GSPC").getQuote().getPrice().doubleValue();
                    });

                    //grava cotacoes atuais dos ativos
                    final RealmResults<Ativo> ativosCarteira = realm.where(Ativo.class).findAll();

                    if (!ativosCarteira.isEmpty()) {
                        String[] acoes = new String[ativosCarteira.size()];

                        int cont = 0;
                        for (Ativo a : ativosCarteira) {
                            acoes[cont] = a.ticket + ".SA";
                            cont++;
                        }

                        Map<String, Stock> cotacoesAtuais = YahooFinance.get(acoes);

                        realm.executeTransaction(realm1 -> {
                            int contador = 0;
                            for (Ativo a : ativosCarteira) {
                                a.cotacao = cotacoesAtuais.get(acoes[contador]).getQuote().getPrice().doubleValue();
                                contador++;
                            }
                        });
                    }

                    System.out.println("dados salvos com sucesso");

                } catch (RealmException | IOException ex) {
                    System.out.println(ex.getMessage());
                } finally {
                    realm.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateValoresIndices();
            DynamicToast.makeSuccess(getApplicationContext(), "Cotações atualizadas", 0).show();
        }
    }

    // verifica se dispositivo tem internet
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
            
        } catch (Exception e) {
            DynamicToast.makeError(getApplicationContext(), "Sem conexão com a internet", 0).show();
        }
        return connected;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_game:
                Intent carteira = new Intent(this, CarteiraActivity.class);
                startActivity(carteira);
                return true;
            case R.id.help:
                Intent g = new Intent(this, GraficoActivity.class);
                startActivity(g);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        geraComposicaoCarteiraPieChart();
        updateValoresIndices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


}