package com.appbolsa.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import java.io.IOException;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class CotacaoService extends IntentService {

    public CotacaoService() {
        super("CotacaoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            System.out.println("Serviço de atualização rodando..");

            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {

                    try {
                        while (true) {
                            Thread.sleep(19000);

                            String[] symbols = new String[]{"^BVSP", "EWZ", "BTC-USD", "^GSPC", "^IXIC", "IFIX.SA"};
                            Map<String, Stock> cotacoesAtuais = YahooFinance.get(symbols);
                            System.out.println(cotacoesAtuais.get("BTC-USD").getQuote().getPrice().doubleValue());

                            //codigo responsavel por enviar a resposta do serviço de cotacao para atividade main
                            ResultReceiver rec = intent.getParcelableExtra("receiver");
                            Bundle bundle = new Bundle();
                            rec.send(Activity.RESULT_OK, bundle);
                            System.out.println("Atualizando cotação ok..");

                        }
                    } catch (IOException | InterruptedException e) {

                        System.out.println("erro ao executar o serviço de cotações");
                    }
                }

            });

        }
    }

}
