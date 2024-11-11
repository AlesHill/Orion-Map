package com.example.galacticmape.Owner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.galacticmape.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class OwnerPlanetsActivity extends AppCompatActivity {

    private LinearLayout linearLayoutStates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner); // Используем макет из OwnerActivity

        linearLayoutStates = findViewById(R.id.linearLayoutStates); // Используем идентификатор контейнера из activity_owner
        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());

        loadStates();
    }

    private void loadStates() {
        try {
            File file = new File(getFilesDir(), "assets/corporationList.json");
            if (!file.exists()) {
                Log.e("OwnerActivity", "Файл corporationList.json не найден.");
                return;
            }

            // Чтение файла
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader reader = new BufferedReader(isr);

            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            // Закрываем потоки
            reader.close();
            isr.close();
            fis.close();

            // Парсим JSON
            JSONObject jsonObject = new JSONObject(jsonContent.toString());

            // Перебор всех государств в statesList.json
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject state = jsonObject.getJSONObject(key);
                String stateName = state.getString("title_name");
                String shortStateName = state.getString("owner_name");

                // Создание кнопки для каждого государства
                Button stateButton = new Button(this);
                stateButton.setText(stateName);
                stateButton.setBackgroundResource(R.drawable.button_background);
                stateButton.setTextColor(getResources().getColor(android.R.color.white));

                // Переход к списку планет государства при нажатии на кнопку
                stateButton.setOnClickListener(v -> {
                    Intent intent = new Intent(OwnerPlanetsActivity.this, OwnerPlanetDetailsActivity.class);
                    intent.putExtra("stateName", stateName);
                    intent.putExtra("shortStateName", shortStateName);
                    startActivity(intent);
                });

                // Добавляем кнопку в макет
                linearLayoutStates.addView(stateButton);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}