package com.example.galacticmape.Owner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.galacticmape.R;

import org.json.JSONObject;

import java.io.InputStream;
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
            // Читаем JSON файл с государствами
            InputStream inputStream = getAssets().open("statesList.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);

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