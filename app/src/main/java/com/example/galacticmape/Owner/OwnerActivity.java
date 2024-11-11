package com.example.galacticmape.Owner;

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
import java.io.InputStreamReader;
import java.util.Iterator;

public class OwnerActivity extends AppCompatActivity {
    private LinearLayout linearLayoutStates;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        linearLayoutStates = findViewById(R.id.linearLayoutStates);
        buttonBack = findViewById(R.id.buttonBack);

        loadStates();

        buttonBack.setOnClickListener(v -> {
            finish(); // Возвращаемся на карту
        });
    }

    private void loadStates() {
        try {
            File file = new File(getFilesDir(), "assets/statesList.json");
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
                String stateKey = keys.next();
                JSONObject stateObject = jsonObject.getJSONObject(stateKey);
                String stateTitle = stateObject.getString("title_name");

                Button stateButton = new Button(this);
                stateButton.setText(stateTitle);
                stateButton.setBackground(getResources().getDrawable(R.drawable.button_background));
                stateButton.setTextColor(getResources().getColor(android.R.color.white));
                stateButton.setOnClickListener(v -> {
                    Intent intent = new Intent(OwnerActivity.this, OwnerDetailsActivity.class);
                    intent.putExtra("stateId", stateKey); // Передаем идентификатор государства
                    startActivity(intent); // Запускаем новую Activity
                });

                linearLayoutStates.addView(stateButton);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}