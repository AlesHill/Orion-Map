package com.example.galacticmape.Owner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.galacticmape.R;

import org.json.JSONObject;
import java.io.InputStream;
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
            // Чтение файла statesList.json из папки assets
            InputStream inputStream = getAssets().open("statesList.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String jsonData = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(jsonData);

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