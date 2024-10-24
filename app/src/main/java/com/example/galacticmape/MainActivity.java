package com.example.galacticmape;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация кнопок и экранов
        final View startScreen = findViewById(R.id.start_screen);
        final LinearLayout mainMenu = findViewById(R.id.main_menu);
        Button startButton = findViewById(R.id.start_button);
        Button milkyWayButton = findViewById(R.id.milky_way_button);
        Button magellanButton = findViewById(R.id.magellan_button);

        // Логика для кнопки "Начать"
        startButton.setOnClickListener(v -> {
            startScreen.setVisibility(View.GONE);
            mainMenu.setVisibility(View.VISIBLE);
        });

        // Логика кнопки "Млечный Путь"
        milkyWayButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivityLoading.class);
            startActivity(intent);
        });

        // Логика для кнопки "Магелланово Облако"
        magellanButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Эта карта недоступна на данный момент.")
                    .setPositiveButton("OK", (dialog, id) -> {
                        // Закрыть диалог
                    });
            builder.create().show();
        });
    }
}