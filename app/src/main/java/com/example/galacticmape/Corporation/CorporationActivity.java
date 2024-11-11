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
import java.util.Iterator;

public class CorporationActivity extends AppCompatActivity {

    private LinearLayout linearLayoutCorporation;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceCorporation) {
        super.onCreate(savedInstanceCorporation);
        setContentView(R.layout.activity_corporation);

        linearLayoutCorporation = findViewById(R.id.linearLayoutCorporation);
        buttonBack = findViewById(R.id.buttonBack);

        loadCorporations();

        buttonBack.setOnClickListener(v -> {
            finish(); // Возвращаемся на карту
        });
    }

    private void loadCorporations() {
        try {
            File file = new File(getFilesDir(), "assets/corporationList.json");
            if (!file.exists()) {
                Log.e("OwnerActivity", "Файл statesList.json не найден.");
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

            // Итерация по ключам JSON объекта для добавления кнопок государств
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String corporationKey = keys.next();
                JSONObject corporationObject = jsonObject.getJSONObject(corporationKey);
                String corporationTitle = corporationObject.getString("title_name");

                Button corporationButton = new Button(this);
                corporationButton.setText(corporationTitle);
                corporationButton.setBackground(getResources().getDrawable(R.drawable.button_background));
                corporationButton.setTextColor(getResources().getColor(android.R.color.white));
                corporationButton.setOnClickListener(v -> {
                    Intent intent = new Intent(CorporationActivity.this, CorporationDetailsActivity.class);
                    intent.putExtra("corporationId", corporationKey); // Передаем идентификатор государства
                    startActivity(intent); // Запускаем новую Activity
                });

                linearLayoutCorporation.addView(corporationButton);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}