package com.example.galacticmape;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MarkerDetailsActivity extends AppCompatActivity {

    private Map<String, String> ownerFullNameMap;
    private Map<String, String> corporationFullNameMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_details);

        // Инициализируем словарь для полного названия государств
        ownerFullNameMap = new HashMap<>();
        ownerFullNameMap.put("Орион", "Орионская Советская Федеративная Социалистическая Республика");
        ownerFullNameMap.put("Империя", "Великая Галактическая Империя");
        ownerFullNameMap.put("Сайдония", "Империя Сайдонского Веикодержавия");
        ownerFullNameMap.put("Содружество Звёзд", "Содружество Звёзд");
        ownerFullNameMap.put("Свободный Эйдем", "Свободный Эйдем");
        ownerFullNameMap.put("ССМП", "Соединённые Сектора Млечного Пути");
        ownerFullNameMap.put("Соц.Интерн", "Социалистический Интернационал");
        ownerFullNameMap.put("Тени", "Объеденённый Тени");
        ownerFullNameMap.put("NeIRo Gestalt", "NeIRo Gestalt");
        ownerFullNameMap.put("Нейтралы", "Нейтралы");

        corporationFullNameMap = new HashMap<>();
        corporationFullNameMap.put("Картана", "Государственная Мега-Корпорация Картана Элит Индастриз");
        corporationFullNameMap.put("Гиалис", "Государственная Корпорация Гиалис");
        corporationFullNameMap.put("Сегинус", "Сегинус Корпорацион");
        corporationFullNameMap.put("Инара", "Инари Биохемикалс");
        corporationFullNameMap.put("Доксфорд", "Доксфордская Компания");
        corporationFullNameMap.put("Великий Легион", "Великий Легион");
        corporationFullNameMap.put("Багровый Закат", "Багровый Закат");
        corporationFullNameMap.put("Гос.Корп.", "Гос.Корп.");
        corporationFullNameMap.put("Шедоу", "Организация Шедоу");
        corporationFullNameMap.put("Gestalt", "Gestalt Corp.");
        corporationFullNameMap.put("", "Отсутствуют");

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

        // Проверяем мапу на наличие полного названия государства
        String fullOwnerName = ownerFullNameMap.containsKey(owner) ? ownerFullNameMap.get(owner) : owner;

        // Проверяем мапу на наличие полного названия корпорации
        String fullCorporationName = corporationFullNameMap.containsKey(corporation) ? corporationFullNameMap.get(corporation) : corporation;

        // Устанавливаем название метки (без

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
        ownerTextView.setText("Гос.: " + fullOwnerName);

        TextView corporationTextView = findViewById(R.id.corporationTextView);
        corporationTextView.setText("Корп.: " + fullCorporationName);

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