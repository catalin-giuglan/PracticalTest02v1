package com.example.practicaltest02v1.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import android.os.Build;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProcessingThread extends Thread {

    private Context context;
    private String queryPrefix;

    // Actiunea pentru Broadcast
    public static final String ACTION_AUTOCOMPLETE = "ro.pub.cs.systems.eim.practicaltest02v1.autocomplete";
    public static final String DATA_RESULT = "result";

    public ProcessingThread(Context context, String queryPrefix) {
        this.context = context;
        this.queryPrefix = queryPrefix;
    }

    @Override
    public void run() {
        Log.d("[ProcessingThread]", "Thread started for query: " + queryPrefix);
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        try {
            // 3a) Primirea informatiilor de la serviciul web
            String urlString = "https://www.google.com/complete/search?client=chrome&q=" + queryPrefix;
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();

            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String result = stringBuilder.toString();

            // Logare raspuns complet (Cerinta 3a)
            Log.d("[ProcessingThread]", "Full response: " + result);

            // 3b) Parsarea stringurilor (Google returneaza un array de array-uri)
            // Format: ["cafea", ["cafea", "cafeaua", "cafea boabe", ...], ...]
            JSONArray jsonArray = new JSONArray(result);
            JSONArray suggestions = jsonArray.getJSONArray(1);

            String resultToSend = "N/A";

            // Afisare intrarea a 3-a in LogCat (Cerinta 3b)
            if (suggestions.length() > 2) {
                String thirdSuggestion = suggestions.getString(2); // index 2 inseamna a 3-a
                Log.d("[ProcessingThread]", "Third suggestion: " + thirdSuggestion);
                resultToSend = thirdSuggestion;
            } else {
                Log.d("[ProcessingThread]", "Not enough suggestions for index 2");
            }

            // Construim un string cu toate sugestiile pentru afisare (Cerinta Punctul 1 final)
            // "doar valorile propuse separate cu virgula si succedate de newline"
            StringBuilder allSuggestions = new StringBuilder();
            for (int i = 0; i < suggestions.length(); i++) {
                allSuggestions.append(suggestions.getString(i)).append(",\n");
            }


            // 3c) Trimiterea prin Broadcast
            Intent intent = new Intent();
            intent.setAction(ACTION_AUTOCOMPLETE);
            // Putem trimite doar a 3-a (cum zice la 3c legat de 3b) sau toate (cum zice la 1)
            // Le trimit pe toate ca sa arate bine in UI, dar logica e aceeasi
            intent.putExtra(DATA_RESULT, allSuggestions.toString());
            context.sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}