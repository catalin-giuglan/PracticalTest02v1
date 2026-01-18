package com.example.practicaltest02v1;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.practicaltest02v1.network.ProcessingThread;

public class PracticalTest02v1MainActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton;
    private TextView resultTextView;
    private Button navigateButton; // Pentru harti mai tarziu

    // Definim BroadcastReceiver-ul
    private MessageBroadcastReceiver messageBroadcastReceiver = new MessageBroadcastReceiver();
    private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(ProcessingThread.DATA_RESULT);
            // Afisam in UI (Cerinta 3c)
            resultTextView.setText(result);
            Toast.makeText(getApplicationContext(), "Received Broadcast!", Toast.LENGTH_SHORT).show();
        }
    }

    // Filtrul pentru a asculta doar actiunea noastra
    private IntentFilter intentFilter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02v1_main);

        searchEditText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        resultTextView = findViewById(R.id.result_text_view);
        navigateButton = findViewById(R.id.navigate_to_maps_button);

        // Adaugam actiunea la filtru
        intentFilter.addAction(ProcessingThread.ACTION_AUTOCOMPLETE);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString();
                if (query.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please insert query", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Pornim Thread-ul
                ProcessingThread processingThread = new ProcessingThread(getApplicationContext(), query);
                processingThread.start();
            }
        });

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), com.example.practicaltest02v1.MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verificam daca rulam pe Android 13 (Tiramisu) sau mai nou
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Specificam explicit ca receiverul este vizibil (EXPORTED)
            registerReceiver(messageBroadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED);
        } else {
            // Pentru versiuni mai vechi, metoda clasica
            registerReceiver(messageBroadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        // Il dez-inregistram cand plecam din activitate ca sa nu avem leak-uri
        unregisterReceiver(messageBroadcastReceiver);
        super.onPause();
    }
}