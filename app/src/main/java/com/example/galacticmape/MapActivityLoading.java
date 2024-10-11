package com.example.galacticmape;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MapActivityLoading extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView progressText;
    private Handler handler = new Handler();
    private int progressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        // Запуск процесса загрузки
        startLoadingProcess();
    }

    private void startLoadingProcess() {
        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus++;
                handler.post(() -> {
                    progressBar.setProgress(progressStatus);
                    progressText.setText(progressStatus + "%");

                    if (progressStatus == 100) {
                        // Переход на карту после завершения загрузки
                        Intent intent = new Intent(MapActivityLoading.this, MapActivityMilkyWay.class);
                        startActivity(intent);
                        finish();
                    }
                });
                try {
                    Thread.sleep(50);  // Имитация времени загрузки
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}