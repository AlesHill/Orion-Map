package com.example.galacticmape;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

public class MarkerDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_details);

        // Получаем данные из Intent
        String title = getIntent().getStringExtra("title");
        String system = getIntent().getStringExtra("system");
        String temperature = getIntent().getStringExtra("temperature");
        String state = getIntent().getStringExtra("state");
        String planetType = getIntent().getStringExtra("planet_type");
        String planetClass = getIntent().getStringExtra("planet_class");
        String economy = getIntent().getStringExtra("economy");
        String owner = getIntent().getStringExtra("owner");
        String corporation = getIntent().getStringExtra("corporation");
        String markerType = getIntent().getStringExtra("marker_type");
        boolean isCapital = getIntent().getBooleanExtra("is_capital", false);

        // Устанавливаем данные в TextView
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(title);  // Устанавливаем только название метки

        TextView systemTextView = findViewById(R.id.systemTextView);
        systemTextView.setText("Система: " + system);

        TextView temperatureTextView = findViewById(R.id.temperatureTextView);
        temperatureTextView.setText("Температура: " + temperature);

        TextView stateTextView = findViewById(R.id.stateTextView);
        stateTextView.setText("Состояние: " + state);

        TextView planetTypeTextView = findViewById(R.id.typeTextView);
        planetTypeTextView.setText("Тип: " + planetType);

        TextView planetClassTextView = findViewById(R.id.classTextView);
        planetClassTextView.setText("Класс: " + planetClass);

        TextView economyTextView = findViewById(R.id.economyTextView);
        economyTextView.setText("Экономика: " + economy);

        TextView ownerTextView = findViewById(R.id.ownerTextView);
        ownerTextView.setText("Гос.: " + owner);

        TextView corporationTextView = findViewById(R.id.corporationTextView);
        corporationTextView.setText("Корп.: " + corporation);

        TextView capitalTextView = findViewById(R.id.capitalTextView);
        capitalTextView.setText(isCapital ? "Столица: Да" : "Столица: Нет");

        TextView markerTypeTextView = findViewById(R.id.markerTypeTextView);
        markerTypeTextView.setText("Тип метки: " + markerType);

        // Обработка нажатия на кнопку "Назад"
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Завершение активности и возврат на карту
            finish();
        });
    }
}