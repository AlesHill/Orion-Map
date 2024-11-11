package com.example.galacticmape.Corporation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

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

public class CorporationPlanetsActivity extends AppCompatActivity {

    private LinearLayout linearLayoutCorporation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corporation); // Используем макет из OwnerActivity

        linearLayoutCorporation = findViewById(R.id.linearLayoutCorporation); // Используем идентификатор контейнера из activity_owner
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
                String corpName = state.getString("title_name");
                String shortCorpName = state.getString("corporation_names");

                // Создание кнопки для каждого государства
                Button stateButton = new Button(this);
                stateButton.setText(corpName);
                stateButton.setBackgroundResource(R.drawable.button_background);
                stateButton.setTextColor(getResources().getColor(android.R.color.white));

                // Переход к списку планет государства при нажатии на кнопку
                stateButton.setOnClickListener(v -> {
                    Intent intent = new Intent(CorporationPlanetsActivity.this, CorporationPlanetDetailsActivity.class);
                    intent.putExtra("corpName", corpName);
                    intent.putExtra("shortCorpName", shortCorpName);
                    startActivity(intent);
                });

                // Добавляем кнопку в макет
                linearLayoutCorporation.addView(stateButton);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}